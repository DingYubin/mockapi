package com.yubin.baselibrary.router;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

public class CTRouteInitHelper {

    public static void initWithApplication(Application application, boolean isDebug) {
        if (isDebug) {
            ARouter.openLog();
            ARouter.openDebug();
            ARouter.printStackTrace();
        }

        ARouter.init(application);
    }

    private CTRouteInitHelper() {
    }
}
