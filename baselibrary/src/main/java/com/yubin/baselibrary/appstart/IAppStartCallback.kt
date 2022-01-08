package com.yubin.baselibrary.appstart

import android.app.Application

interface IAppStartCallback {
    /**
     * 只在主进程初始化的组件
     */
    fun initInMainProcess(app: Application)

    /**
     *  可以在主进程的子线程初始化的组件
     */
    fun initInMainProcessBackgroundThread(app: Application)

    /**
     * 在其他(包括主进程)进程初始化的组件
     */
    fun initInOther(app: Application)

}