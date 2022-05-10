package com.yubin.draw.widget.viewGroup.exposure.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.collection.ArrayMap
import com.yubin.draw.widget.viewGroup.exposure.handler.ExposureHandler
import com.yubin.draw.widget.viewGroup.exposure.protocol.IExposureCallback

/**
 * 定义一个Layout作为曝光组件的父布局
 */
class ExposureLayout : FrameLayout {

    /**
     * 定义曝光处理类
     */
    private val mExposureHandler by lazy {
        ExposureHandler(this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * 添加到视图
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mExposureHandler.onAttachedToWindow()
    }

    /**
     * 从视图中移除
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mExposureHandler.onDetachedFromWindow()
    }

    /**
     * 设置曝光条件 曝光区域大小，例如展示超过50%才算曝光
     */
    fun setShowRatio(ratio: Float) {
        mExposureHandler.setShowRatio(ratio)
    }

    /**
     * 设置曝光最小时间，例如必须曝光超过两秒才算曝光
     */
    fun setTimeLimit(timeLimit: Int) {
        mExposureHandler.setTimeLimit(timeLimit)
    }

    /**
     * 绑定数据
     */
    fun bindViewData(map: ArrayMap<String, Any>, isRecyclerView: Boolean) {
        mExposureHandler.bindViewData(map, isRecyclerView)
    }

    /**
     * 曝光操作
     */
    fun exposure(page: String, eventId: String){
        mExposureHandler.exposure(page, eventId)
    }

    /**
     * 曝光回调
     */
    fun setExposureCallback(callback: IExposureCallback) {
        mExposureHandler.setExposureCallback(callback)
    }
}