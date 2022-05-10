package com.yubin.draw.widget.viewGroup.exposure.handler

import android.graphics.Color
import android.view.View
import androidx.collection.ArrayMap
import com.yubin.baselibrary.util.LogUtil
import com.yubin.baselibrary.util.RxTimer
import com.yubin.draw.BuildConfig
import com.yubin.draw.widget.viewGroup.exposure.bean.ExposureTraceBean
import com.yubin.draw.widget.viewGroup.exposure.manager.ExposureManager
import com.yubin.draw.widget.viewGroup.exposure.protocol.IExposureCallback
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 曝光操作
 */
class ExposureHandler(private val view: View) {
    private var exposePara: ArrayMap<String, Any>? = null
    private var mShowRatio: Float = 0.5f //曝光条件超过多大面积 0~1f
    private var mExposureCallback: IExposureCallback? = null //曝光回调
    private var isRecyclerView: Boolean = false//判断是否是recyclerView
    private val isExposed: AtomicBoolean = AtomicBoolean(true)//是否曝光过
    private var mTimeLimit: Int = 2000 //曝光条件超过多久才算曝光，例如2秒(2000)

    /**
     * 视图view进入window
     */
    fun onAttachedToWindow() {
        if (isBindView() == false) {
            return
        }
        addViewToExposure()
    }

    /**
     * 视图view移除window
     */
    fun onDetachedFromWindow() {
        if (isBindView() == true && isRecyclerView) {
            removeExposeCache()
        }
    }

    /**
     * 添加视图到曝光区域
     * RecyclerView
     * 1、有数据情况，需要判断
     *      已经曝光过，则不进行任何操作
     *      未曝光过，则增加新绑定的数据
     * 2、没有数据到情况下，什么都不做
     * View 其他情况，添加操作
     */
    private fun addViewToExposure() {
        val pageName = if (exposePara?.contains("pageName") == true) {
            exposePara?.get("pageName") as String
        } else ""

        val exposureId = if (exposePara?.contains("exposureId") == true) {
            exposePara?.get("exposureId") as String
        } else ""

        if (pageName.isNotEmpty() && exposureId.isNotEmpty()) {
            val isExposeAble = if (isRecyclerView) {
                ExposureManager.instance.checkExposed(pageName, exposureId)
            } else {
                exposePara?.get("isExposeAble") == true
            }
            LogUtil.i("线程 --- ${Thread.currentThread().name} 曝光 exposureId = $exposureId , isExposeAble = $isExposeAble")
            addExposure(exposureId, isExposeAble, pageName)
        }
    }

    /**
     * 移除缓存区域里数据
     */
    private fun removeExposeCache() {

        val pageName = if (exposePara?.contains("pageName") == true) {
            exposePara?.get("pageName") as String
        } else ""

        val exposureId = if (exposePara?.contains("exposureId") == true) {
            exposePara?.get("exposureId") as String
        } else ""

        if (pageName.isNotEmpty() && exposureId.isNotEmpty()) {
            val isExposeAble = ExposureManager.instance.checkExposed(pageName, exposureId)
            if (isExposeAble) { //判断是否曝光过，若果没有曝光过，并且是 recyclerView, 进行解绑操作
                exposePara?.clear()
            }
        }
    }

    /**
     * 设置曝光时间限制条件
     * 回调给主界面进行上报数据
     */
    fun exposure(page: String, exposureId: String) {

        if (isRecyclerView && isBindView() == false && !isExposed.get()) {
            ExposureManager.instance.resetExposure(page, exposureId)
            return
        }

        //调试模式下 曝光闪烁3次
        if (BuildConfig.DEBUG) {
            val rxTimer = RxTimer()
            rxTimer.interval(0, 500) { number ->

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
        }

        LogUtil.i("线程 --- ${Thread.currentThread().name} 完成曝光数据上报成功 --> $exposePara")

        mExposureCallback?.exposure()
        isExposed.set(true)
    }

    /**
     * 添加曝光
     */
    private fun addExposure(
        exposureId: String,
        isExposeAble: Boolean,
        pageName: String
    ) {
        if (exposureId.isNotEmpty()) {
            val bean = ExposureTraceBean(
                pageName,
                exposureId,
                view,
                0,
                mShowRatio,
                mTimeLimit,
                isExposeAble
            )
            ExposureManager.instance.add(
                pageName,
                bean
            )
            if (isExposeAble) ExposureManager.instance.addExposureId(exposureId)
        }
    }

    /**
     * 设置曝光时间限制条件
     */
    fun setTimeLimit(index: Int) {
        this.mTimeLimit = index
    }

    /**
     * 设置曝光面积占比
     */
    fun setShowRatio(ratio: Float) {
        this.mShowRatio = ratio
    }

    /**
     * 设置曝光时间限制条件
     */
    fun bindViewData(map: ArrayMap<String, Any>, isRecyclerView: Boolean) {
        this.isRecyclerView = isRecyclerView
        this.exposePara = map
        isExposed.set(false)
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

