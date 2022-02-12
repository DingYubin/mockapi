package com.yubin.mockapi.common;

import java.util.concurrent.TimeoutException;

/**
 * description 设置忽略AssetManager finalize()方法引起的异常
 * 注意这个的初始化，要放在bugly初始化的前面
 *
 */
public class CrashUtil {

    private CrashUtil() {

    }

    public static void init() {
        final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {

            if (t.getName().equals("FinalizerWatchdogDaemon") && e instanceof TimeoutException) {
                //ignore it
//                LogUtil.i("ignore TimeoutException in thread named 'FinalizerWatchdogDaemon'  ");
            } else {
                 defaultUncaughtExceptionHandler.uncaughtException(t, e);
            }
        });
    }

}
