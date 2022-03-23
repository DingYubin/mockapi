package com.yubin.baselibrary.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.Display
import android.view.WindowManager

/**
 * description: 单位辅助类
 */
object CMUnitHelper {

    /**
     * 将dp单位转成px
     *
     * @param value value
     * @return 转化之后的值
     */
    fun dp2px(value: Float): Float {
        val displayMetrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, displayMetrics)
    }

    /**
     * 将sp单位转成px
     * @param value   value
     * @return 转化之后的值
     */
    fun sp2px(value: Float): Float {

        val displayMetrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, displayMetrics)
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    fun px2sp(pxValue: Float): Int {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dp(pxValue: Float): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (pxValue / density + 0.5f).toInt()
    }

    fun getRealScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size)
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x =
                    (Display::class.java.getMethod("getRawWidth").invoke(display) as Int)
                size.y =
                    (Display::class.java.getMethod("getRawHeight").invoke(display) as Int)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return size
    }

}