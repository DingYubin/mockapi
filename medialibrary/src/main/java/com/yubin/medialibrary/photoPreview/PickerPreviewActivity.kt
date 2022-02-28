package com.yubin.medialibrary.photoPreview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.imagepipeline.image.ImageInfo
import com.komi.slider.SliderConfig
import com.komi.slider.SliderUtils
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.CMStatusBarUtil
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.been.ImagePickerBean
import com.yubin.medialibrary.album.cache.ImageLoaderManager
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.databinding.ActivityPickerPreviewBinding
import com.yubin.medialibrary.manager.AlbumStrategy
import com.yubin.medialibrary.manager.MediaInfo
import com.yubin.medialibrary.util.BitmapCompressUtil
import com.yubin.medialibrary.util.MD5Util
import com.yubin.medialibrary.util.MediaUtil
import com.yubin.medialibrary.video.VideoPlayActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.relex.photodraweeview.OnPhotoTapListener
import me.relex.photodraweeview.PhotoDraweeView
import java.io.File

/**
 *description
 *
 *@author laiwei
 *@date create at 4/25/21 2:50 PM
 */
@SuppressLint("NotifyDataSetChanged")
class PickerPreviewActivity : NativeActivity<ActivityPickerPreviewBinding>() {
    private var mPageIndexText: TextView? = null

    private var mIndex = 0
    private lateinit var mImageUrlList: ArrayList<ImagePickerBean>
    private var mList: ArrayList<ImagePickerBean> = ArrayList()

    /**
     * 以下四个参数用于当确定时，压缩图片所用
     */
    private var imageFileTypeName = "png"
    private var adapter: ImagePreviewAdapter? = null
    private lateinit var strategy: AlbumStrategy

    companion object {
        const val IMAGE_INDEX = "image_index"
        const val IMAGE_LIST = "image_list"
    }

