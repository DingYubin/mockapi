package com.yubin.medialibrary.album

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.facebook.drawee.backends.pipeline.Fresco
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.CMStatusBarUtil
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.been.ImagePickerBean
import com.yubin.medialibrary.album.cache.ImageLoaderManager
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.databinding.ActivityPhotoAlbumBinding
import com.yubin.medialibrary.manager.AlbumStrategy
import com.yubin.medialibrary.manager.MediaInfo
import com.yubin.medialibrary.photoPreview.PickerPreviewActivity
import com.yubin.medialibrary.photoPreview.PickerPreviewActivity.Companion.IMAGE_LIST
import com.yubin.medialibrary.util.BitmapCompressUtil
import com.yubin.medialibrary.util.MD5Util
import com.yubin.medialibrary.util.MediaUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


/**
 *description
 *
 *@date create at 4/24/21 2:32 PM
 */

open class ImagePickerActivity : NativeActivity<ActivityPhotoAlbumBinding>() {

    companion object {
        const val FRAGMENT_TAG_FOLDER = "image_folder"
        const val IMAGE_PREVIEW_REQUEST = 0x101

        const val IMAGE_LAST_FOLDER = "image_last_folder"
    }

    private var resolver: ContentResolver? = null

    /**
     * 已选择图片列表
     */
    private val mSelectList: ArrayList<ImagePickerBean> = arrayListOf()

    /**
     * 全部图片列表
     */
    private var mImages: ArrayList<ImagePickerBean>? = null

    /**
     * 图片信息按文件夹分类列表
     */
    private var mFolders: HashMap<String, ArrayList<ImagePickerBean>>? = null

    /**
     * 文件夹列表
     */
    private var mFolderList: ArrayList<ImagePickerBean>? = null

    /**
     * 上次退出时所在的文件夹
     */
    private var lastFolder: String? = null

    /**
     * 当前文件夹所在的序号
     */
    private var lastIndex = 0

    /**
     * 最多可选择的图片数量
     */
    private var maxSelectNumber = 9
    private var imageFileTypeName = "png"

    private lateinit var video: String
    private lateinit var all: String
    private lateinit var other: String
    private lateinit var strategy: AlbumStrategy
    private var imagePickerViewModel: ImagePickerViewModel? = null

