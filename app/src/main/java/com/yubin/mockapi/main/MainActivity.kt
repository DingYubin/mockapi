package com.yubin.mockapi.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.yubin.account.user.ui.AccountActivity
import com.yubin.baselibrary.core.BaseApplication.Companion.context
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.baselibrary.util.CECDeviceHelper
import com.yubin.baselibrary.util.CMDisplayHelper.dp
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.manager.CameraFinder
import com.yubin.medialibrary.manager.CameraStrategy
import com.yubin.medialibrary.manager.IMediaCallBack
import com.yubin.medialibrary.manager.MediaInfo
import com.yubin.medialibrary.util.CMMediaUtil
import com.yubin.mockapi.R
import com.yubin.mockapi.databinding.ActivityMainBinding
import com.yubin.mvp.ui.MvpLoginActivity

class MainActivity : NativeActivity<ActivityMainBinding>() {

    /**
     * 相机宽度
     */
    var w = 0

    /**
     * 相机高度
     */
    var h = 0


    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (this.supportActionBar != null) {
            this.supportActionBar!!.hide()
        }

        w = CECDeviceHelper.screenWidthWithContext(context) - 28.dp
        h = 200.dp

        val config: MediaManager.Config = MediaManager.Config()
        MediaManager.Companion.init(config)

        findViewById<View>(R.id.goto_user_page).setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.mvp).setOnClickListener {
            MvpLoginActivity.openLoginActivity(
                this
            )
        }

        findViewById<View>(R.id.mvvm).setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.MvvmPage.PATH_MVVM_LOGIN)
                .navigation()
        }

        findViewById<View>(R.id.mvx).setOnClickListener {
//            ARouter.getInstance()
//                .build(RouterPath.AccountPage.PATH_MVX_LOGIN)
//                .navigation()
        }

        findViewById<View>(R.id.im).setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.ImPage.PATH_IM_CONVERSATION)
                .navigation()
        }

        findViewById<View>(R.id.draw).setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.UiPage.PATH_UI_DRAW)
                .navigation()
        }

        findViewById<View>(R.id.rxjava).setOnClickListener {
            ARouter.getInstance()
                .build(RouterPath.RxPage.PATH_RX_JAVA)
                .navigation()
        }

        findViewById<View>(R.id.camera).setOnClickListener {
//            ARouter.getInstance()
//                .build(RouterPath.MediaPage.PATH_MEDIA_CAMERA)
//                .navigation()

            CMMediaUtil.startCamera(
                CameraStrategy(
                    isShowVideo0 = false,
                    maxCount0 = 1,
                    cameraFinder = CameraFinder(true, w, h)
            ).apply {
                selectedBtnText = "确定"
            }, context,
                object : IMediaCallBack {
                    override fun result(medias: ArrayList<MediaInfo>) {
                        Log.d("camera", "url: ${medias[0].uri}")
                        showView(medias[0].uri)
                    }
                })
        }
    }

    private fun showView(uri: String) {
        val imageView = findViewById<ImageView>(R.id.my_img)

        // 从网络上拉取网络图片
        Glide.with(context)
            .load(uri)
            .into(imageView)
    }

}