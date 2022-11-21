package com.yubin.draw.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.widget.ToastUtil
import com.yubin.draw.R
import com.yubin.draw.databinding.ActivityWindowBinding
import com.yubin.draw.widget.view.video.VideoListener
import com.yubin.draw.widget.view.video.VideoPlayer
import com.yubin.draw.widget.window.FloatingRootWindow
import com.yubin.draw.widget.window.FloatingWindow
import com.yubin.medialibrary.permissionHelper.CECPermissionHelper
import com.yubin.medialibrary.permissionHelper.CECPermissionListener
import java.io.File
import java.io.IOException


@Route(path = RouterPath.UiPage.PATH_UI_WINDOW)
class WindowActivity : NativeActivity<ActivityWindowBinding>(){

//    private val horizontal_path = "https://vod01.zmengzhu.com/video/hls-hd/1636445926b4df7f1505720fe9665276_221109091144.m3u8"
    private val path = "http://vod01-o.zmengzhu.com/record/base/92f53f6b416fc7b300370608.m3u8"
    private val horizontal_path = "rtmp://al01-live.zmengzhu.com/watch/04189782e5cedd4f00371329?auth_key=1668603243-0-0-74191b88a2bbb550e829024c6b648418"
//    private val mp4_path = "https://stream7.iqilu.com/10339/article/202002/18/2fca1c77730e54c7b500573c2437003f.mp4"
    private val stream_path = "rtmp://al01-live.zmengzhu.com/watch/f6f86d752e26f98100372857?auth_key=1668841131-0-0-c7d34ce901da02e749eabfd3a3ad3696"
    private val mp4_path = "https://cass-mobile.oss-cn-shenzhen.aliyuncs.com/assets/live-stream/%E8%B4%A6%E6%9C%9F%E6%94%AF%E6%8C%81VCR0809.mp4"
    private val replay_path = "http://vod01-o.zmengzhu.com/record/base/6f4a6bf32d75024900372725.m3u8"

    private var mFloatingWindow: FloatingWindow? = null

    private var mFloatingRootWindow: FloatingRootWindow? = null

    private var mVideoFloatingRootWindow: FloatingRootWindow? = null

    private var videoPlayer: VideoPlayer?= null

    private var videoView: VideoView?= null

    private var mThumb: ImageView?= null

