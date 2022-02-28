package com.yubin.medialibrary.video

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.Camera
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.os.*
import android.util.Base64
import android.util.Log
import android.util.Size
import android.view.*
import android.view.View.OnTouchListener
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.cache.ImageLoaderManager
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.databinding.ActivityVideoRecordBinding
import com.yubin.medialibrary.manager.MediaInfo
import com.yubin.medialibrary.util.FileUtil
import com.yubin.medialibrary.util.LogUtil
import com.yubin.medialibrary.util.MediaUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

/**
 *description
 *
 *@author laiwei
 *@date create at 4/26/21 2:37 PM
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class VideoRecordActivity : NativeActivity<ActivityVideoRecordBinding>(),
    SurfaceHolder.Callback,
    MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener {

    override fun getViewBinding() = ActivityVideoRecordBinding.inflate(layoutInflater)

    private val TAG = "video"

    //定义一个标签，当SurfaceView对象存在时，则不再执行其对象的调用方法
    private var result = true
    private var surfaceHolder: SurfaceHolder? = null

    private var timer: Timer? = null
    private var mediaRecorder: MediaRecorder? = null

    private var camera: Camera? = null
    private var cameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    private var isRecording = false
    private var videoFilePath: String? = null
    private var duration = 0
    private var durationTemp = 0
    private var startDuration: Long = 0
    var updateHandler: Handler? = null

    /**
     * 最大录制时间20s
     */
    private var mMaxRecordTime = 20 * 1000
    private var cameraOrientation = 0

    private var w = 0
    private var h = 0
    private var targetRatio = 0f

    private var targetSize = Size(640, 480)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //全屏显示
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        /*
         * 检查是否有读写权限
         */
        if (selfPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
            selfPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        ) {
            ImageLoaderManager.INSTANCE.initThreadPoolExecutor(64, this)
            initUiStyle()
            initSurfaceHolder()
            setViewStateAndListener()
        } else {
            MediaManager.instance.toast.invoke(
                getString(R.string.permission_message_dialog_default),
                this
            )
            finish()
        }
    }

    private fun initUiStyle() {
        MediaManager.instance.mediaStyle?.let {
            binding.btnVideoSend.setTextColor(ContextCompat.getColor(this, it.sendTextColor))
            binding.btnVideoSend.setBackgroundResource(it.sendTextBg)
        }
        mMaxRecordTime = intent.getIntExtra("seconds", mMaxRecordTime)
    }

    private fun initSurfaceHolder() {
        val dm = resources.displayMetrics
        w = dm.widthPixels
        h = dm.heightPixels
        targetRatio = h * 1f / w

        binding.circleProgress.mMaxProgress = mMaxRecordTime

        binding.surfaceView.setZOrderOnTop(false)
        surfaceHolder = binding.surfaceView.holder
        surfaceHolder?.setFormat(PixelFormat.TRANSLUCENT)
        surfaceHolder?.addCallback(this)
        surfaceHolder?.setKeepScreenOn(true)
        updateHandler = VideoHandler {
            if (duration >= mMaxRecordTime || !isRecording) {
                return@VideoHandler
            }
            duration += 100
            //解决拍摄过程显示时间和实际发送的视频时间不对称问题
            if (duration <= 1000) {
                durationTemp = 0
            } else {
                durationTemp = duration - 950
            }
            binding.circleProgress.mProgress = duration
            binding.tvTime.text =
                String.format(
                    Locale.getDefault(),
                    "%d秒",
                    Math.round((durationTemp + 0.0f) / 1000)
                )

        }
    }

    private fun generateFileInfo() {
        val nameSeed = UUID.randomUUID().toString()
        val dirPath: String = FileUtil.getExternalCachePath(this, "video")
        val videoName = "$nameSeed.mp4"
        videoFilePath = dirPath + File.separator + videoName
    }

    private fun releaseMediaRecorder() {
        if (mediaRecorder != null) {
            // clear recorder configuration
            mediaRecorder!!.reset()
            mediaRecorder!!.setOnErrorListener(null)
            // release the recorder object
            mediaRecorder!!.release()
            mediaRecorder = null
        }
    }

    private fun releaseCamera() {
        camera?.setPreviewCallback(null)
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    private fun getCameraInstance(cameraId: Int): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open(cameraId)
        } catch (e: Exception) {
            LogUtil.e(e.message)
        }
        return c
    }

    override fun onBackPressed() {
        if (videoFilePath == null) {
            super.onBackPressed()
            return
        }
        val promptDialog: RecordExitDialog = RecordExitDialog.newInstance()
        promptDialog.setListener(object : RecordExitDialog.ClickListener {
            override fun exit() {
                FileUtil.deleteFile(videoFilePath)
                finish()
            }

            override fun recapture() {
                toRecordVideo()
            }
        })
        promptDialog.show(supportFragmentManager, "CMVideoRecordActivity")
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setViewStateAndListener() {
        /**
         * 去播放
         */
        binding.videoPlay.onViewClick {
            //去播放视频
            val intent = Intent()
            intent.setClass(this@VideoRecordActivity, VideoPlayActivity::class.java)
            intent.putExtra("url", videoFilePath)
            startActivity(intent)
        }
        binding.btnClose.onViewClick(View.OnClickListener {
            onBackPressed()
        })
        /*
         * 发送视频到聊天
         */
        binding.btnVideoSend.onViewClick(View.OnClickListener {
            var currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 1500) {
                return@OnClickListener
            }
            lastClickTime = currentTime
            sendMessage()

        })
        /*
         * 切换摄像头
         */
        binding.btnTurn.onViewClick(View.OnClickListener {
            cameraId = if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Camera.CameraInfo.CAMERA_FACING_FRONT
            } else {
                Camera.CameraInfo.CAMERA_FACING_BACK
            }
            releaseCamera()
            camera = getCameraInstance(cameraId)
            setCameraParams()
        })
        /*
         * 重新拍摄
         */
        binding.btnVideoRerecord.onViewClick(View.OnClickListener {
            toRecordVideo()
        })
        binding.circleProgress.setOnTouchListener(OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (System.currentTimeMillis() - startDuration <= 2000) {
                    MediaManager.instance.toast.invoke("你的操作太频繁，请稍后再试", this)
                    return@OnTouchListener false
                }
                startRecord()
            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                if (isRecording) {
                    if (duration < 1000) {
                        MediaManager.instance.toast.invoke("录制时间过短，请重新录制", this)
                        toRecordVideo()
                    } else {
                        stopRecord()
                        binding.videoThumb.visibility = View.VISIBLE
                        val mmr = MediaMetadataRetriever()
                        mmr.setDataSource(videoFilePath)
                        val thumbBitmap =
                            mmr.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        mmr.release()
                        binding.videoThumb.setImageBitmap(thumbBitmap)
                        switchActionBar(false)
                    }
                }
            }
            true
        })
    }

    private fun toRecordVideo() {
        binding.videoThumb.visibility = View.GONE
        stopRecord()
        FileUtil.deleteFile(videoFilePath)
        videoFilePath = null
        switchActionBar(true)
        initCamera()
    }

    private fun sendMessage() {
        val dismissFunction =
            MediaManager.instance.showLoading(this@VideoRecordActivity, "", true)
        GlobalScope.launch {
            var videoDuration = 0
            val videoFile = File(videoFilePath)
            var thumbString = ""
            withContext(Dispatchers.IO) {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(videoFilePath)
                val baos = ByteArrayOutputStream()
                val thumbBitmap = mmr.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST)
                if (null != thumbBitmap) {
                    thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
                    thumbString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
                    videoDuration =
                        mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt()
                    if (videoDuration > mMaxRecordTime) {
                        videoDuration = mMaxRecordTime
                    }
                    //解决拍摄过程显示时间和实际发送的视频时间不对称问题
                    videoDuration = Math.round((videoDuration + 0.0f) / 1000)
                    mmr.release()
                    MediaUtil.mediaScan(this@VideoRecordActivity, videoFile)
                }
                withContext(Dispatchers.Main) {
                    dismissFunction()
                    val medias = arrayListOf<MediaInfo>()
                    medias.add(
                        MediaInfo(
                            isVideo = true,
                            duration = videoDuration.toLong(),
                            size = videoFile.length(),
                            uri = "file://$videoFilePath",
                            thumb = thumbString
                        )
                    )
                    MediaManager.instance.callBack?.result(medias)

                }
                finish()
            }
        }
    }

    /**
     * 设置录制状态
     *
     * @param showRecord true 录制状态 false录制完成状态
     */
    private fun switchActionBar(showRecord: Boolean) {
        if (showRecord) {
            binding.actionBar.visibility = View.GONE
            binding.recordBar.visibility = View.VISIBLE
            binding.tvTime.text = "长按录制视频"
            binding.circleProgress.mProgress = 0
            binding.btnTurn.isEnabled = true
            binding.videoPlay.visibility = View.GONE
        } else {
            binding.actionBar.visibility = View.VISIBLE
            binding.recordBar.visibility = View.GONE
            binding.btnTurn.isEnabled = false
            binding.videoPlay.visibility = View.VISIBLE
        }
    }

    private fun startRecord() {
        binding.videoThumb.visibility = View.GONE
        isRecording = true
        duration = 0
        startDuration = System.currentTimeMillis()
        generateFileInfo()
        initCamera()
        startRecordVideo()
        binding.tvTime.visibility = View.VISIBLE
        binding.tvTime.text = "0秒"
        timer = Timer()
        timer?.schedule(VideoTimerTask(), 0, 100)
        binding.circleProgress.mProgress = 0
    }

    private fun stopRecord() {
        timer?.purge()
        timer?.cancel()
        timer = null
        result = true
        isRecording = false
        releaseMediaRecorder()
    }

    override fun onPause() {
        super.onPause()
        if (isRecording) {
            if (duration < 1000) {
                MediaManager.instance.toast.invoke("录制时间过短，请重新录制", this)
                toRecordVideo()
            }
            stopRecord()
            switchActionBar(false)
        }
    }

    override fun onNewDestroy() {
        super.onNewDestroy()
        stopRecord()
        releaseCamera()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startRecordVideo() {
        try {
            camera?.unlock()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        mediaRecorder = MediaRecorder()
        // Step 1: Unlock and set camera to MediaRecorder
        mediaRecorder?.reset()
        mediaRecorder?.setOnErrorListener(this)
        mediaRecorder?.setOnInfoListener(this)
        mediaRecorder?.setCamera(camera)
        // Step 2: Set sources
        //设置声音来源
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
//            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        // Set output file format
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setVideoSize(targetSize.width, targetSize.height)
        mediaRecorder?.setVideoFrameRate(30)
        mediaRecorder?.setVideoEncodingBitRate(3 * 1024 * 1024)
        mediaRecorder?.setOrientationHint(cameraOrientation)
        mediaRecorder?.setMaxDuration(mMaxRecordTime)
        // Step 4: Set output file
        mediaRecorder?.setOutputFile(videoFilePath)
        // Step 5: Set the preview output
        mediaRecorder?.setPreviewDisplay(surfaceHolder?.surface)
        try {
            // Step 6: Prepare configured MediaRecorder
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: IllegalStateException) {
            Log.w(TAG, "IllegalStateException preparing MediaRecorder: " + e.message)
            releaseMediaRecorder()
        } catch (e: IOException) {
            Log.w(TAG, "IOException preparing MediaRecorder: " + e.message)
            releaseMediaRecorder()
        } catch (e: Exception) {
            LogUtil.e(e.message)
        }
    }

    private fun initCamera() {
        if (!result) {
            return
        }
        if (camera != null) {
            return
        }
        camera = getCameraInstance(cameraId)
        setCameraParams()
    }

    private fun setCameraParams() {
        if (null == camera) {
            return
        }
        try {
            val params = camera!!.parameters
            //设置相机的很速屏幕
            val isRotate: Boolean
            if (this.resources
                    .configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
            ) {
                params["orientation"] = "portrait"
                camera!!.setDisplayOrientation(90)
                isRotate = true
            } else {
                params["orientation"] = "landscape"
                camera!!.setDisplayOrientation(0)
                isRotate = false
            }
            setCameraDisplayOrientation(this, cameraId, camera!!)
            //设置聚焦模式
            val focusModes = params.supportedFocusModes
            for (focusMode in focusModes) {
                if (Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO == focusMode) {
                    params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                    break
                }
            }
            getBestVideoSize(params.supportedVideoSizes, isRotate)
            val size = getBestPreViewSize(
                targetSize.width,
                targetSize.height,
                params.supportedPreviewSizes
            )
            if (null != size) {
                val lp = binding.surfaceView.layoutParams
                if (isRotate) {
                    lp.height = (w * 1.0 * size.width / size.height).toInt()
                } else {
                    lp.height = (w * 1.0 * size.height / size.width).toInt()
                }
                lp.width = w
                binding.surfaceView.layoutParams = lp
                params.setPreviewSize(size.width, size.height)
            }
            //缩短Recording启动时间
            params.setRecordingHint(true)
            //是否支持影像稳定能力，支持则开启
            if (params.isVideoStabilizationSupported) {
                params.videoStabilization = true
            }
            camera!!.parameters = params
            camera!!.setPreviewDisplay(surfaceHolder)
            camera!!.startPreview()
        } catch (e: Exception) {
            LogUtil.e(e.message)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceHolder = holder
        result = true
        initCamera()
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
        surfaceHolder = holder
        result = false
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        result = false
    }

    override fun onError(mr: MediaRecorder?, what: Int, extra: Int) {}

    override fun onInfo(mr: MediaRecorder?, what: Int, extra: Int) {
        if (MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED == what) {
            binding.circleProgress.mProgress = mMaxRecordTime
            binding.tvTime.text = String.format(Locale.getDefault(), "%d秒", 20)
            stopRecord()
            binding.videoThumb.visibility = View.VISIBLE
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(videoFilePath)
            val thumbBitmap =
                mmr.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST)
            mmr.release()
            binding.videoThumb.setImageBitmap(thumbBitmap)
            switchActionBar(false)
        }
    }

    private fun getBestVideoSize(
        sizes: List<Camera.Size>,
        isRotate: Boolean
    ) {
        val max = 1280 * 960
        val min = 640 * 480
        var w = 640
        var h = 480
        var minDiff: Float = Float.MAX_VALUE
        for (i in sizes.indices) {
            val size = sizes[i]
            val pixels = size.width * size.height
            if (pixels < min || pixels > max) {
                continue
            }
            var tRatio: Float = if (isRotate) {
                size.width * 1f / size.height
            } else {
                size.height * 1f / size.width
            }
            val diff = Math.abs(targetRatio - tRatio)
            if (diff < minDiff) {
                w = size.width
                h = size.height
                minDiff = diff
            }
        }
        targetSize = Size(w, h)
    }

    //获取与指定宽高相等或最接近的尺寸
    private fun getBestPreViewSize(
        targetWidth: Int,
        targetHeight: Int,
        sizeList: List<Camera.Size>
    ): Camera.Size? {
        var bestSize: Camera.Size? = null
        val targetRatio = targetHeight * 1f / targetWidth //目标大小的宽高比
        var minDiff = targetRatio
        for (size in sizeList) {
            if (size.width == targetHeight && size.height == targetWidth) {
                bestSize = size
                break
            }
            val supportedRatio = size.height * 1f / size.width
            if (Math.abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(supportedRatio - targetRatio)
                bestSize = size
            }
        }
        return bestSize
    }

    /**
     * 设置相机显示方向的详细解读
     */
    private fun setCameraDisplayOrientation(
        activity: Activity,
        cameraId: Int, camera: Camera
    ) {
        // 1.获取屏幕切换角度值。
        val rotation = activity.windowManager.defaultDisplay
            .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        // 2.获取摄像头方向。
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        // 3.设置相机显示方向。
        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        cameraOrientation = info.orientation
        camera.setDisplayOrientation(result)
    }

    inner class VideoTimerTask : TimerTask() {
        override fun run() {
            updateHandler?.sendEmptyMessage(1)
        }
    }

    @SuppressLint("HandlerLeak")
    inner class VideoHandler(private val handleMsg: () -> Unit) :
        Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            handleMsg()
        }
    }
}