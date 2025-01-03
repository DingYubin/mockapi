package com.yubin.draw.web

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.mvvm.CassNativeActivity
import com.yubin.baselibrary.util.CMStatusBarUtil
import com.yubin.baselibrary.util.resColor
import com.yubin.draw.R
import com.yubin.draw.databinding.ActivityWebWindowBinding
import com.yubin.draw.manager.FloatingWebWindowManager


@Route(path = RouterPath.UiPage.PATH_UI_WEB_WINDOW)
class WebWindowActivity : CassNativeActivity<ActivityWebWindowBinding>(){

    private var mThumb: ImageView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CMStatusBarUtil.setStatusColor(this, false, true, R.color.color_2970fd.resColor())
        supportActionBar?.hide()
        FloatingWebWindowManager.initialize(this)
        this.initWebView()
//        initView()
    }

    private fun initWebView() {
        val url = "https://www.baidu.com/"

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(
            R.id.workSpaceFragmentContainer,
            CAWorkSpaceFragment.newInstance(url, "", false)
        )
        fragmentTransaction.commit()
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
            if (Settings.canDrawOverlays(this@WebWindowActivity)) {
//                showFloatingWindow()
            } else {
                Toast.makeText(this, "获取悬浮窗权限失败", Toast.LENGTH_SHORT).show()
            }
        }
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


}