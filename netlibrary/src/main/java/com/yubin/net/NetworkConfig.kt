package com.yubin.net

/**
 * Description ：网络参数配置
 */
object NetworkConfig {

    /**
     * 读取超时
     */
    const val READ_TIME_OUT = 20 * 1000L

    /**
     * 连接超时
     */
    const val CONNECT_TIME_OUT = 20 * 1000L

    /**
     * 缓存大小
     */
    const val CACHE_STALE_SEC = 10 * 1024 * 1024L
}