    override fun getViewBinding(): ActivityPickerPreviewBinding =
        ActivityPickerPreviewBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
        CMStatusBarUtil.setStatusColor(this, false, true, Color.WHITE)
        initParams()
        initUiStyle()
        initSlider()
        obtainData()
        initViewListener()
    }

    private fun initParams() {
        strategy = intent.getSerializableExtra("strategy") as AlbumStrategy
        imageFileTypeName = strategy.fileType.name
    }

    private fun initUiStyle() {
        MediaManager.instance.mediaStyle?.let {
            binding.sendPhoto.setTextColor(ContextCompat.getColor(this, it.sendTextColor))
            binding.sendPhoto.setBackgroundResource(it.sendTextBg)
            binding.pageText.setBackgroundResource(it.previewLabelImage)
        }
    }

    private fun obtainData() {
        val intent = intent
        if (intent != null) {
            mIndex = intent.getIntExtra(IMAGE_INDEX, 0)
            mImageUrlList =
                intent.getSerializableExtra(IMAGE_LIST) as ArrayList<ImagePickerBean>
            mList = mImageUrlList
            if (mIndex != 0 && mIndex >= mImageUrlList.size) {
                mIndex = mImageUrlList.size - 1
            }
            if (mList.isNotEmpty()) {
                mList.forEachIndexed { index, _ ->
                    run {
                        mList[index].isSelect = index == 0
                    }
                }
            }
        }
    }

    private fun initViewListener() {
        if (mImageUrlList.isEmpty()) {
            binding.pageText.visibility = View.INVISIBLE
        }
        val mViewPager: MultiTouchViewPager = findViewById(R.id.viewPager)
        //返回按钮点击事件
        binding.closeBack.setOnClickListener { finish() }

        //确定按钮点击事件
        binding.sendPhoto.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 1500) {
                return@setOnClickListener
            }
            lastClickTime = currentTime
            finishWithImageUrls()
        }
        if (mList.size == 0) {
            binding.pageText.visibility = View.GONE
        } else {
            binding.pageText.visibility = View.VISIBLE
            binding.pageText.text = (mViewPager.currentItem + 1).toString()
        }

        binding.recyclerViewSmallImages.layoutManager =
            LinearLayoutManager(this@PickerPreviewActivity, RecyclerView.HORIZONTAL, false)
        adapter = ImagePreviewAdapter(object : ImagePreviewListener {
            override fun imagePreviewSelect(bean: ImagePickerBean, position: Int) {
                val originalSelect = bean.isSelect
                if (!originalSelect) {
                    mList.forEach {
                        it.isSelect = false
                    }
                    bean.isSelect = !originalSelect
                }
                if (position != mViewPager.currentItem) {
                    mViewPager.currentItem = position
                }
                binding.pageText.text = (mViewPager.currentItem + 1).toString()
                adapter?.notifyDataSetChanged()
            }

        })
        binding.recyclerViewSmallImages.adapter = adapter
        adapter?.submitList(mList)
        val mPageAdapter: PagerAdapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return mImageUrlList.size
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val image = mImageUrlList[position]
                if (image.isVideo) {
                    val view =
                        layoutInflater.inflate(R.layout.item_video_preview, container, false)
                    val mImageUrlView = view.findViewById<AppCompatImageView>(R.id.videoThumb)
                    val playView = view.findViewById<AppCompatImageView>(R.id.videoPlay)
                    ImageLoaderManager.INSTANCE.intoTarget(
                        image.thumbData,
                        image.data,
                        mImageUrlView
                    )
                    playView.onViewClick {
                        //去播放视频
                        val intent = Intent()
                        intent.setClass(
                            this@PickerPreviewActivity,
                            VideoPlayActivity::class.java
                        )
                        intent.putExtra("url", image.data)
                        startActivity(intent)
                    }
                    try {
                        container.addView(
                            view,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return view
                }
                val photoDraweeView = PhotoDraweeView(this@PickerPreviewActivity)
                var imagePath = image.data ?: ""
                imagePath = when {
                    TextUtils.isEmpty(imagePath) -> {
                        ""
                    }
                    imagePath.startsWith("http") -> {
                        imagePath
                    }
                    else -> {
                        "file://$imagePath"
                    }
                }
                /**
                 * 用于控制图片的缩放
                 */
                val controller =
                    Fresco.newDraweeControllerBuilder()
                controller.setUri(Uri.parse(imagePath))
                controller.oldController = photoDraweeView.controller
                controller.controllerListener = object :
                    BaseControllerListener<ImageInfo?>() {
                    override fun onFinalImageSet(
                        id: String,
                        imageInfo: ImageInfo?,
                        animatable: Animatable?,
                    ) {
                        super.onFinalImageSet(id, imageInfo, animatable)
                        if (imageInfo == null) {
                            return
                        }
                        photoDraweeView.update(imageInfo.width, imageInfo.height)
                    }
                }
                photoDraweeView.controller = controller.build()
                photoDraweeView.onPhotoTapListener =
                    OnPhotoTapListener { _: View?, _: Float, _: Float -> finish() }
                try {
                    container.addView(
                        photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return photoDraweeView
            }

            override fun destroyItem(
                container: ViewGroup,
                position: Int,
                `object`: Any,
            ) {
                container.removeView(`object` as View)
            }
        }
        mViewPager.adapter = mPageAdapter
        mViewPager.addOnPageChangeListener(object : OnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (mImageUrlList.size > 1) {
                    mPageIndexText?.text = mImageUrlList.size.toString()
                    mList.forEach {
                        it.isSelect = false
                    }
                    mList[position].isSelect = true
                    adapter?.notifyDataSetChanged()
                    binding.pageText.text = (position + 1).toString()
                }
            }

        })
        mViewPager.currentItem = mIndex
        if (mIndex == 0 && mImageUrlList.isNotEmpty()) {
            mPageIndexText?.text = mImageUrlList.size.toString()
        }
        if (mImageUrlList.isNotEmpty()) {
            binding.sendPhoto.text =
                String.format(strategy.selectedBtnText + " (%s)", mImageUrlList.size)
        }
    }


    /**
     * 设置滑出页面时的效果
     */
    private fun initSlider() {
        val mConfig = SliderConfig.Builder()
            .secondaryColor(Color.TRANSPARENT) //
            .edge(false) //是否允许有滑动边界值,默认是有的true
            .sensitivity(0.1f) //控制滑出事件的感应的灵敏度
            .slideEnter(false)
            .slideExit(false)
            .build()
        SliderUtils.attachActivity(this, mConfig)
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
            mImageUrlList.forEach {
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
}