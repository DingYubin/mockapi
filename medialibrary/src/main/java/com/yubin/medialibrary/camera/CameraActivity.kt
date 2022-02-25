package com.yubin.medialibrary.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera.PictureCallback
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.widget.ToastUtil
import com.yubin.medialibrary.R
import com.yubin.medialibrary.databinding.ActivityCameraBinding
import com.yubin.medialibrary.manager.CameraStrategy
import com.yubin.medialibrary.manager.MediaInfo
import com.yubin.medialibrary.util.BitmapCompressUtil
import com.yubin.medialibrary.util.CameraHelper
import com.yubin.medialibrary.util.MD5Util
import com.yubin.medialibrary.util.UnitHelper
import kotlinx.coroutines.*
import java.io.File

/**
 *description 拍照页面
 *
 *@author yubin
 *@date create at 4/24/21 2:11 PM
 *email
 */
@Route(path = RouterPath.MediaPage.PATH_MEDIA_CAMERA)
class CameraActivity : NativeActivity<ActivityCameraBinding>(),
    CoroutineScope by MainScope() {

    companion object {
        const val REQUEST_CODE = 0x101
    }

    private var mCameraHelper: CameraHelper? = null
    private var maxWidth = 1080
    private var maxHeight = 1920
    private var safeToTakePicture = false

    private val mTitle = "camera"
    private val imageFileType = "png"

    private lateinit var strategy: CameraStrategy
    private var resolver: ContentResolver? = null
    private var cameraViewModel: CameraViewModel? = null

    /**
     * 当前拍照bitmap
     */
    private var bitmap: Bitmap? = null

    override fun getViewBinding(): ActivityCameraBinding =
        ActivityCameraBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (null != supportActionBar) {
            supportActionBar!!.hide()
        }
        //全屏显示
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
        /*
         * 检查是否有相机,读写,权限
         */if (selfPermissionGranted(this, Manifest.permission.CAMERA) &&
            selfPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
            selfPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            this.initParams()
            this.initUiStyle()
            this.addEventListener()
            this.initOneAlbum()
            this.initCamera()
        } else {
            MediaManager.instance.toast.invoke(
                getString(R.string.permission_message_dialog_default),
                this
            )
            finish()
        }

    }

    private fun addEventListener() {
        /**
         * 关闭界面
         */
        binding.cameraClose.onViewClick {
            this.finish()
        }
        /**
         * 切换前后摄像头
         */
        binding.cameraSwitch.onViewClick {
            if (null == mCameraHelper || null == mCameraHelper!!.getCamera()) {
                return@onViewClick
            }
            mCameraHelper!!.exchangeCamera()
        }
        /**
         * 拍摄照片
         */
        binding.takePhoto.onViewClick {
            if (null == mCameraHelper || null == mCameraHelper!!.getCamera()) {
                return@onViewClick
            }
            if (safeToTakePicture) {
                try {
                    mCameraHelper!!.getCamera()!!.takePicture(null, null, mPictureCallback)
                    safeToTakePicture = false

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.flash.onViewClick {
            if (null == mCameraHelper || null == mCameraHelper!!.getCamera()) {
                return@onViewClick
            }

            switchFlash()
        }

        /**
         * 跳转相册
         */
        binding.goPhotoAlbum.onViewClick {
//            val intent = Intent(this, ImagePickerActivity::class.java)
//            intent.putExtra("strategy", strategy)
//            startActivityForResult(intent, REQUEST_CODE)
        }
        /**
         * 重新拍摄
         */
        binding.reTakePhoto.onViewClick {
            mCameraHelper!!.startPreview()
            binding.reTakeGroup.visibility = View.GONE
            binding.takeGroup.visibility = View.VISIBLE
        }
        /**
         * 发送照片
         */
        binding.sendPhoto.onViewClick {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 1500) {
                return@onViewClick
            }
            val dismissFunction =
                MediaManager.instance.showLoading(this@CameraActivity, "", true)
            GlobalScope.launch {
                var newPath: String? = null
                withContext(Dispatchers.IO) {
                    lastClickTime = currentTime
                    val file = getExternalFilesDir("imagePicker") ?: return@withContext
                    if (!file.exists()) {
                        file.mkdirs()
                    }
                    val destPath = file.absolutePath
                    val name: String = MD5Util.toMd5(mTitle)
                        .toString() + "_" + System.currentTimeMillis() + "." + imageFileType
                    newPath = BitmapCompressUtil.compressBitmap2File(
                        bitmap,
                        destPath + File.separator + name,
                        strategy.fileType
                    )
                }
                withContext(Dispatchers.Main) {
                    dismissFunction()
                    if (newPath?.isNotEmpty() == true) {
                        val medias = ArrayList<MediaInfo>()
                        medias.add(MediaInfo(isVideo = false, uri = "file://$newPath"))
                        MediaManager.instance.callBack?.result(medias)
                        finish()
                    } else {
                        MediaManager.instance.toast(
                            getString(R.string.compress_image_fail),
                            this@CameraActivity
                        )
                    }
                }

            }
        }
    }


    override fun onPause() {
        super.onPause()
        if (null != mCameraHelper) {
            mCameraHelper!!.stopPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        if (null != mCameraHelper) {
            mCameraHelper!!.startPreview()
            safeToTakePicture = true
        }
    }

    override fun onNewDestroy() {
        super.onNewDestroy()
        if (null != mCameraHelper) {
            mCameraHelper!!.releaseCamera()
        }
        bitmap?.let {
            if (!it.isRecycled) {
                it.recycle()
            }
        }
    }

    private fun initCamera() {
        binding.cameraSurface.visibility = View.VISIBLE
        maxWidth = resources.displayMetrics.widthPixels
        maxHeight = resources.displayMetrics.heightPixels
        mCameraHelper =
            CameraHelper(this@CameraActivity, binding.cameraSurface, maxWidth, maxHeight)
        mCameraHelper!!.startPreview()
    }

    /**
     * 显示一张相册图片
     */
    @SuppressLint("Range")
    private fun initOneAlbum() {
        val mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        GlobalScope.launch(Dispatchers.IO) {
            resolver = contentResolver
            val cursor = resolver?.query(
                mImageUri, arrayOf(
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
                ),
                null,
                null,
                MediaStore.Images.ImageColumns.DATE_MODIFIED + " desc"
            )
            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return@launch
                }
                val data =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                val bitmap = BitmapCompressUtil.decodeBitmapFromFile(
                    data,
                    UnitHelper.dp2px(binding.goPhotoAlbum.context, 52F).toInt()
                )
                cursor.close()
                cameraViewModel?.fetchMediaResult?.postValue(bitmap)
            }

        }
    }

    private fun initParams() {
        strategy = intent.getSerializableExtra("strategy") as CameraStrategy
        cameraViewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        cameraViewModel?.fetchMediaResult?.observe(this) {
            it?.let {
                binding.goPhotoAlbum.setImageBitmap(it)
            }
        }
    }

    private fun initUiStyle() {
        MediaManager.instance.mediaStyle?.let {
            binding.sendPhoto.setTextColor(ContextCompat.getColor(this, it.sendTextColor))
            binding.sendPhoto.setBackgroundResource(it.sendTextBg)
            binding.sendPhoto.text = strategy.selectedBtnText
        }
    }

    private var mPictureCallback = PictureCallback { data, _ ->
        launch {
            mCameraHelper!!.getCamera()
            bitmap = BitmapCompressUtil.compressData2Bitmap(
                data,
                mCameraHelper!!.getDisplayOrientation()
            )
            withContext(Dispatchers.Main) {
                if (bitmap == null) {
                    MediaManager.instance.toast.invoke("拍照失败", this@CameraActivity)
                    return@withContext
                }
                mCameraHelper!!.closeFlash()
//                mCameraHelper!!.stopPreview()
                binding.reTakeGroup.visibility = View.VISIBLE
                binding.takeGroup.visibility = View.GONE
            }
            safeToTakePicture = true
        }
    }

    // 将移动，缩放以及旋转后的图层保存为新图片
    // 本例中沒有用到該方法，需要保存圖片的可以參考
    fun createCropBitmap(width : Int, height : Int, bitmap : Bitmap?, x:Int?, y:Int?): Bitmap? {
        if (bitmap == null || x == null || y == null) return null

        return Bitmap.createBitmap(
            bitmap,
            x,
            y,
            width,
            height
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            finish()
        }
    }

    /**
     * 开闭闪光灯
     */
    private fun switchFlash() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            val mess = resources.getString(R.string.scan_toast_flash)
            ToastUtil.showToast(mess, Toast.LENGTH_SHORT)
            return
        }

        if (mCameraHelper?.isOpenFlash() == true) {
            mCameraHelper?.closeFlash()
        } else {
            mCameraHelper?.openFlash()
        }
    }

}