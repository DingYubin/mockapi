package com.yubin.mvvm.net

import android.app.Application
import com.yubin.mvvm.BuildConfig
import com.yubin.mvvm.net.converter.GsonConverterFactory.Companion.create
import com.yubin.mvvm.net.interceptor.NetworkInterceptor
import com.yubin.mvvm.net.interceptor.ResponseInterceptor
import com.yubin.net.NetOkHttpClient.Companion.init
import com.yubin.net.NetworkConfigInitHelper.initWithApplication
import retrofit2.Converter

/**
 * description: 初始化网络
 * date 3/17/21 4:07 PM.
 */
object NetworkInitHelper {

    @JvmField
    var baseUrl = "http://www.yubin.com/"

    /**
     * 初始化网络模块
     */
    fun initNetWork(application: Application?) {
        //网络
        val config = initWithApplication(
            application!!,
            baseUrl,
            !BuildConfig.DEBUG
        )
        config.interceptors = arrayOf(
            NetworkInterceptor(),
            ResponseInterceptor()
        )

        config.convertFactories = arrayOf<Converter.Factory>(
            create()
        )
        init(config)
    }
}