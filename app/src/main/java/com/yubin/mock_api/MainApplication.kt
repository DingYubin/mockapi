package com.yubin.mock_api

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.yubin.baselibrary.appstart.AppStartInitializer
import com.yubin.baselibrary.appstart.IAppStartCallback
import com.yubin.baselibrary.core.BaseApplication

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

            }

            override fun initInMainProcessBackgroundThread(app: Application) {
                TODO("Not yet implemented")
            }

            override fun initInOther(app: Application) {
                TODO("Not yet implemented")
            }

        })

    }


}