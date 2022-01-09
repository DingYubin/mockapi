package com.yubin.baselibrary.util

import android.content.Context
import android.util.TypedValue

/**
 * description: 单位辅助类
 */
object CMUnitHelper {
    /**
     * 将dp单位转成px
     *
     * @param context context [Context]
     * @param value   value
     * @return 转化之后的值
     */
    fun dp2px(context: Context, value: Float): Float {

        if (context == null) {
            return java.lang.Float.MIN_EXPONENT.toFloat()
        }
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, displayMetrics)
    }

    /**
     * 将sp单位转成px
     *
     * @param context context [Context]
     * @param value   value
     * @return 转化之后的值
     */
    fun sp2px(context: Context?, value: Float): Float {
        if (context == null) {
            return java.lang.Float.MIN_EXPONENT.toFloat()
        }
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, displayMetrics)
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dp(context: Context, pxValue: Float): Int {
        val density = context.resources.displayMetrics.density
        return (pxValue / density + 0.5f).toInt()
    }

}