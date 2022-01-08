package com.yubin.baselibrary.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.os.Process
import android.text.TextUtils
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * description: app相关辅助类
 */
object CMAppHelper {
    /**
     * 获取应用versionCode
     */
    fun getVersionCode(context: Context): String {
        val packageManager = context.packageManager
        var versionCode = Uri.EMPTY.toString()
        try {
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionCode = packageInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
//            LogUtil.e(e.message)
        }
        return versionCode
    }

    /**
     * 获取应用versionName
     */
    fun getVersionName(context: Context): String {
        val packageManager = context.packageManager
        var versionName = ""
        try {
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }

    /**
     * 是否在主线程中
     *
     * @return true，则在主线程，false，则不在
     */
    val isMainThread: Boolean
        get() = Looper.getMainLooper() == Looper.myLooper()

    /**
     * 是否在主进程中（一个应用可能用多个进程）
     *
     * @return true，则在主进程，false，则不在
     */
    fun isMainProcess(context: Context?): Boolean {
        return if (context == null) {
            false
        } else TextUtils.equals(
            getProcessNameWithContext(
                context
            ), context.packageName
        )
    }

    /**
     * [获取应用程序包名信息]
     *
     * @return 当前应用的包名信息
     */
    fun getPackageName(context: Context): String {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.packageName
        } catch (e: Exception) {
//            LogUtil.e(e.stackTraceToString())
        }
        return Uri.EMPTY.toString()
    }

    /**
     * 获取进程名称
     *
     * @param context context [Context]
     * @return 进程名称
     */
    fun getProcessNameWithContext(context: Context?): String {
        try {
            if (context == null) {
                return Uri.EMPTY.toString()
            }
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE)
                ?: return Uri.EMPTY.toString()
            val pid = Process.myPid()
            var processName = Uri.EMPTY.toString()
            val activityManager = manager as ActivityManager
            for (process in activityManager.runningAppProcesses) {
                if (process.pid != pid) {
                    continue
                }
                processName = process.processName
                break
            }
            return processName
        } catch (e: Exception) {
//            LogUtil.e(e.stackTraceToString())
        }
        return Uri.EMPTY.toString()
    }

    val Context.scanForActivity: Activity?
        get() = scanForActivity(this)


    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    private fun scanForActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    val deviceBrand: String
        get() = Build.BRAND

    /**
     * 获取CPU型号
     */
    fun getCpuName(): String {
        try {
            val fr = FileReader("/proc/cpuinfo")
            BufferedReader(fr).use { localBufferedReader ->
                var line = ""
                while (localBufferedReader.readLine()?.also { line = it } != null) {
                    if (line.startsWith("Hardware") && line.contains(":")) {
                        return line.split(":".toRegex()).toTypedArray()[1]
                    }
                }
            }
        } catch (e: IOException) {
//            LogUtil.e(e.stackTraceToString())
        }
        return Uri.EMPTY.toString()
    }
}