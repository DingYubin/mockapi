package com.yubin.baselibrary.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.yubin.baselibrary.core.BaseApplication;

public class ResourceUtil {
    private ResourceUtil() {
    }

    public static String getString(Context context, int resid) {
        if (context != null) {
            return context.getResources().getString(resid);
        } else {
            return BaseApplication.context.getResources().getString(resid);
        }
    }

    public static String getString(@StringRes int resourceId) {
        return BaseApplication.context.getResources().getString(resourceId);
    }


    public static String[] getStringAry(@ArrayRes int resourceId) {
        return BaseApplication.context.getResources().getStringArray(resourceId);
    }
    /**
     * 获取drawable
     *
     * @param resId 图片的资源id
     * @return drawable对象
     */
    public static Drawable getDrawable(int resId) {
        return ContextCompat.getDrawable(BaseApplication.context, resId);
    }


    public static int getColor(@ColorRes int resourceId) {
        return BaseApplication.context.getResources().getColor(resourceId, null);
    }

    /**
     * 获取dimen
     *
     * @param resId 尺寸资源id
     * @return
     */
    public static int getDimens(@DimenRes int resId) {
        return BaseApplication.context.getResources().getDimensionPixelSize(resId);
    }

}
