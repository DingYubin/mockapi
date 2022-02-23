package com.yubin.medialibrary.util


import android.app.Activity
import android.graphics.ImageFormat
import android.graphics.Point
import android.hardware.Camera
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import java.io.IOException

/**
 *description
 *
 *@author laiwei
 *@date create at 4/24/21 8:58 AM
 */
class CameraHelper(activity: Activity, surfaceView: SurfaceView) {

    private val TAG = javaClass.name

    private val MIN_PREVIEW_PIXELS = 480 * 320 // normal screen
    private val MAX_ASPECT_DISTORTION = 0.15

    private var mCamera: Camera? = null                   //Camera对象
    private lateinit var mParameters: Camera.Parameters   //Camera对象的参数
    private var mSurfaceView: SurfaceView = surfaceView   //用于预览的SurfaceView对象
    var mSurfaceHolder: SurfaceHolder = mSurfaceView.holder                     //SurfaceHolder对象

    var mPreviewCallback: Camera.PreviewCallback? = null

    private var mActivity: Activity = activity

    var mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK  //摄像头方向
    var mDisplayOrientation: Int = 0    //预览旋转的角度

    private var picWidth = 2160        //保存图片的宽
    private var picHeight = 3840       //保存图片的高

    private var mCustomWidth: Int = 0
    private var mCustomHeight: Int = 0

    init {
        init()
    }

    constructor(
        activity: Activity,
        surfaceView: SurfaceView,
        previewCallback: Camera.PreviewCallback
    ) : this(activity, surfaceView) {
        mPreviewCallback = previewCallback
    }

    constructor(
        activity: Activity,
        surfaceView: SurfaceView,
        customWidth: Int,
        customHeight: Int
    ) : this(activity, surfaceView) {
        mCustomWidth = customWidth
        mCustomHeight = customHeight
    }

