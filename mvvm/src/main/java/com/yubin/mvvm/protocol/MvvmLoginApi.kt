package com.yubin.mvvm.protocol

import com.yubin.mvvm.bean.UserEntity
import com.yubin.mvvm.net.model.BaseResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface MvvmLoginApi {

    @POST("/api/login")
    suspend fun login(@Body body: RequestBody): BaseResponse<UserEntity>

}