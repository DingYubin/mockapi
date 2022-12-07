package com.yubin.mvvm.protocol

import com.yubin.mvvm.bean.UserEntity
import com.yubin.mvvm.net.model.BaseResponse
import com.yubin.net.NetworkConstants.ENCRYPT_HEADER_USER_QUERY
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MvvmLoginApi {

    @Headers(ENCRYPT_HEADER_USER_QUERY)
    @POST("/api/login")
    suspend fun login(@Body body: RequestBody): BaseResponse<UserEntity>

}