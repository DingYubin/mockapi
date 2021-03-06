package com.yubin.net

import com.franmontiel.persistentcookiejar.BuildConfig
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * Created by dingyubin at 2022-01-07 4:27 PM
 * Description ：OkhttpClient实例构造类
 */
class NetOkHttpClient private constructor() {

    private object SingletonHolder {

        val holder: OkHttpClient by lazy {
            NetOkHttpClient().generateOkHttpClient(config)
        }
    }

    companion object {

        var config: NetworkConfigInitHelper.Config? = null

        /**
         * 全局单例模式
         */
        val instance by lazy { SingletonHolder.holder }

        /**
         * 全局配置初始化
         */
        fun init(config: NetworkConfigInitHelper.Config) {
            this.config = config
        }

        /**
         * 新实例模式
         */
        fun newInstance(config: NetworkConfigInitHelper.Config): OkHttpClient {
            return NetOkHttpClient().generateOkHttpClient(config)
        }
    }

    /**
     * 构造OKHttpClient实例
     */
    private fun generateOkHttpClient(config: NetworkConfigInitHelper.Config?): OkHttpClient {
        config ?: throw IllegalArgumentException("CTOkHttpClient.Companion.init() never been called or the config is null")
        val cacheFile = File(config.application.cacheDir, "cache")
        val cache = Cache(cacheFile, config.cacheStateSec)
        return OkHttpClient.Builder()
            .readTimeout(config.readTimeOut, TimeUnit.MILLISECONDS)
            .connectTimeout(config.connectTimeOut, TimeUnit.MILLISECONDS)
            .apply {
                //设置连接池数量
                if (config.maxIdleConnections >= 0) {
                    connectionPool(ConnectionPool(config.maxIdleConnections, 5, TimeUnit.MINUTES))
                }
            }
            .apply {
                for (interceptor in config.interceptors) {
                    addInterceptor(interceptor)
                }
            }
            .apply {
                for (interceptor in config.networkInterceptors) {
                    addNetworkInterceptor(interceptor)
                }
            }
            .apply {
                val mLogInterceptor = HttpLoggingInterceptor()
                if (config.isIsProduction && !BuildConfig.DEBUG) { // 非生产环境模式关闭Log
                    mLogInterceptor.level = HttpLoggingInterceptor.Level.NONE
                } else {
                    mLogInterceptor.level = HttpLoggingInterceptor.Level.BODY
                }
                addNetworkInterceptor(mLogInterceptor)
            }
            .apply {
                //生产环境关闭抓包代理
                if (config.isIsProduction && !BuildConfig.DEBUG) {
                    proxy(Proxy.NO_PROXY)
                }
            }
            .cookieJar(CookieJarManager.cookieJar)
            .cache(cache)
            .build()
    }
}