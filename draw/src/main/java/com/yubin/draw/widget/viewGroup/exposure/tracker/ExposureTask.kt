package com.yubin.draw.widget.viewGroup.exposure.tracker

import android.graphics.Rect
import android.os.Handler
import android.view.View
import com.yubin.draw.bean.ExposureViewTraceBean
import com.yubin.draw.widget.viewGroup.exposure.manager.ExposureManager
import com.yubin.draw.widget.viewGroup.exposure.tracker.ExposureTracker.Companion.EXPOSURE_DATA
import com.yubin.draw.widget.viewGroup.exposure.utils.ExposureHelper

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/04/20
 * desc    :
 * 1、检查是否有满足曝光条件，如果有满足的则上报曝光内容给神策
 * 2、判断是否曝光过，视图是否在曝光区域内，如果不在曝光区域内，重新计数，在曝光区域内，进行曝光
 * 3、累加时间步长
 * version : 1.0
</pre> *
 */
class ExposureTask(private val handler: Handler, private val page: String) : Runnable {

    override fun run() {

        val exposure = ExposureManager.instance.query(page)
        exposure.forEach {
            exposureView(it)
        }

        // 每5s重复一次
        handler.postDelayed(this, (3 * 100).toLong()) //延迟3秒,再次执行task本身,实现了500ms一次的循环效果
    }

    /**
     * 曝光操作
     * 对于没有曝光的数据，在面积区域内，则进行数据更新操作，否则步长(time)重新开始计算
     */
    private fun exposureView(it: ExposureViewTraceBean) {

        if (it.isExpose) {
            //在面积区域内，则进行数据更新操作，否则步长重新开始计算
            if (isExposureArea(it)) {
                //不满足条件的，更新数据
                if (it.time < it.mTimeLimit) {
                    it.time = (it.time + 300)
                } else {
                    //曝光条件满足，执行曝光任务
                    handler.obtainMessage(EXPOSURE_DATA, it).sendToTarget()
                    it.isExpose = false
                }
            } else {
                it.time = 0
            }
            ExposureManager.instance.update(page, it)
        }
    }

    /**
     * 判断是否在曝光区域内
     * 检查曝光数据是否还存在
     */
    private fun isExposureArea(bean: ExposureViewTraceBean): Boolean {
        val view = bean.view
        val mShowRatio = bean.area
        val mRect = Rect()
        val mlRect = Rect()
        if (view.visibility != View.VISIBLE ||
            !view.isShown || !view.getGlobalVisibleRect(mRect) || !view.getLocalVisibleRect(mlRect)
        ) {
            return false
        }

        if (mShowRatio > 0) {

            val visibleHeightEnough = if (ExposureHelper.exposureTopHigh > 0) {
                ExposureHelper.exposureTopHigh > kotlin.math.abs(mRect.bottom - view.measuredHeight * mShowRatio)
            } else {
                kotlin.math.abs(mlRect.bottom - mlRect.top) > view.height * mShowRatio
            }

            val visibleWidthEnough =
                kotlin.math.abs(mlRect.right - mlRect.left) > view.width * mShowRatio

            return visibleHeightEnough && visibleWidthEnough
        }

        return false
    }
}