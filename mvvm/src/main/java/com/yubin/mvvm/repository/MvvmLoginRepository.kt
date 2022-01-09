package com.yubin.mvvm.repository

import com.yubin.mvvm.bean.UserEntity
import com.yubin.mvvm.protocol.MvvmLoginApi
import com.yubin.net.RetrofitFactory.Companion.instance
import com.yubin.net.dsl.requestBody
import com.yubin.net.model.BaseResponse

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