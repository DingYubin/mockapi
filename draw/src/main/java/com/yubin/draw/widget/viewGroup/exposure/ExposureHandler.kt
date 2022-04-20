package com.yubin.draw.widget.viewGroup.exposure

import android.view.View
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.bean.ExposureViewTraceBean

class ExposureHandler(private val view: View) {

    private var mAttachedToWindow = false //添加到视图中的状态
    private var mHasWindowFocus = true // 视图获取到焦点的状态 ，默认为true，避免某些场景不被调用
    private var mVisibilityAggregated = true //可见性的状态 ，默认为true，避免某些场景不被调用
    private var mShowRatio: Float = 0f //曝光条件超过多大面积 0~1f
    private var mTimeLimit: Int = 0 //曝光条件超过多久才算曝光，例如2秒(2000)
    private var page: String? = null

    /**
     * 添加到视图时添加OnPreDrawListener
     */
    fun onAttachedToWindow() {
        mAttachedToWindow = true
        val view = view as ExposureLayout
        val eventId = view.exposePara["eventId"]
        page?.let {
            ExposureManager.instance.add(
                it,
                ExposureViewTraceBean(eventId!!, view, 0, mShowRatio, mTimeLimit,false)
            )
        }
        LogUtil.d("添加到视图时添加 eventId : $eventId 进入屏幕")

    }

    /**
     * 从视图中移除时去掉OnPreDrawListener
     * 尝试取消曝光
     */
    fun onDetachedFromWindow() {
        mAttachedToWindow = false
        val view = view as ExposureLayout
        val eventId = view.exposePara["eventId"]
        LogUtil.d("从视图中移除时去掉 eventId : $eventId 移除屏幕")
    }

    /**
     * 视图焦点改变
     * 尝试取消曝光
     */
    fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        mHasWindowFocus = hasWindowFocus
//        LogUtil.d("视图焦点改变 hasWindowFocus = $hasWindowFocus")

    }

    /**
     * 可见性改变
     * 尝试取消曝光
     */
    fun onVisibilityAggregated(isVisible: Boolean) {
        mVisibilityAggregated = isVisible
//        LogUtil.d("可见性改变 isVisible = $isVisible")

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

}

