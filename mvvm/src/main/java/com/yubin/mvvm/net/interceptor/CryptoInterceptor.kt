package com.yubin.mvvm.net.interceptor

import com.yubin.baselibrary.util.AESCBCUtil
import com.yubin.baselibrary.util.LogUtil
import com.yubin.net.NetworkConstants.ENCRYPT_AVER
import com.yubin.net.NetworkConstants.ENCRYPT_USER
import com.yubin.net.NetworkConstants.X_CONTENT
import com.yubin.net.NetworkConstants.X_LOCAL_ENCRYPT
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.io.IOException

/**
 * 加密、解密
 */
class CryptoInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val encryptTag = request.header(X_LOCAL_ENCRYPT)

        if (ENCRYPT_USER == encryptTag) {
            request = encryptUserReq(request)
        }
        return chain.proceed(request)
    }

    private fun encryptUserReq(request: Request): Request {
        var requestBody: RequestBody? = request.body ?: return request.newBuilder().header(X_CONTENT, ENCRYPT_AVER).build()
        val plain = bodyToString(requestBody)
        LogUtil.d("requestBody : $plain")
        val bodyEncrypted = AESCBCUtil.encrypt(plain)
        requestBody = bodyEncrypted.toByteArray().toRequestBody(ENCRYPT_AVER.toMediaType())

        LogUtil.d("requestBody : ${bodyToString(requestBody)}")
        return request.newBuilder().method(request.method, requestBody).build()
    }

    @Throws(IOException::class)
    private fun bodyToString(request: RequestBody?): String {
        try {
            Buffer().use { buffer ->
                return if (request != null) {
                    request.writeTo(buffer)
                    buffer.readUtf8()
                } else {
                    ""
                }
            }
        } catch (e: Exception) {
            throw IOException(e)
        }
    }
}