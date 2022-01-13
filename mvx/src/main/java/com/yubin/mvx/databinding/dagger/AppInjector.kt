package com.yubin.mvx.databinding.dagger

import android.app.Application

/**
 * <pre>
 * @author : dingyubin
 * time   : 2018-1-16
 * desc   : 初始化
 * version: 1.0
</pre> *
 */
object AppInjector {

    private lateinit var appComponent: AppComponent

    fun init(app: Application) {
        appComponent = DaggerAppComponent.builder().application(app).build()

        appComponent.inject(app)
    }
}