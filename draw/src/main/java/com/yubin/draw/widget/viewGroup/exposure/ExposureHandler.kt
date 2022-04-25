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

    private var mHasWindowFocus = true // 视图获取到焦点的状态 ，默认为true，避免某些场景不被调用
    private var mVisibilityAggregated = true //可见性的状态 ，默认为true，避免某些场景不被调用
    private var mShowRatio: Float = 0f //曝光条件超过多大面积 0~1f
    private var mTimeLimit: Int = 0 //曝光条件超过多久才算曝光，例如2秒(2000)
    private var page: String? = null//曝光页面
    private var mExposureCallback: IExposureCallback? = null //曝光回调
    private var isRecyclerView: Boolean = false//判断是否是recyclerView

    /**
     * 添加到视图时添加OnPreDrawListener
     * RecyclerView
     * 1、有数据情况，需要判断
     *      已经曝光过，则不进行任何操作
     *      未曝光过，则增加新绑定的数据
     * 2、没有数据到情况下，什么都不做
     * View 其他情况，添加操作
     */
    fun onAttachedToWindow() {
        if (isBindView() == false) {
            return
        }

        if (isRecyclerView && page != null) {
            val eventId = exposePara?.get("eventId") as String
            val isExpose = ExposureManager.instance.isExposed(page!!, eventId)
            LogUtil.d("添加到视图时添加 eventId : $eventId, isExpose : $isExpose")
            ExposureManager.instance.add(
                page!!,
                ExposureViewTraceBean(eventId, view, 0, mShowRatio, mTimeLimit, isExpose!!)
            )
            if (isExpose) {
                ExposureManager.instance.addEvent(eventId)
            }
            return
        }

        page?.let {
            val isExpose = exposePara?.get("isExpose") as Boolean
            val eventId = exposePara?.get("eventId") as String
            LogUtil.d("添加到视图时添加 eventId : $eventId, isExpose : $isExpose")
            ExposureManager.instance.add(
                it,
                ExposureViewTraceBean(eventId, view, 0, mShowRatio, mTimeLimit, isExpose)
            )

            if (isExpose) {
                ExposureManager.instance.addEvent(eventId)
            }
        }
    }

    /**
     * 从视图中移除时去掉OnPreDrawListener
     * 尝试取消曝光
     */
    fun onDetachedFromWindow() {

        if (isRecyclerView && isBindView() == true && page != null) {
            val eventId = exposePara?.get("eventId") as String
            val isExposed = ExposureManager.instance.isExposed(page!!, eventId)
            LogUtil.d("删除到视图时添加 eventId : $eventId, isExpose : $isExposed")

            if (isExposed == false && isRecyclerView) {//判断是否曝光过，若果没有曝光过，并且是 recyclerView, 进行解绑操作
                exposePara?.clear()
            }
        }

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
     * 如果是recyclerView 需要判断该数据是否还在绑定的view上面
     */
    fun exposure() {
        LogUtil.i("曝光操作: ${Thread.currentThread().name} 线程操作")
//        ExposureManager.instance.isExposed
//        exposePara?.forEach { (key, value) -> LogUtil.i("曝光数据 --> $key: $value") }

        if (isRecyclerView && isBindView() == false) {
            return
        }

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
    fun bindViewData(map: ArrayMap<String, Any>, isRecyclerView: Boolean) {
        this.isRecyclerView = isRecyclerView
        this.exposePara = map
    }

    /**
     * 曝光回调
     */
    fun setExposureCallback(callback: IExposureCallback) {
        mExposureCallback = callback
    }

    /**
     * 判断当前view是否处于绑定状态
     */
    private fun isBindView(): Boolean? {

        return exposePara?.isNotEmpty()
    }

}

