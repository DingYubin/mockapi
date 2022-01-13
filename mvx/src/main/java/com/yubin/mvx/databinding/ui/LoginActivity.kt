package com.yubin.mvx.databinding.ui

import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.mvx.R
import com.yubin.mvx.databinding.ActivityLoginNewBinding
import com.yubin.mvx.databinding.base.BaseActivity
import com.yubin.mvx.databinding.model.LoginViewModel

@Route(path = RouterPath.AccountPage.PATH_MVX_LOGIN)
class LoginActivity : BaseActivity<ActivityLoginNewBinding?>() {
    private var loginViewModel: LoginViewModel? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_login_new
    }

    override fun initViewModel() {
        loginViewModel = getViewModel(LoginViewModel::class.java)
        bindingView!!.model = loginViewModel
    }

    override fun init() {
        bindingView!!.password.transformationMethod = PasswordTransformationMethod.getInstance()
        bindingView!!.login.setOnClickListener {
            Log.d(
                "MvpLoginActivity",
                "account : " + bindingView!!.account.text.toString() + ", password : " + bindingView!!.password.text.toString()
            )
        }
        bindingView!!.returnButton.setOnClickListener { finish() }
    }

    private fun showMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}