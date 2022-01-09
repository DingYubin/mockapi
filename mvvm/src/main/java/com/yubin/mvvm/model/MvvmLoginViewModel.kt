package com.yubin.mvvm.model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.yubin.baselibrary.viewmodel.BaseViewModel
import com.yubin.baselibrary.widget.ToastUtil.toast
import com.yubin.mvvm.bean.UserEntity
import com.yubin.mvvm.repository.MvvmLoginRepository

class MvvmLoginViewModel (app: Application) : BaseViewModel(app) {

    private val loginRepository = MvvmLoginRepository()
    val loginLiveData = MutableLiveData<UserEntity>()

    /**
     * 登录
     */
    fun login(
        account: String,
        password: String)
    = suspendCoroutine {
        val response = loginRepository.login(account, password)
        if (response.isSuccessful && null != response.data) {
            loginLiveData.postValue(response.data)
        } else {
            response.message?.toast()
            loginLiveData.postValue(null)
        }
    }
}