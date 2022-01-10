package com.yubin.mvvm.net.interceptor

import android.net.Uri
import android.util.Log
import com.yubin.baselibrary.core.BaseApplication.Companion.context
import okhttp3.Interceptor
import okhttp3.Response


/**
 * Description ：通用网络拦截器
 */
class NetworkInterceptor : Interceptor{

    companion object{
        //热更新版本号
        var mHotUpdateVersion :Int = 0
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("CMNetworkInterceptor", "intercept() called with: chain = $chain")
        val request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("charset", "UTF-8")
                .removeHeader("User-Agent")
                .addHeader("User-Agent", generateUserAgent())
                //鉴权
//                .apply {
//                    addHeader("authorization", UserHelper.getAccessTokenWithType())
//                }
                .build()

       return chain.proceed(request)
    }


    /**
     * 生成 User-Agent
     */
    private fun generateUserAgent(): String {
        //由于header不允许出现中文，而brand和model都有可能为中文，所以需要encode
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return "storeapp/${packageInfo.versionName}.$mHotUpdateVersion" +
                " Android/${android.os.Build.VERSION.RELEASE}" +
                " ${Uri.encode(android.os.Build.BRAND.removeSpace())}/${Uri.encode(android.os.Build.MODEL.removeSpace())}"
    }

    /**
     * 去掉所有空格
     */
    private fun String.removeSpace():String{
        return this.replace(" ", "")
    }


}