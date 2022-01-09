package com.yubin.baselibrary.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue

/**
 * description: 屏幕相关
 * date 1/14/21 11:45 AM.
 */
object CMDisplayHelper {

    /**
     * 获取屏幕尺寸
     */
    fun getScreenSize(context: Context): Point {
        val size = Point()
//        context.display?.getRealSize(size)
        val dm = context.resources.displayMetrics
        size.x = dm.widthPixels
        size.y = dm.heightPixels
        return size
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    val statusBarHeight: Int
        get() {
            // 获得状态栏高度
            val resourceId =
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
            return Resources.getSystem().getDimensionPixelSize(resourceId)
        }

    /**
     * 根据手机的分辨率获取dp像素
     */
    val Int.dp: Int
        get() {
            val displayMetrics = Resources.getSystem().displayMetrics
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this.toFloat(),
                displayMetrics
            ).toInt()
        }

    /**
     * 根据手机的分辨率获取dp像素
     */
    val Float.dp: Int
        get() {
            val displayMetrics = Resources.getSystem().displayMetrics
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this,
                displayMetrics
            ).toInt()
        }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    val Float.px2sp: Int
        get() {
            val fontScale = Resources.getSystem().displayMetrics.scaledDensity
            return (this / fontScale + 0.5f).toInt()
        }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    val Float.px2dp: Int
        get() {
            val density = Resources.getSystem().displayMetrics.density
            return (this / density + 0.5f).toInt()
        }
}