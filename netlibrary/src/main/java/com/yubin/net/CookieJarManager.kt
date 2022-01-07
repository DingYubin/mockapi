package com.yubin.net

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.OkHttpClient
import java.lang.IllegalArgumentException

/**
 * Created by dingyubin at 2022-01-07 4:27 PM
 * Description ：CookieJar管理类，负责CookieJar的创建和清除
 */
class CookieJarManager private constructor() {
    private object SingletonHolder {
        val holder: PersistentCookieJar by lazy {
            val config = (CTOkHttpClient.config
                ?: throw IllegalArgumentException("CTOkHttpClient.Companion.init() never been called"))
            PersistentCookieJar(
                SetCookieCache(),
                SharedPrefsCookiePersistor(config.application)
            )
        }
    }

    companion object {
        val cookieJar = SingletonHolder.holder
        fun clear() {
            cookieJar.clear()
        }
    }
}