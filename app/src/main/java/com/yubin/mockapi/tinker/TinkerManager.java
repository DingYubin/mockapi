package com.yubin.mockapi.tinker;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.beta.tinker.TinkerLoadReporter;
import com.tencent.bugly.beta.tinker.TinkerPatchListener;
import com.tencent.bugly.beta.tinker.TinkerPatchReporter;
import com.tencent.bugly.beta.tinker.TinkerResultService;
import com.tencent.bugly.beta.tinker.TinkerUncaughtExceptionHandler;
import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.util.UpgradePatchRetry;
import com.yubin.baselibrary.util.LogUtil;
import com.yubin.mockapi.MainApplication;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/08/09
 *     desc    : 对Tinker进行封装
 *     version : 1.0
 * </pre>
 */
public class TinkerManager {

    private static UncaughtExceptionHandler systemExceptionHandler;
    private static TinkerUncaughtExceptionHandler uncaughtExceptionHandler;

    //是否初始化Tinker
    private static boolean isInstalled = false;

    private static TinkerManager tinkerManager = new TinkerManager();
    private static boolean patchRestartOnScreenOff = true;

    private ApplicationLike applicationLike;

    private Application application;

    public TinkerManager() {
    }

    public static TinkerManager getInstance() {
        return tinkerManager;
    }

    /**
     * 初始化Tinker
     */
    public static void installTinker(Object tinkerApplicationLikeObject) {

        if (tinkerApplicationLikeObject == null) {
            Log.e("Tinker.TinkerManager", "Tinker ApplicationLike is null");
        } else {
            if (tinkerApplicationLikeObject instanceof ApplicationLike) {
                installDefaultTinker((ApplicationLike)tinkerApplicationLikeObject);
            } else {
                Log.e("Tinker.TinkerManager", "NOT tinker ApplicationLike object");
            }
        }
    }

    public static void installDefaultTinker(ApplicationLike appLike) {
        if (isInstalled) {
            return;
        }

        Application application = appLike.getApplication();
        if (application instanceof MainApplication) {
            LogUtil.i("Tinker === TinkerManager installDefaultTinker is MainApplication");
        }

        getInstance().setTinkerApplicationLike(appLike);
        registJavaCrashHandler();
        setUpgradeRetryEnable(true);
        TinkerLoadReporter var1 = new TinkerLoadReporter(appLike.getApplication());
        TinkerPatchReporter var2 = new TinkerPatchReporter(appLike.getApplication());
        TinkerPatchListener var3 = new TinkerPatchListener(appLike.getApplication());
        UpgradePatch var4 = new UpgradePatch();
        Tinker var5 = TinkerInstaller.install(appLike, var1, var2, var3, TinkerResultService.class, var4);
//        TinkerInstaller.install(appLike);
        if (var5 != null) {
            isInstalled = true;
        }
    }

    private void setTinkerApplicationLike(ApplicationLike applicationLike) {
        this.applicationLike = applicationLike;
        if (applicationLike != null) {
            this.application = applicationLike.getApplication();
        }
    }

    public static ApplicationLike getTinkerApplicationLike() {
        return getInstance().applicationLike;
    }

    /**
     * 加载补丁文件
     *
     * @param path
     */
    public static void loadPatchPatch(String path) {

        if (Tinker.isTinkerInstalled()) {
            TinkerInstaller.onReceiveUpgradePatch(getInstance().application.getApplicationContext(), path);
        }
    }

    public static void setPatchRestartOnScreenOff(boolean patchRestartOnScreenOff) {
        TinkerManager.patchRestartOnScreenOff = patchRestartOnScreenOff;
    }

    public static void registJavaCrashHandler() {
        if (uncaughtExceptionHandler == null) {
            systemExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
            uncaughtExceptionHandler = new TinkerUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
        }

    }

    public static void unregistJavaCrashHandler() {
        if (systemExceptionHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(systemExceptionHandler);
        }
    }

    public static void setUpgradeRetryEnable(boolean enable) {
        UpgradePatchRetry.getInstance(getTinkerApplicationLike().getApplication()).setRetryEnable(enable);
    }

    public static boolean isPatchRestartOnScreenOff() {
        return patchRestartOnScreenOff;
    }
}
