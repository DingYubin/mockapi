package com.yubin.mvx.databinding.dagger;

import android.app.Application;

/**
 * <pre>
 *     @author : dingyubin
 *     time   : 2018-1-16
 *     desc   : 初始化
 *     version: 1.0
 * </pre>
 */

public class AppInjector {
    private static AppComponent appComponent;

    private AppInjector() {
        //no instance
    }

    public static void init(Application app) {
//        DaggerAppCompaonent
        appComponent.inject(app);
    }
}
