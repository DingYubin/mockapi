package com.yubin.draw.widget.viewGroup.exposure

import android.graphics.Color
import android.view.View
import androidx.collection.ArrayMap
import com.yubin.baselibrary.util.LogUtil
import com.yubin.baselibrary.util.RxTimer
import com.yubin.draw.bean.ExposureViewTraceBean

/**
 * 曝光操作
 */
class ExposureHandler(private val view: View) {
    private var exposePara: ArrayMap<String, Any>? = null

    private var mAttachedToWindow = false //添加到视图中的状态
    private var mHasWindowFocus = true // 视图获取到焦点的状态 ，默认为true，避免某些场景不被调用
    private var mVisibilityAggregated = true //可见性的状态 ，默认为true，避免某些场景不被调用
    private var mShowRatio: Float = 0f //曝光条件超过多大面积 0~1f
    private var mTimeLimit: Int = 0 //曝光条件超过多久才算曝光，例如2秒(2000)
    private var page: String? = null//曝光页面
    private var mExposureCallback: IExposureCallback? = null //曝光回调

    /**
     * 添加到视图时添加OnPreDrawListener
     */
    fun onAttachedToWindow() {
        mAttachedToWindow = true
        page?.let {
            val isExpose = exposePara?.get("isExpose") as Boolean
            val eventId = exposePara?.get("eventId") as String
            LogUtil.d("添加到视图时添加 eventId : $eventId, isExpose : $isExpose")
            ExposureManager.instance.add(
                it,
                ExposureViewTraceBean(eventId, view, 0, mShowRatio, mTimeLimit, isExpose)
            )
        }
    }

    /**
     * 从视图中移除时去掉OnPreDrawListener
     * 尝试取消曝光
     */
    fun onDetachedFromWindow() {
        mAttachedToWindow = false
//        val eventId = exposePara?.get("eventId")
//        eventId?.let{
//            LogUtil.d("从视图中移除时去掉 eventId : $eventId 移除屏幕 view: ${view.hashCode()}")
//        }
    }

    /**
     * 视图焦点改变
     * 尝试取消曝光
     */
    fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        mHasWindowFocus = hasWindowFocus
    }

    /**
     * 可见性改变
     * 尝试取消曝光
     */
    fun onVisibilityAggregated(isVisible: Boolean) {
        mVisibilityAggregated = isVisible
    }

    /**
     * 设置曝光时间限制条件
     * 回调给主界面进行上报数据
     */
    fun exposure() {
        LogUtil.i("曝光操作: ${Thread.currentThread().name} 线程操作")
//        exposePara?.forEach { (key, value) -> LogUtil.i("曝光数据 --> $key: $value") }

        val rxTimer = RxTimer()
        rxTimer.interval(0, 500) { number ->
//            LogUtil.i("曝光操作: ${Thread.currentThread().name} 第 $number 次")
            if (number % 2 == 0L) {
                view.setBackgroundColor(Color.parseColor("#00ff00"))
            } else {
                view.setBackgroundColor(Color.parseColor("#F0F0F0"))
            }
            if (number >= 5) {
                rxTimer.cancel()
                view.setBackgroundColor(Color.parseColor("#00F0F0F0"))
            }
        }

        mExposureCallback?.exposure()
    }

    /**
     * 设置要曝光的页面
     */
    fun setPage(pageName: String) {
        page = pageName
    }

    /**
     * 设置曝光面积条件
     */
    fun setShowRatio(area: Float) {
        mShowRatio = area
    }

    /**
     * 设置曝光时间限制条件
     */
    fun setTimeLimit(index: Int) {
        this.mTimeLimit = index
    }

    /**
     * 设置曝光时间限制条件
     */
    fun bindViewData(map: ArrayMap<String, Any>) {
        this.exposePara = map
    }

    /**
     * 曝光回调
     */
    fun setExposureCallback(callback: IExposureCallback) {
        mExposureCallback = callback
    }

}