    override fun getViewBinding() = ActivityPhotoAlbumBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        CMStatusBarUtil.setStatusColor(this, false, true, Color.WHITE)
        this.initUiStyle()
        /*
         * 检查是否有读写权限
         */if (selfPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
            selfPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            ImageLoaderManager.INSTANCE.initThreadPoolExecutor(64, this)
            this.initParams()
            this.fetchImageData()
            this.initViewListener()
        } else {
            fetchImageData()
            MediaManager.instance.toast.invoke(
                getString(R.string.permission_message_dialog_default),
                this
            )
        }

    }

    private fun initParams() {
        strategy = intent.getSerializableExtra("strategy") as AlbumStrategy
        maxSelectNumber = strategy.maxCount
        imageFileTypeName = strategy.fileType.name
        this.showSendCount()
        imagePickerViewModel = ViewModelProvider(this).get(ImagePickerViewModel::class.java)
        imagePickerViewModel?.fetchMediaResult?.observe(this) {
            setImagePickerState()
        }
    }

    private fun showSendCount() {
        binding.sendPhoto.text = getString(R.string.selected_text,strategy.selectedBtnText,mSelectList.size,maxSelectNumber)
    }

    private fun initViewListener() {
        /**
         * 去预览照片
         */
        binding.selectPreview.onViewClick { it ->
            if (mSelectList.isEmpty()) {
                MediaManager.instance.toast.invoke(
                    getString(R.string.image_picker_not_select_photo),
                    this@ImagePickerActivity
                )
                return@onViewClick
            }
            val intent = Intent()
            it?.context?.let { intent.setClass(it, PickerPreviewActivity::class.java) }
            intent.putExtra(PickerPreviewActivity.IMAGE_INDEX, 0)
            intent.putExtra(IMAGE_LIST, mSelectList)
            intent.putExtra("strategy", strategy)
            startActivityForResult(intent, IMAGE_PREVIEW_REQUEST)
        }
        /**
         * 发送照片
         */
        binding.sendPhoto.onViewClick {
            if (mSelectList.isNullOrEmpty()) {
                MediaManager.instance.toast.invoke(
                    getString(R.string.image_picker_not_select_photo),
                    this@ImagePickerActivity
                )
            } else {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < 1500) {
                    return@onViewClick
                }
                lastClickTime = currentTime
                finishWithImageUrls()
            }
        }
        /**
         * 取消关闭当前页面
         */
        binding.cancel.onViewClick { finish() }
        /**
         * 切换相册
         */
        binding.albumTitle.onViewClick {
            val fm = supportFragmentManager
            val bt = fm.beginTransaction()
            var fragment = fm.findFragmentByTag(FRAGMENT_TAG_FOLDER)
            if (null == fragment) {
                setFolderSelectViewModel()
                fragment = ImagePickerFolderFragment.newInstance(mFolderList!!, lastIndex)
                bt.add(R.id.activity_cec_image_picker_folder, fragment, FRAGMENT_TAG_FOLDER)
                bt.commit()
            } else {
                showOrHideFragment()
            }
            updateArrow()
        }
    }

    /**
     * 设置文件夹Folder点击通信
     */
    private fun setFolderSelectViewModel() {
        imagePickerViewModel?.folderMutableLiveData?.observe(
            this,
            androidx.lifecycle.Observer {
                showOrHideFragment()
                if (TextUtils.isEmpty(it)) {
                    return@Observer
                }
                if (null == mFolders) {
                    return@Observer
                }
                val list = mFolders!![it]
                if (all == it) {
                    binding.albumTitle.text = it
                } else if (list?.isNotEmpty() == true) {
                    binding.albumTitle.text = list[0].bucketDisplayName
                }
                updateArrow()
                binding.imagePickerView.setData(list, mSelectListener)
            })
    }

    private fun updateArrow() {
        val fm = supportFragmentManager
        val fragment = fm.findFragmentByTag(FRAGMENT_TAG_FOLDER)
        val drawable: Drawable? = if (fragment?.isHidden == false) {
            ContextCompat.getDrawable(this, R.drawable.icon_album_bottom)
        } else {
            ContextCompat.getDrawable(this, R.drawable.icon_album_top)
        }
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable?.let {
            binding.albumTitle.setCompoundDrawables(null, null, drawable, null)
        }
    }

    /**
     * 显示或隐藏文件夹Fragment
     */
    private fun showOrHideFragment() {
        val fm = supportFragmentManager
        val bt = fm.beginTransaction()
        val fragment = fm.findFragmentByTag(FRAGMENT_TAG_FOLDER) ?: return
        if (fragment.isHidden) {
            bt.show(fragment)
        } else {
            bt.hide(fragment)
        }
        bt.commit()
    }

    private fun initUiStyle() {
        all = getString(R.string.all_photo)
        video = getString(R.string.video)
        other = getString(R.string.other)
        MediaManager.instance.mediaStyle?.let {
            binding.sendPhoto.setTextColor(ContextCompat.getColor(this, it.sendTextColor))
            binding.sendPhoto.setBackgroundResource(it.sendTextBg)
        }
    }

    /**
     * 获取手机里所有图片信息
     */
    private fun fetchImageData() {
        val mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        GlobalScope.launch(Dispatchers.IO) {
            resolver = contentResolver
            val cursor = resolver?.query(
                mImageUri, arrayOf(
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATE_MODIFIED,
                ),
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_MODIFIED + " desc"
            )
            if (cursor != null) {
                val mStringObjectMap = parsingImagesList(cursor)
                mImages = mStringObjectMap["images"] as ArrayList<ImagePickerBean>?
                mFolders =
                    mStringObjectMap["folders"] as HashMap<String, ArrayList<ImagePickerBean>>?
                mFolderList = mStringObjectMap["folderList"] as ArrayList<ImagePickerBean>?
            } else {
                mImages = arrayListOf()
                mFolders = hashMapOf()
                mFolderList = arrayListOf()
            }

            if (strategy.isShowVideo) {
                val videoCursor = resolver?.query(
                    mVideoUri, arrayOf(
                        MediaStore.Video.VideoColumns._ID,
                        MediaStore.Video.VideoColumns.DATA,
                        MediaStore.Video.VideoColumns.DISPLAY_NAME,
                        MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Video.VideoColumns.SIZE,
                        MediaStore.Video.VideoColumns.DURATION,
                        MediaStore.Video.VideoColumns.DATE_MODIFIED,
                    ),
                    null,
                    null,
                    MediaStore.Video.VideoColumns.DATE_MODIFIED + " desc"
                )
                parsingVideosList(videoCursor)
            } else {
                parsingVideosList(null)
            }
            if (lastIndex >= mFolderList?.size ?: 0 || lastIndex < 0) {
                lastIndex = 0
            }
            mFolderList?.get(lastIndex)?.isFolderSelect = true
            imagePickerViewModel?.fetchMediaResult?.postValue("SUCCESS")
        }
    }

    /**
     * 获取解析图片列表，按所在文件夹分类
     */
    @SuppressLint("Range")
    private fun parsingImagesList(cursor: Cursor?): Map<String, Any> {
        lastFolder = MediaManager.instance.getValue(IMAGE_LAST_FOLDER)
        val map: MutableMap<String, Any> = HashMap()
        if (null == cursor || !cursor.moveToFirst()) {
            map["img"] = emptyList<Any>()
            return map
        }
        val images: ArrayList<ImagePickerBean> = arrayListOf()
        val folders: HashMap<String, MutableList<ImagePickerBean>> = hashMapOf()
        val folderList: ArrayList<ImagePickerBean> = arrayListOf()

        //默认初始化一个所有的图片
        val allImage = ImagePickerBean()
        allImage.parentFolder = all
        allImage.displayName = all
        allImage.bucketDisplayName = all
        folders[all] = images
        folderList.add(allImage)
        //默认添加一个视频文件，先占位
        folderList.add(allImage)
        //默认添加一个视频文件，先占位
        do {
            val image = ImagePickerBean()
            image.maxSelectNumber = maxSelectNumber
            image.bucketDisplayName =
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
            image.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            image.dateModified =
                cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED))
            image.displayName =
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
            val data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
            image.data = data
            //如果返回父路径为空，则统一设置为`其他`
            val parentFolder = File(data).parent ?: other
            image.parentFolder = parentFolder
            images.add(image)
            if (folders.containsKey(parentFolder)) {
                val ret: MutableList<ImagePickerBean>? =
                    folders[parentFolder]
                if (null != ret) {
                    ret.add(image)
                    ret[0].currentFolderImageNumber = ret[0].currentFolderImageNumber + 1
                }
            } else {
                val fList: MutableList<ImagePickerBean> = mutableListOf()
                fList.add(image)
                image.currentFolderImageNumber = 1
                folders[parentFolder] = fList
                folderList.add(image)
                if (lastFolder == parentFolder) {
                    lastIndex = folderList.size - 1
                }
            }
        } while (cursor.moveToNext())
        cursor.close()
        //最后设置所有图片的数据
        allImage.currentFolderImageNumber = images.size
        allImage.data = images[0].data

        map["images"] = images
        map["folders"] = folders
        map["folderList"] = folderList
        return map
    }

    /**
     * 获取解析视频列表
     */
    @SuppressLint("Range")
    private fun parsingVideosList(cursor: Cursor?) {
        val videos: ArrayList<ImagePickerBean> = arrayListOf()
        if (null == cursor || !cursor.moveToFirst()) {
            //do nothing
        } else {
            do {
                val image = ImagePickerBean()
                image.isVideo = true
                strategy.maxVideoSize.let {
                    image.maxVideoSize = it
                }
                image.maxSelectNumber = maxSelectNumber
                image.bucketDisplayName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME))
                image.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID))
                image.dateModified =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_MODIFIED))
                image.displayName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME))
                val data =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA))
                image.data = data
                val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE))
                image.size = size
                val duration =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION))
                image.duration = duration

                val thumbColumns = arrayOf(
                    MediaStore.Video.Thumbnails.DATA,
                    MediaStore.Video.Thumbnails.VIDEO_ID,
                )
                val selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                val selectionArgs = arrayOf(image.id.toString() + "")
                val thumbCursor: Cursor? = resolver?.query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns,
                    selection,
                    selectionArgs,
                    null
                )
                var thumbPath: String? = null
                if (thumbCursor != null && thumbCursor.moveToFirst()) {
                    thumbPath =
                        thumbCursor.getString(thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA))
                }
                thumbCursor?.close()
                image.thumbData = thumbPath
                if (duration > 0) {
                    videos.add(image)
                }
            } while (cursor.moveToNext())
            cursor.close()
            mFolders?.set(video, videos)
        }
        if (videos.isNullOrEmpty()) {
            if (mFolderList?.size ?: 0 >= 2) {
                mFolderList?.removeAt(1)
                lastIndex--
            }
        } else {
            //将视频加入到图片之中
            mFolders?.get(all)?.addAll(videos)
            mFolders?.get(all)?.let { list ->
                list.sortBy {
                    -it.dateModified
                }
            }
            //将视频加入到图片之中
            videos[0].currentFolderImageNumber = videos.size
            videos[0].parentFolder = video
            videos[0].bucketDisplayName = video
            if (mFolderList?.size ?: 0 >= 2) {
                mFolderList?.set(1, videos[0])
            } else {
                mFolderList?.add(videos[0])
            }
            if (lastFolder == video) {
                lastIndex = 1
            }
        }
    }


    private val mSelectListener: ImageSelectListener = object : ImageSelectListener {
        override fun onImageSelected(pickerBean: ImagePickerBean, isSelect: Boolean): Int {
            if (isSelect) {
                mSelectList.add(pickerBean)
                pickerBean.badge = mSelectList.size
            } else {
                val ret = mSelectList.indexOf(pickerBean)
                if (ret >= 0) {
                    mSelectList.remove(pickerBean)
                    for (index in ret until mSelectList.size) {
                        mSelectList[index].badge = mSelectList[index].badge - 1
                    }
                    setImagePickerOkState(mSelectList.size)
                    return ret
                }
            }
            setImagePickerOkState(mSelectList.size)
            return mSelectList.size
        }
    }

    /**
     * 设置确定按钮显示状态
     */
    fun setImagePickerOkState(number: Int) {
        this.showSendCount()
        binding.sendPhoto.isEnabled = number > 0
        if (number > 0) {
            binding.selectPreview.setTextColor(resources.getColor(R.color.color_e6000000))
        } else {
            binding.selectPreview.setTextColor(resources.getColor(R.color.color_4d000000))
        }
    }

    /**
     * 设置图片列表状态
     */
    private fun setImagePickerState() {
        //如果没有视频和图片  显示空状态图
        if (mImages.isNullOrEmpty() && mFolders?.get(video).isNullOrEmpty()) {
            val view: View = binding.imagePickerNoPhoto.inflate()
            val noPhotoCancel: AppCompatTextView? = view.findViewById(R.id.text_cancel)
            noPhotoCancel?.onViewClick({ finish() })
            return
        }
        val lastList: List<ImagePickerBean>? = mFolders!![lastFolder]
        if (null == lastList) {
            binding.imagePickerView.setData(mImages, mSelectListener)
            lastIndex = 0
            lastFolder = mFolderList!![0].parentFolder
            GlobalScope.launch {
                MediaManager.instance.saveValue(IMAGE_LAST_FOLDER, lastFolder.toString())
            }
        } else {
            if (all == lastFolder) {
                binding.albumTitle.text = lastFolder
            } else if (lastList.isNotEmpty()) {
                binding.albumTitle.text = lastList[0].bucketDisplayName
            }
            binding.imagePickerView.setData(lastList, mSelectListener)
        }
    }

    /**
     * 压缩图片并返回
     */
    private fun finishWithImageUrls() {
        val file = getExternalFilesDir("imagePicker") ?: return
        if (!file.exists()) {
            file.mkdirs()
        }
        val destPath = file.absolutePath
        val dismissFunction = MediaManager.instance.showLoading(this, "", true)
        GlobalScope.launch(Dispatchers.IO) {
            val medias = arrayListOf<MediaInfo>()
            mSelectList.forEach {
                if (it.isVideo) {
                    medias.add(
                        MediaInfo(
                            isVideo = true,
                            duration = it.duration / 1000,
                            size = it.size,
                            uri = "file://${it.data}",
                            thumb = MediaUtil.encodeBitmap2String(
                                MediaUtil.decodeVideoThumb(
                                    it.data
                                )
                            )
                        )
                    )
                } else {
                    if (strategy.quality == AlbumStrategy.QUALITY_ORIGIN) {
                        medias.add(MediaInfo(isVideo = false, uri = "file://${it.data}"))
                    } else {
                        val name = MD5Util.toMd5(it.data).toString() + "." + imageFileTypeName
                        val newPath: String? = BitmapCompressUtil.compressBitmapFile(
                            it.data,
                            destPath + File.separator + name,
                            imageFileTypeName,
                            strategy.maxWidth,
                            strategy.maxHeight,
                            strategy.quality
                        )
                        if (!TextUtils.isEmpty(newPath)) {
                            medias.add(MediaInfo(isVideo = false, uri = "file://$newPath"))
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                dismissFunction()
                MediaManager.instance.callBack?.result(medias)
                setResult(RESULT_OK)
                finish()
            }
        }
    }


    override fun onBackPressed() {
        val fm = supportFragmentManager
        val bt = fm.beginTransaction()
        val fragment =
            fm.findFragmentByTag(FRAGMENT_TAG_FOLDER)
        if (null == fragment || fragment.isHidden) {
            super.onBackPressed()
            return
        }
        bt.hide(fragment)
        bt.commit()
    }

    override fun onNewDestroy() {
        super.onNewDestroy()
        ImageLoaderManager.INSTANCE.release()
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_PREVIEW_REQUEST) {
            setResult(RESULT_OK)
            finish()
        }
    }

}

