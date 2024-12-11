package com.yubin.mvvm.model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.yubin.baselibrary.util.LogUtil
import com.yubin.baselibrary.viewmodel.BaseViewModel
import com.yubin.baselibrary.widget.ToastUtil.toast
import com.yubin.kotlindb.AppDataBase
import com.yubin.kotlindb.entity.OERecord
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

    suspend fun saveOeRecord() {
        val oe = OERecord()
        oe.oe = "123456"
        oe.longer = "1234"
        oe.width = "123"
        oe.high = "123"
        oe.weight = "123"

        AppDataBase.getInstance().oeRecordDao().save(oe)

        LogUtil.d(
            AppDataBase.getInstance().oeRecordDao()
                .queryOERecordByOe("123456").toString()
        )
    }
}