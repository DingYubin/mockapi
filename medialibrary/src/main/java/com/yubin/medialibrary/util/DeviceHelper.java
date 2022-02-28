package com.yubin.medialibrary.util;

import android.content.Context;

import androidx.annotation.Nullable;

/**
 * description: 开思设备辅助类
 * <p>
 * author:   侯军(A01082)
 */
public class DeviceHelper {

    /**
     * 获取屏幕宽度
     *
     * @param context context {@link Context}
     * @return 屏幕宽度
     */
    public static int screenWidthWithContext(@Nullable Context context) {
        if (context == null) {
            return 0;
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context context {@link Context}
     * @return 屏幕高度
     */
    public static int screenHeightWithContext(@Nullable Context context) {
        if (context == null) {
            return 0;
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    private DeviceHelper() {
    }



}