    override fun getViewBinding(): ActivityWindowBinding =
        ActivityWindowBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {

        binding.btnShowVideoFloatingWindow.setOnClickListener {
            //动态权限获取
            CECPermissionHelper.requestForStorage(object : CECPermissionListener {
                override fun onPermissionGranted() {
                    //展示浮窗
                    showVideoFloatingWindow()
                }

                override fun onPermissionDeclined(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }

                override fun onPermissionDenied(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }
            })
        }

        binding.btnDismissVideoFloatingWindow.setOnClickListener {
            mVideoFloatingRootWindow?.dismiss()
        }

        binding.btnShowInterFloatingWindow.setOnClickListener {
            //展示浮窗
            showInterFloatingWindow()
        }

        binding.btnDismissFloatingWindow.setOnClickListener {
            mFloatingRootWindow?.dismiss()
        }


        binding.btnShowFloatingWindow.setOnClickListener {
            if (!requestOverlayPermission()) {
                showFloatingWindow()
            } else {
                Toast.makeText(this, "请开启悬浮窗权限", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDismissFloatingWindow.setOnClickListener {
            mFloatingWindow?.dismiss()
        }

        binding.start.setOnClickListener {
            videoPlayer?.start()
        }

        binding.pause.setOnClickListener {
            videoPlayer?.pause()
        }

        binding.cutVideo.setOnClickListener {

            //自己申请权限了
            val file = File(Environment.getExternalStorageDirectory(), "aaa.png")
            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        binding.verticalVideo.setOnClickListener {
            //动态权限获取
            CECPermissionHelper.requestForStorage(object : CECPermissionListener {
                override fun onPermissionGranted() {
                    //展示浮窗
                    showVideoFloatingWindow("VERTICAL", horizontal_path)
                }

                override fun onPermissionDeclined(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }

                override fun onPermissionDenied(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }
            })
        }

        binding.btnVideoViewFloatingWindow.setOnClickListener {
            //动态权限获取
            CECPermissionHelper.requestForStorage(object : CECPermissionListener {
                override fun onPermissionGranted() {
                    //展示浮窗
                    showVideoViewFloatingWindow("HORIZONTAL", mp4_path)
                }

                override fun onPermissionDeclined(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }

                override fun onPermissionDenied(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }
            })
        }

        binding.horizontalVideo.setOnClickListener {
            //动态权限获取
            CECPermissionHelper.requestForStorage(object : CECPermissionListener {
                override fun onPermissionGranted() {
                    //展示浮窗
                    showVideoFloatingWindow("HORIZONTAL", horizontal_path)
                }

                override fun onPermissionDeclined(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }

                override fun onPermissionDenied(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }
            })
        }

        binding.streamVideo.setOnClickListener {
            //动态权限获取
            CECPermissionHelper.requestForStorage(object : CECPermissionListener {
                override fun onPermissionGranted() {
                    //展示浮窗
                    showVideoFloatingWindow("HORIZONTAL", stream_path)
                }

                override fun onPermissionDeclined(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }

                override fun onPermissionDenied(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }
            })
        }

        binding.mp4Video.setOnClickListener {
            //动态权限获取
            CECPermissionHelper.requestForStorage(object : CECPermissionListener {
                override fun onPermissionGranted() {
                    //展示浮窗
                    showVideoFloatingWindow("HORIZONTAL", mp4_path)
                }

                override fun onPermissionDeclined(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }

                override fun onPermissionDenied(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }
            })
        }

        binding.replayVideo.setOnClickListener {
            //动态权限获取
            CECPermissionHelper.requestForStorage(object : CECPermissionListener {
                override fun onPermissionGranted() {
                    //展示浮窗
                    showVideoFloatingWindow("HORIZONTAL", replay_path)
                }

                override fun onPermissionDeclined(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }

                override fun onPermissionDenied(permission: String?) {
                    ToastUtil.showToast(
                        getString(R.string.permission_message_dialog_default),
                        Toast.LENGTH_SHORT
                    )
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.start()

    }

    override fun onPause() {
        super.onPause()
        if (videoPlayer?.isPlaying == true) {
            videoPlayer?.pause()
            //保留暂停时最后一帧数画面
        }

        if (videoView?.isPlaying == true) {
            videoView?.pause()
        }
    }

    /**
     * 判断是否开启悬浮窗口权限，否则，跳转开启页
     */
    private fun requestOverlayPermission(): Boolean {
        return if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                    "package:$packageName"
                )
            )
            resultLauncher.launch(intent)
            true
        } else {
            false
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            if (Settings.canDrawOverlays(this@WindowActivity)) {
                showFloatingWindow()
            } else {
                Toast.makeText(this, "获取悬浮窗权限失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showFloatingWindow() {
        mFloatingWindow = FloatingWindow()
        val view: View = initFloatView()
        mFloatingWindow?.showFloatingWindowView(this, view)
    }

    private fun showInterFloatingWindow() {
        mFloatingRootWindow = FloatingRootWindow()
        val view: View = initFloatRootView()
        mFloatingRootWindow?.showFloatingWindowView(this, view)
    }

    private fun showVideoFloatingWindow() {
        mVideoFloatingRootWindow = FloatingRootWindow()
        val view: View = initFloatRootVideo()
        mVideoFloatingRootWindow?.showFloatingWindowView(this, view)
    }

    private fun showVideoFloatingWindow(positionType: String, path: String) {
        binding.videoFloatWindow.removeAllViews()
        videoPlayer?.release()
        //展示浮窗
        val view: View = initFloatVideo(positionType, path)
        binding.videoFloatWindow.addView(view)
    }

    private fun showVideoViewFloatingWindow(positionType: String, path: String) {
        binding.videoFloatWindow.removeAllViews()
//        videoView?.release()
//        videoPlayer?.release()
        //展示浮窗
        val view: View = initFloatVideoView(positionType, path)
        binding.videoFloatWindow.addView(view)
    }

    private fun initFloatVideo(positionType: String, path: String): View {
        val view = if ("VERTICAL" == positionType) {
            View.inflate(this, R.layout.view_video_floating_window_vertical, null)
        } else {
            View.inflate(this, R.layout.view_video_floating_window_horizontal, null)
        }

        // 设置视频封面
        mThumb = view.findViewById<View>(R.id.thumb_floating_view) as ImageView
        Glide.with(this).load(R.drawable.thumb).into(mThumb!!)

        videoPlayer = view.findViewById(R.id.video_view)
        //视频内容设置
//        videoPlayer.initVideoView()
        videoPlayer?.setPath(path)
        try {
            videoPlayer?.load()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        videoPlayer?.setVideoListener(object : VideoListener {

            override fun onCompletion() {
//                videoPlayer.start()
            }
            override fun onPrepared() {
                mThumb?.visibility = View.GONE
                videoPlayer?.start()
            }

        })

        // 悬浮窗关闭
        view.findViewById<View>(R.id.close_floating_view)
            .setOnClickListener {
                videoPlayer?.release()
                binding.videoFloatWindow.removeAllViews()
            }

        // 跳转操作
        view.findViewById<FrameLayout>(R.id.ad)
            .setOnClickListener {
                //通过路由跳转直播页面
                ARouter.getInstance()
                    .build(RouterPath.UiPage.PATH_UI_EXPOSURE)
                    .navigation()
            }
        return view
    }


    private fun initFloatVideoView(positionType: String, path: String): View {
        val view = if ("VERTICAL" == positionType) {
            View.inflate(this, R.layout.view_video_view_floating_window_vertical, null)
        } else {
            View.inflate(this, R.layout.view_video_view_floating_window_horizontal, null)
        }

        // 设置视频封面
        val mThumb = view.findViewById<View>(R.id.thumb_floating_view) as ImageView
        Glide.with(this).load(R.drawable.thumb).into(mThumb)
        videoView = view.findViewById(R.id.video_view)

        //视频内容设置
        videoView?.setVideoPath("https://stream7.iqilu.com/10339/article/202002/18/2fca1c77730e54c7b500573c2437003f.mp4")
        // 视频准备完毕，隐藏正在加载封面，显示视频
        videoView?.setOnPreparedListener { mThumb.visibility = View.GONE }
        // 循环播放
        videoView?.setOnCompletionListener { videoView?.start() }

        videoView?.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.UiPage.PATH_UI_EXPOSURE)
                .navigation()
        }
        // 开始播放视频
        videoView?.start()

        // 悬浮窗关闭
        view.findViewById<View>(R.id.close_floating_view)
            .setOnClickListener {
                binding.videoFloatWindow.removeAllViews()
            }

        // 跳转操作
        view.findViewById<FrameLayout>(R.id.ad)
            .setOnClickListener {
                //通过路由跳转直播页面
                ARouter.getInstance()
                    .build(RouterPath.UiPage.PATH_UI_EXPOSURE)
                    .navigation()
            }
        return view
    }
    /**
     * 浮窗样式
     */
    private fun initFloatRootView(): View {
        val view = View.inflate(this, R.layout.view_floating_root_window, null)
        // 设置视频封面
        val mThumb = view.findViewById<View>(R.id.thumb_floating_view) as ImageView
        Glide.with(this).load(R.drawable.thumb).into(mThumb)

        val videoView = view.findViewById<VideoView>(R.id.video_view)
        //视频内容设置
        videoView.setVideoPath("https://stream7.iqilu.com/10339/article/202002/18/2fca1c77730e54c7b500573c2437003f.mp4")
        // 视频准备完毕，隐藏正在加载封面，显示视频
        videoView.setOnPreparedListener { mThumb.visibility = View.GONE }
        // 循环播放
        videoView.setOnCompletionListener { videoView.start() }
        // 开始播放视频
        videoView.start()

        // 悬浮窗关闭
        view.findViewById<View>(R.id.close_floating_view)
            .setOnClickListener {
                mFloatingRootWindow?.dismiss()
            }

        return view
    }

    /**
     * 浮窗拉流样式
     */
    private fun initFloatRootVideo(): View {
        val view = View.inflate(this, R.layout.view_video_floating_window_horizontal, null)
//        val view = View.inflate(this, R.layout.view_video_floating_window_horizontal, null)
        // 设置视频封面
        val mThumb = view.findViewById<View>(R.id.thumb_floating_view) as ImageView
        Glide.with(this).load(R.drawable.thumb).into(mThumb)

        videoPlayer = view.findViewById(R.id.video_view)
        //视频内容设置
//        videoPlayer.setPath("rtmp://al01-live.zmengzhu.com/watch/61959a845384098b00371405?auth_key=1668240168-0-0-03ef6eccf9310aa2e84fede63e9acf11")

//        videoPlayer?.setPath("http://vod01.zmengzhu.com/video/live/0bde047237335a1200371364.m3u8")

//        videoPlayer.setPath("https://stream7.iqilu.com/10339/article/202002/18/2fca1c77730e54c7b500573c2437003f.mp4")
//        videoPlayer.initVideoView()
        videoPlayer?.setPath(path)
        try {
            videoPlayer?.load()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        videoPlayer?.setVideoListener(object : VideoListener {

            override fun onCompletion() {
//                videoPlayer.start()
            }
            override fun onPrepared() {
                mThumb.visibility = View.GONE
                videoPlayer?.start()
            }

        })

        // 悬浮窗关闭
        view.findViewById<View>(R.id.close_floating_view)
            .setOnClickListener {
                videoPlayer?.release()
                mVideoFloatingRootWindow?.dismiss()
            }
        return view
    }

    /**
     * 浮窗样式
     */
    private fun initFloatView(): View {
        val view = View.inflate(this, R.layout.view_floating_window, null)
        // 设置视频封面
        val mThumb = view.findViewById<View>(R.id.thumb_floating_view) as ImageView
        Glide.with(this).load(R.drawable.thumb).into(mThumb)
        // 悬浮窗关闭
        view.findViewById<View>(R.id.close_floating_view)
            .setOnClickListener { mFloatingWindow?.dismiss() }
        // 返回前台页面
        view.findViewById<View>(R.id.back_floating_view).setOnClickListener {
            mFloatingWindow?.setTopApp(
                this@WindowActivity
            )
        }
        val videoView = view.findViewById<VideoView>(R.id.video_view)
        //视频内容设置
        videoView.setVideoPath("https://stream7.iqilu.com/10339/article/202002/18/2fca1c77730e54c7b500573c2437003f.mp4")
        // 视频准备完毕，隐藏正在加载封面，显示视频
        videoView.setOnPreparedListener { mThumb.visibility = View.GONE }
        // 循环播放
        videoView.setOnCompletionListener { videoView.start() }

        videoView.setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.UiPage.PATH_UI_EXPOSURE)
                .navigation()
        }
        // 开始播放视频
        videoView.start()
        return view
    }

}