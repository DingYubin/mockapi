package com.yubin.mvvm.repository

import com.yubin.mvvm.bean.UserEntity
import com.yubin.mvvm.net.dsl.requestBody
import com.yubin.mvvm.net.model.BaseResponse
import com.yubin.mvvm.protocol.MvvmLoginApi
import com.yubin.net.RetrofitFactory.Companion.instance

class MvvmLoginRepository {

    private val accountLoginApi: MvvmLoginApi =
        instance.create(MvvmLoginApi::class.java)

    /**
     * 登录
     */
    suspend fun login(
        userLoginName: String,
        password: String
    ): BaseResponse<UserEntity> {
        return accountLoginApi.login(requestBody {
            "userLoginName".with(userLoginName)
            "password".with(password)
        })
    }
}