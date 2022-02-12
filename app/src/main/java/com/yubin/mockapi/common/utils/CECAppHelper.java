package com.yubin.mockapi.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CECAppHelper {

    /**
     * 获取应用versionCode
     */
    public static String getVersionCode(@NonNull Context context) {
        PackageManager packageManager = context.getPackageManager();
        String versionCode = Uri.EMPTY.toString();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
//            LogUtil.e(e.getMessage());
        }
        return versionCode;
    }

    /**
     * 获取应用versionName
     */
    public static String getVersionName(@NonNull Context context) {
        PackageManager packageManager = context.getPackageManager();
        String versionName = "";
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 是否在主线程中
     *
     * @return true，则在主线程，false，则不在
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * 是否在主进程中（一个应用可能用多个进程）
     *
     * @return true，则在主进程，false，则不在
     */
    public static boolean isMainProcess(@Nullable Context context) {
        if (context == null) {
            return false;
        }
        return TextUtils.equals(getProcessNameWithContext(context), context.getPackageName());
    }

    /**
     * [获取应用程序包名信息]
     *
     * @return 当前应用的包名信息
     */
    public static String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
//            LogUtil.e(e.getMessage());
        }
        return null;
    }

    /**
     * 获取进程名称
     *
     * @param context context {@link Context}
     * @return 进程名称
     */
    public static String getProcessNameWithContext(@Nullable Context context) {
        try {
            if (context == null) {
                return Uri.EMPTY.toString();
            }
            Object manager = context.getSystemService(Context.ACTIVITY_SERVICE);
            if (manager == null) {
                return Uri.EMPTY.toString();
            }
            int pid = Process.myPid();
            String processName = Uri.EMPTY.toString();
            ActivityManager activityManager = (ActivityManager) manager;
            for (ActivityManager.RunningAppProcessInfo process : activityManager.getRunningAppProcesses()) {
                if (process.pid != pid) {
                    continue;
                }
                processName = process.processName;
                break;
            }
            return processName;
        } catch (Exception e) {
//            LogUtil.e(e.getMessage());
        }
        return Uri.EMPTY.toString();
    }

    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }


    /**
     * 获取CPU型号
     *
     * @return
     */
    public static String getCpuName() {

        String str1 = "/proc/cpuinfo";
        String str2 = "";

        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr);
            while ((str2 = localBufferedReader.readLine()) != null) {
                if (str2.contains("Hardware")) {
                    return str2.split(":")[1];
                }
            }
            localBufferedReader.close();
        } catch (IOException e) {
//            LogUtil.e(e.getMessage());
        }
        return Uri.EMPTY.toString();

    }

    /**
     * 获取CPU型号
     *
     * @return
     */
    public static String getProcessor() {

        String str1 = "/proc/cpuinfo";
        String str2 = "";

        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr);
            while ((str2 = localBufferedReader.readLine()) != null) {
                if (str2.contains("Processor")) {
                    return str2.split(":")[1];
                }
            }
            localBufferedReader.close();
        } catch (IOException e) {
//            LogUtil.e(e.getMessage());
        }
        return Uri.EMPTY.toString();

    }


    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    private CECAppHelper() {
    }
}