    private fun init() {
        mSurfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                //surfaceChanged
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                releaseCamera()
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                if (mCamera == null) {
                    openCamera(mCameraFacing)
                }
                startPreview()
            }
        })
    }

    //打开相机
    fun openCamera(cameraFacing: Int = Camera.CameraInfo.CAMERA_FACING_BACK): Boolean {
        val supportCameraFacing = supportCameraFacing(cameraFacing)
        if (supportCameraFacing) {
            try {
                mCamera = Camera.open(cameraFacing)
                initParameters(mCamera!!)
                mCamera?.setPreviewCallback(mPreviewCallback)
            } catch (e: Exception) {
                e.printStackTrace()
                toast("打开相机失败!")
                return false
            }
        }
        return supportCameraFacing
    }

    //配置相机参数
    private fun initParameters(camera: Camera) {
        try {
            mParameters = camera.parameters
            mParameters.previewFormat = ImageFormat.NV21

            //获取与指定宽高相等或最接近的尺寸
            val screenSize = Point()
            mActivity.windowManager.defaultDisplay.getSize(screenSize)
            val bestPreviewSize = if (mCustomWidth == 0) {
                getBestSize(
                    mSurfaceView.width,
                    mSurfaceView.height,
                    mParameters.supportedPreviewSizes
                )
            } else {
                getBestSize(mCustomWidth, mCustomHeight, mParameters.supportedPreviewSizes)
            }
            mParameters.setPreviewSize(bestPreviewSize.x, bestPreviewSize.y)
            //设置保存图片尺寸
            val bestPicSize = if (mCustomWidth == 0) {
                getBestSize(picWidth, picHeight, mParameters.supportedPictureSizes)
            } else {
                getBestSize(mCustomWidth, mCustomHeight, mParameters.supportedPictureSizes)
            }
            mSurfaceView.layoutParams.height =
                mSurfaceView.context.resources.displayMetrics.widthPixels * bestPicSize.x / bestPicSize.y
            mParameters.setPictureSize(bestPicSize.x, bestPicSize.y)
            //对焦模式
            if (isSupportFocus(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                mParameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE

            camera.parameters = mParameters
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //开始预览
    fun stopPreview() {
        mCamera?.let {
            try {
                it.stopPreview()
                it.setPreviewDisplay(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //开始预览
    fun startPreview() {
        mCamera?.let {
            try {
                it.setPreviewDisplay(mSurfaceHolder)
                setCameraDisplayOrientation(mActivity)
                it.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    //判断是否支持某一对焦模式
    private fun isSupportFocus(focusMode: String): Boolean {
        var autoFocus = false
        val listFocusMode = mParameters.supportedFocusModes
        for (mode in listFocusMode) {
            if (mode == focusMode)
                autoFocus = true
        }
        return autoFocus
    }

    //切换摄像头
    fun exchangeCamera() {
        releaseCamera()
        mCameraFacing = if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK)
            Camera.CameraInfo.CAMERA_FACING_FRONT
        else
            Camera.CameraInfo.CAMERA_FACING_BACK

        openCamera(mCameraFacing)
        startPreview()
    }

    //释放相机
    fun releaseCamera() {
        if (mCamera != null) {
            mCamera?.stopPreview()
            mCamera?.setPreviewCallback(null)
            mCamera?.release()
            mCamera = null
        }
    }

    //设置预览旋转的角度
    fun setCameraDisplayOrientation(activity: Activity) {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(mCameraFacing, info)
        val rotation = activity.windowManager.defaultDisplay.rotation

        var screenDegree = 0
        when (rotation) {
            Surface.ROTATION_0 -> screenDegree = 0
            Surface.ROTATION_90 -> screenDegree = 90
            Surface.ROTATION_180 -> screenDegree = 180
            Surface.ROTATION_270 -> screenDegree = 270
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mDisplayOrientation = (info.orientation + screenDegree) % 360
            mDisplayOrientation =
                (360 - mDisplayOrientation) % 360          // compensate the mirror
        } else {
            mDisplayOrientation = (info.orientation - screenDegree + 360) % 360
        }
        mCamera?.setDisplayOrientation(mDisplayOrientation)
    }

    fun resetOrientation() {
        mCamera?.let {
            releaseCamera()
            restartCamera()
        }
    }

    fun restartCamera() {
        if (mCamera == null) {
            openCamera(mCameraFacing)
            startPreview()
        }
    }

    //判断是否支持某个相机
    private fun supportCameraFacing(cameraFacing: Int): Boolean {
        val info = Camera.CameraInfo()
        for (i in 0 until Camera.getNumberOfCameras()) {
            Camera.getCameraInfo(i, info)
            if (info.facing == cameraFacing) return true
        }
        return false
    }


    private fun toast(msg: String) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show()
    }

    fun getCamera(): Camera? = mCamera

    fun getDisplayOrientation(): Int = mDisplayOrientation


    fun openFlash() {
        try {
            val param: Camera.Parameters? = mCamera?.parameters
            param?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            mCamera?.parameters = param
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun closeFlash() {
        try {
            val param: Camera.Parameters? = mCamera?.parameters
            param?.flashMode = Camera.Parameters.FLASH_MODE_OFF
            mCamera?.parameters = param
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun findBestPreviewSizeValue(width: Int, height: Int, parameters: Camera.Parameters): Point? {
        Log.d(TAG, "target preview size : width = $width, height = $height")
        val rawSupportedSizes = parameters.supportedPreviewSizes
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default")
            val defaultSize = parameters.previewSize
                ?: throw IllegalStateException("Parameters contained no preview size!")
            return Point(defaultSize.width, defaultSize.height)
        }
        if (Log.isLoggable(TAG, Log.INFO)) {
            val previewSizesString = StringBuilder()
            for (size in rawSupportedSizes) {
                previewSizesString.append(size.width).append('x').append(size.height).append(' ')
            }
            Log.i(TAG, "Supported preview sizes: $previewSizesString")
        }
        val screenAspectRatio: Double = height.toDouble() / width.toDouble()

        // Find a suitable size, with max resolution
        var maxResolution = 0
        var maxResPreviewSize: Camera.Size? = null
        for (size in rawSupportedSizes) {
            val realWidth = size.width
            val realHeight = size.height
            val resolution = realWidth * realHeight
            if (resolution < MIN_PREVIEW_PIXELS) {
                continue
            }
            val isCandidatePortrait = realWidth < realHeight
            val maybeFlippedWidth = if (isCandidatePortrait) realHeight else realWidth
            val maybeFlippedHeight = if (isCandidatePortrait) realWidth else realHeight
            val aspectRatio = maybeFlippedWidth / maybeFlippedHeight.toDouble()
            val distortion = Math.abs(aspectRatio - screenAspectRatio)
            if (distortion > MAX_ASPECT_DISTORTION) {
                continue
            }
            if (resolution > maxResolution) {
                maxResolution = resolution
                maxResPreviewSize = size
            }
        }

        // If no exact match, use largest preview size. This was not a great idea on older devices because
        // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
        // the CPU is much more powerful.
        if (maxResPreviewSize != null) {
            val largestSize = Point(maxResPreviewSize.width, maxResPreviewSize.height)
            Log.i(TAG, "Using largest suitable preview size: $largestSize")
            return largestSize
        }

        // If there is nothing at all suitable, return current preview size
        val defaultPreview = parameters.previewSize
            ?: throw IllegalStateException("Parameters contained no preview size!")
        val defaultSize = Point(defaultPreview.width, defaultPreview.height)
        Log.i(TAG, "No suitable preview sizes, using default: $defaultSize")
        return defaultSize
    }

    //获取与指定宽高相等或最接近的尺寸
    private fun getBestSize(
        targetWidth: Int,
        targetHeight: Int,
        sizeList: List<Camera.Size>
    ): Point {

        var bestSize = Point(0, 0)
        val targetRatio = (targetHeight.toDouble() / targetWidth)  //目标大小的宽高比
        var minDiff = targetRatio

        for (size in sizeList) {
            if (size.width == targetHeight && size.height == targetWidth) {
                bestSize = Point(size.width, size.height)
                break
            }

            val supportedRatio = (size.width.toDouble() / size.height)
            if (Math.abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(supportedRatio - targetRatio)
                bestSize = Point(size.width, size.height)
            }
        }
        return bestSize
    }

    private fun getBestSizeWithWidth(
        targetWidth: Int,
        targetHeight: Int,
        sizeList: List<Camera.Size>
    ): Point {
        Log.d(
            TAG,
            "getBestSizeWithWidth() called with: targetWidth = $targetWidth, targetHeight = $targetHeight, sizeList = $sizeList"
        )
        var bestSize: Point = Point(0, 0)
        val targetRatio = (targetHeight.toDouble() / targetWidth)  //目标大小的宽高比
        var minDiff = targetRatio
        val longTarget = if (targetWidth > targetHeight) targetWidth else targetHeight
        val shortTarget = if (targetWidth > targetHeight) targetHeight else targetWidth
        for (size in sizeList) {
            val longSize = if (size.width > size.height) size.width else size.height
            val shortSize = if (size.width > size.height) size.height else size.width

            if (longTarget == longSize && shortTarget == shortSize) {
                bestSize = Point(size.width, size.height)
                break
            }

            if (shortSize == shortTarget && longSize < longTarget) {
                bestSize = Point(size.width, size.height)
                break
            }

            val supportedRatio = (size.width.toDouble() / size.height)
            if (Math.abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(supportedRatio - targetRatio)
                bestSize = Point(size.width, size.height)
            }
        }
        Log.i(TAG, "Using largest suitable preview size: [${bestSize.x}, ${bestSize.y}]")
        return bestSize
    }
}