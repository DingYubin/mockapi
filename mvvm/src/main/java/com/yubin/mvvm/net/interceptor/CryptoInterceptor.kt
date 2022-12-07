package com.yubin.mvvm.net.interceptor

import com.yubin.baselibrary.util.AESCBCUtil
import com.yubin.baselibrary.util.LogUtil
import com.yubin.net.NetworkConstants.ENCRYPT_AVER
import com.yubin.net.NetworkConstants.ENCRYPT_USER
import com.yubin.net.NetworkConstants.X_CONTENT
import com.yubin.net.NetworkConstants.X_LOCAL_DECRYPT
import com.yubin.net.NetworkConstants.X_LOCAL_ENCRYPT
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
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

        var response = chain.proceed(request)
        if (response.isSuccessful) {
            val decryptTag = response.header(X_LOCAL_DECRYPT)
            if (ENCRYPT_USER == decryptTag) {
                response = decryptResp(response)
            }
        }
        return response
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

    private fun decryptResp(response: Response) : Response {
        val responseBody = response.body
        val bodyStr = response.body?.string()
//        val bodyStr = "i6Xufs+E5p3upVhS0oNMbeFNmMQ/rmHXBKa6FJG0m9tMupHShVuH6ndPyxRpxWLJRzGhHxSCnrOH3NeO70Q9R6jFTsru+b+p6nB7IWZvO6cED3kcObHpQ5v536fbrkZoBOpMLS+X5inCG9KG98jizPI77tiuUE1KhdCiOwHPvFzkLy+gX+mf9cT6ziJdS7Wc"
        LogUtil.d("responseBody : $bodyStr")
        val bodyDecrypted = AESCBCUtil.decrypt(bodyStr)
        LogUtil.d("responseBody : $bodyDecrypted")
        return response.newBuilder().body(bodyDecrypted.toResponseBody(responseBody?.contentType())).build()
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