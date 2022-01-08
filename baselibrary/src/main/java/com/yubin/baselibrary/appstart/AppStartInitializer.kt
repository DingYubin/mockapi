package com.yubin.baselibrary.appstart

import android.app.Application
import com.yubin.baselibrary.util.CMAppHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *description application启动器
 */
object AppStartInitializer {

    fun startUp(app: Application, appStart: IAppStartCallback) {
        if (CMAppHelper.isMainProcess(app)) {
            GlobalScope.launch(Dispatchers.IO) {
                appStart.initInMainProcessBackgroundThread(app)
            }
            appStart.initInMainProcess(app)
        }
        appStart.initInOther(app)
    }
}