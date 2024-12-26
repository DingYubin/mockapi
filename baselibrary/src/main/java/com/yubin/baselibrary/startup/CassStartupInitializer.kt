package com.yubin.baselibrary.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

/**
 * description 初始化-生成一个全局的context
 *
 * @author laiwei
 * @date 2024/5/6 17:46
 */
class CassStartupInitializer : Initializer<CassStartupInitializer> {

    companion object {
        lateinit var appContext: Context
        lateinit var app: Application
    }

    override fun create(context: Context): CassStartupInitializer {
        return apply {
            appContext = context
            app = context.applicationContext as Application
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return ArrayList()
    }

}