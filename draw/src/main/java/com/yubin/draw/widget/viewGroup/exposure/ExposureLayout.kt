package com.yubin.draw.widget.viewGroup.exposure

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.collection.ArrayMap
import com.yubin.baselibrary.util.LogUtil
import com.yubin.baselibrary.util.RxTimer

/**
 * 定义一个Layout作为曝光组件的父布局
 */
class ExposureLayout : FrameLayout {

    lateinit var exposePara: ArrayMap<String, String>

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
     * 视图焦点改变
     */
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        mExposureHandler.onWindowFocusChanged(hasWindowFocus)
    }

    /**
     * 视图可见性
     */
    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        mExposureHandler.onVisibilityAggregated(isVisible)
    }

    /**
     * 设置曝光条件 曝光区域大小，例如展示超过50%才算曝光
     */
    fun setShowRatio(ratio: Float) {
        mExposureHandler.setShowRatio(ratio)
    }

    /**
     * 设置要曝光的页面
     */
    fun setPage(pageName: String) {
        mExposureHandler.setPage(pageName)
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
    fun bindViewData(map: ArrayMap<String, String>) {
        exposePara = map
    }

    /**
     * 曝光操作
     * 闪屏操作
     */
    fun exposure(){
        val rxTimer = RxTimer()
        rxTimer.interval(0, 500) { number ->
            LogUtil.i("曝光操作: ${Thread.currentThread().name} 第 $number 次")
            if (number % 2 == 0L) {
                this.setBackgroundColor(Color.parseColor("#00ff00"))
            } else {
                this.setBackgroundColor(Color.parseColor("#F0F0F0"))
            }
            if (number >= 5) {
                rxTimer.cancel()
                this.setBackgroundColor(Color.parseColor("#00F0F0F0"))
            }
        }
    }
}