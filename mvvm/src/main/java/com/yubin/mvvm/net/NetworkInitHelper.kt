package com.yubin.mvvm.net

import android.app.Application
import android.text.TextUtils
import com.google.gson.Gson
import com.yubin.baselibrary.core.BaseApplication.Companion.context
import com.yubin.library.mock.MockApiInterceptor
import com.yubin.library.mock.MockApiSuite
import com.yubin.library.mock.api.StandardMockApi
import com.yubin.library.mock.constant.MockHttpMethod
import com.yubin.mvvm.BuildConfig
import com.yubin.mvvm.net.converter.GsonConverterFactory.Companion.create
import com.yubin.mvvm.net.interceptor.CryptoInterceptor
import com.yubin.mvvm.net.interceptor.NetworkInterceptor
import com.yubin.mvvm.net.interceptor.ResponseInterceptor
import com.yubin.mvvm.net.model.MockConfig
import com.yubin.mvvm.net.util.MockUtil
import com.yubin.net.NetOkHttpClient.Companion.init
import com.yubin.net.NetworkConfigInitHelper.initWithApplication

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
            ResponseInterceptor(),
            CryptoInterceptor(),
            getMockApiInterceptor(application)
        )

        config.convertFactories = arrayOf(
            create()
        )
        init(config)
    }

    /**
     * 获取Mock拦截器
     */
    private fun getMockApiInterceptor(application: Application?): MockApiInterceptor {
        val mockApiInterceptor = MockApiInterceptor(application)

        val mockJson: String = MockUtil.stringFromAssets(context, "config.json")
        if (!TextUtils.isEmpty(mockJson)) {
            val mockConfig: MockConfig = Gson().fromJson(mockJson, MockConfig::class.java)
            for (mocks in mockConfig.mocks) {
                val suite = MockApiSuite(mocks.name) // account为suite name
                for (mock in mocks.mock) {
                    if (mock.method == "get") {
                        suite.addMockApi(
                            StandardMockApi(
                                MockHttpMethod.GET,
                                mock.api
                            ).setSuccessDataFile(mock.mockFile)
                        )
                    } else if (mock.method == "post") {
                        suite.addMockApi(
                            StandardMockApi(
                                MockHttpMethod.POST,
                                mock.api
                            ).setSuccessDataFile(mock.mockFile)
                        )
                    }
                }
                mockApiInterceptor.addMockApiSuite(suite)
            }
        }
        return mockApiInterceptor
    }
}