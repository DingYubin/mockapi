package com.yubin.mvvm.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.router.path.RouterPath
import com.yubin.baselibrary.ui.basemvvm.NativeActivity
import com.yubin.mvvm.databinding.ActivityPartBinding
import com.yubin.mvvm.model.MvvmLoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Route(path = RouterPath.MvvmPage.PATH_MVVM_OE)
class OeActivity : NativeActivity<ActivityPartBinding>(), CoroutineScope by MainScope() {

    private lateinit var viewModel: MvvmLoginViewModel

    companion object {
        @JvmStatic
        fun openLoginActivity(activity: Activity) {
            val intent = Intent(activity, OeActivity::class.java)
            activity.startActivity(intent)
        }
    }
    
    override fun getViewBinding(): ActivityPartBinding {
        return ActivityPartBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this.supportActionBar != null) {
            this.supportActionBar!!.hide()
        }
        this.initLiveDataObserver()
        this.initViewStateAndListener()
    }

    private fun initLiveDataObserver() {
        viewModel = ViewModelProvider(this)[MvvmLoginViewModel::class.java]

//        /**
//         * 登录完成之后返回用户信息
//         */
//        viewModel.loginLiveData.observe(this) {
//            it?.let {
//                if (it.isLogin) {
//                    showMsg("登陆成功")
//                    // 跳转主页面
//                    finish()
//                } else {
//                    showMsg("登陆失败")
//                }
//            }
//        }
    }

    private fun initViewStateAndListener() {

        binding.save.onViewClick{
            //保存数据
            lifecycleScope.launch {
                viewModel.saveOeRecord()
            }

        }

        binding.returnButton.onViewClick{
            finish()
        }
    }

    private fun showMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}