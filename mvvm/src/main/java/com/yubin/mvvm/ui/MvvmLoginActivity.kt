package com.yubin.mvvm.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.NativeActivity
import com.yubin.mvvm.databinding.ActivityLoginBinding
import com.yubin.mvvm.net.model.MvvmLoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@Route(path = RouterPath.AccountPage.PATH_LOGIN)
class MvvmLoginActivity : NativeActivity<ActivityLoginBinding>(), CoroutineScope by MainScope() {

    private lateinit var viewModel: MvvmLoginViewModel

    companion object {
        @JvmStatic
        fun openLoginActivity(activity: Activity) {
            val intent = Intent(activity, MvvmLoginActivity::class.java)
            activity.startActivity(intent)
        }
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
        binding.login.isEnabled = true
        binding.login.onViewClick{
            val account = binding.etConnection.text.toString()
            val password = binding.etPassword.text.toString()

            Log.d("MvvmLoginActivity", "account : $account, password : $password")
            viewModel.login(account, password)
        }

        binding.returnButton.onViewClick{
            finish()
        }
    }

    private fun showMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}