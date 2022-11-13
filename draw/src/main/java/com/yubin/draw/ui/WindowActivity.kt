package com.yubin.draw.ui

//import com.dou361.ijkplayer.widget.AndroidMediaController
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.draw.R
import com.yubin.draw.databinding.ActivityWindowBinding
import com.yubin.draw.widget.window.FloatingRootWindow
import com.yubin.draw.widget.window.FloatingWindow


@Route(path = RouterPath.UiPage.PATH_UI_WINDOW)
class WindowActivity : NativeActivity<ActivityWindowBinding>(){

    private var mFloatingWindow: FloatingWindow? = null

    private var mFloatingRootWindow: FloatingRootWindow? = null

    override fun getViewBinding(): ActivityWindowBinding =
        ActivityWindowBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.btnShowInterFloatingWindow.setOnClickListener {
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


    /**
     * 浮窗样式
     */
    private fun initFloatRootView(): View {
        val view = View.inflate(this, R.layout.view_floating_root_window, null)
        // 设置视频封面
        val mThumb = view.findViewById<View>(R.id.thumb_floating_view) as ImageView
        Glide.with(this).load(R.drawable.thumb).into(mThumb)
        // 悬浮窗关闭
        view.findViewById<View>(R.id.close_floating_view)
            .setOnClickListener { mFloatingRootWindow?.dismiss() }

        val videoView = view.findViewById<VideoView>(R.id.video_view)
        //视频内容设置
        videoView.setVideoPath("https://stream7.iqilu.com/10339/article/202002/18/2fca1c77730e54c7b500573c2437003f.mp4")
        // 视频准备完毕，隐藏正在加载封面，显示视频
        videoView.setOnPreparedListener { mThumb.visibility = View.GONE }
        // 循环播放
        videoView.setOnCompletionListener { videoView.start() }
        // 开始播放视频
        videoView.start()
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
        // 开始播放视频
        videoView.start()
        return view
    }

}