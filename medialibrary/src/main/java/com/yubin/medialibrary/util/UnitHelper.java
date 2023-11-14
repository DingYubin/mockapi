package com.yubin.medialibrary.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.Nullable;


/**
 * description
 *
 * @author yubin
 * @date create at 2020/7/22 14:20
 */
public class UnitHelper {

    private UnitHelper() {
    }

    /**
     * 将dp单位转成px
     *
     * @param context context {@link Context}
     * @param value   value
     * @return 转化之后的值
     */
    public static float dp2px(@Nullable Context context, float value) {
        if (context == null) {
            return Float.MIN_EXPONENT;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, displayMetrics);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 获取屏幕Metrics参数
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

}
