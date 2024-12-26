package com.yubin.baselibrary.util

import android.content.res.Resources
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.yubin.baselibrary.startup.CassStartupInitializer

/**
 * description 字符资源转换为字符串
 *
 * @author laiwei
 * @date 2024/9/10 18:58
 * @param
 * @return
 */
fun Int.resString(): String {
    return CassStartupInitializer.appContext.getString(this)
}

/**
 * description 颜色资源转换为颜色值
 *
 * @author laiwei
 * @date 2024/9/10 18:58
 * @param
 * @return
 */
fun Int.resColor(): Int {
    return ContextCompat.getColor(CassStartupInitializer.appContext, this)
}

/**
 * description dp转换为px
 *
 * @author laiwei
 * @date 2024/9/11 11:17
 * @param
 * @return
 */
val Float.dp: Float
    get() {
        val displayMetrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, displayMetrics)
    }

/**
 * dp值转换成px
 *
 * @return
 */
fun Float.dp2px(): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

/**
 * sp值转换成px
 *
 * @return
 */
fun Float.sp2px(): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)

/**
 * dp值转换成px
 *
 * @return
 */
fun Int.dp2px(): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()

/**
 * sp值转换成px
 *
 * @return
 */
fun Int.sp2px(): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()