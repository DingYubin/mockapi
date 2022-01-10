package com.yubin.mvvm.net.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * description: 响应结果拦截器
 * date 1/14/21 2:22 PM.
 */
class ResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("ResponseInterceptor", "intercept() called with: chain = $chain")
        return chain.proceed(chain.request())
    }
}