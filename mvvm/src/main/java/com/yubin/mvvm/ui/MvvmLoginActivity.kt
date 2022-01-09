package com.yubin.mvvm.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.ui.NativeActivity
import com.yubin.mvvm.databinding.ActivityLoginBinding
import com.yubin.mvvm.model.MvvmLoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MvvmLoginActivity : NativeActivity<ActivityLoginBinding>(), CoroutineScope by MainScope() {

    private lateinit var viewModel: MvvmLoginViewModel

    fun openLoginActivity(activity: Activity) {
        val intent = Intent(activity, MvvmLoginActivity::class.java)
        activity.startActivity(intent)
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initLiveDataObserver()
        this.initViewStateAndListener()
    }

    private fun initLiveDataObserver() {
        viewModel = ViewModelProvider(this)[MvvmLoginViewModel::class.java]

        /**
         * 登录完成之后返回用户信息
         */
        viewModel.loginLiveData.observe(this) {
            it?.let {
                if (it.isLogin) {
                    showMsg("登陆成功")
                    finish()
                } else {
                    showMsg("登陆失败")
                }
            }
        }
    }

    private fun initViewStateAndListener() {

        binding.login.onViewClick{
            val account = binding.etConnection.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(account, password)
        }
    }

    private fun showMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}