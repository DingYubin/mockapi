package com.yubin.mockapi.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.yubin.account.user.ui.AccountActivity
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.mockapi.R
import com.yubin.mvp.ui.MvpLoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

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
    }
}