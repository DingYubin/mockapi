package com.yubin.mockapi

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.yubin.baselibrary.appstart.AppStartInitializer
import com.yubin.baselibrary.appstart.IAppStartCallback
import com.yubin.baselibrary.core.BaseApplication
import com.yubin.baselibrary.router.CTRouteInitHelper
import com.yubin.baselibrary.util.AutoSizeInitHelper
import com.yubin.mockapi.common.ToolsInitUtils
import com.yubin.mvvm.net.NetworkInitHelper

class MainApplication : BaseApplication() {


    companion object {
        lateinit var application: MainApplication
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)

    }

    override fun onCreate() {
        super.onCreate()
        application = this
        AppStartInitializer.startUp(this, object : IAppStartCallback {
            override fun initInMainProcess(app: Application) {
                AutoSizeInitHelper.init()
                NetworkInitHelper.initNetWork(app)
                ToolsInitUtils.initBugly(app)
            }

            override fun initInMainProcessBackgroundThread(app: Application) {
                initRouter(app)
            }

            override fun initInOther(app: Application) {
            }

        })

    }

    private fun initRouter(application: Application) {
        //路由
        CTRouteInitHelper.initWithApplication(application, BuildConfig.DEBUG)
    }

}