package com.yubin.draw.widget.viewGroup.exposure

import android.graphics.Rect
import android.os.Handler
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.bean.ExposureViewTraceBean
import com.yubin.draw.widget.viewGroup.exposure.ExposureTracker.Companion.EXPOSURE_DATA

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
        handler.postDelayed(this, (5 * 100).toLong()) //延迟5秒,再次执行task本身,实现了500ms一次的循环效果
    }

    /**
     * 曝光操作
     * 对于没有曝光的数据，在面积区域内，则进行数据更新操作，否则步长(time)重新开始计算
     */
    private fun exposureView(it: ExposureViewTraceBean) {
        if (!it.exposed) {
            //在面积区域内，则进行数据更新操作，否则步长重新开始计算
            if (isExposureArea(it)) {
                //不满足条件的，更新数据
                if (it.time < it.mTimeLimit) {
                    it.time = (it.time + 500)
                } else {
                    //曝光条件满足，执行曝光任务
                    LogUtil.i("线程: ${Thread.currentThread().name} 满足曝光条件: 可以进行曝光")
                    handler.obtainMessage(EXPOSURE_DATA, it.view).sendToTarget()
                    it.exposed = true
                }
            } else {
                it.time = 0
            }
            ExposureManager.instance.update(page, it)
        }
    }

    /**
     * 判断是否在曝光区域内
     */
    private fun isExposureArea(bean: ExposureViewTraceBean): Boolean {
        val mRect = Rect()
        val view = bean.view
        val mShowRatio = bean.area
        val visible = view.getLocalVisibleRect(mRect) && view.isShown
        if (!visible) {
            return false
        }

        if (bean.area > 0) {
            if (kotlin.math.abs(mRect.bottom - mRect.top) > view.height * mShowRatio
                && kotlin.math.abs(mRect.right - mRect.left) > view.width * mShowRatio
            ) {
                return true
            }
        }
        return false
    }
}