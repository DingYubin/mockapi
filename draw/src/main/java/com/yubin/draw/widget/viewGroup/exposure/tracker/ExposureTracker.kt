package com.yubin.draw.widget.viewGroup.exposure.tracker

import com.yubin.baselibrary.util.HandlerHelper
import com.yubin.draw.widget.viewGroup.exposure.bean.ExposureTraceBean
import com.yubin.draw.widget.viewGroup.exposure.view.ExposureLayout


/**
 * 曝光控制器
 */
class ExposureTracker(private val page: String) {
    companion object {
        const val EXPOSURE_DATA = 200
    }

    private val handlerHelper =
        HandlerHelper { msg ->
            when (msg.what) {
                EXPOSURE_DATA -> {
                    if (msg.obj is ExposureTraceBean) {
                        val exposure = msg.obj as ExposureTraceBean
                        val view = exposure.view as ExposureLayout
                        view.exposure(exposure.page, exposure.exposureId)
                    }
                }
            }
            false
        }

    /**
     * 开始执行定时任务
     */
    fun startTask() {
        handlerHelper.handler.post(ExposureTask(handlerHelper.handler, page))
    }

    /**
     * 清除handler消息队列
     */
    fun clearTask() {
        handlerHelper.clear()
    }

    /**
     * 销毁现场任务
     */
    fun release() {
        handlerHelper.release()
    }

}
