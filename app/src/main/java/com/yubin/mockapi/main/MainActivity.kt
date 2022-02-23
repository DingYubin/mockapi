package com.yubin.mockapi.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.yubin.account.user.ui.AccountActivity
import com.yubin.baselibrary.core.BaseApplication.Companion.context
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.manager.CameraStrategy
import com.yubin.medialibrary.manager.IMediaCallBack
import com.yubin.medialibrary.manager.MediaInfo
import com.yubin.medialibrary.util.CMMediaUtil
import com.yubin.mockapi.R
import com.yubin.mvp.ui.MvpLoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

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

        findViewById<View>(R.id.camera).setOnClickListener {
//            ARouter.getInstance()
//                .build(RouterPath.MediaPage.PATH_MEDIA_CAMERA)
//                .navigation()

            CMMediaUtil.startCamera(
                CameraStrategy(
                isShowVideo0 = false,
                maxCount0 = 1
            ).apply {
                selectedBtnText = "确定"
            }, context,
                object : IMediaCallBack {
                    override fun result(medias: ArrayList<MediaInfo>) {
                        Log.d("camera", "url: $medias[0].uri")
                    }
                })
        }
    }
}