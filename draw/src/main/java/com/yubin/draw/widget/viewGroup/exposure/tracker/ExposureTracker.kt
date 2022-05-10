package com.yubin.draw.widget.viewGroup.exposure.tracker

import com.yubin.baselibrary.util.HandlerHelper
import com.yubin.draw.bean.ExposureViewTraceBean
import com.yubin.draw.widget.viewGroup.exposure.manager.ExposureManager
import com.yubin.draw.widget.viewGroup.exposure.view.ExposureLayout


/**
 * 曝光控制器
 */
class ExposureTracker(private val page: String) {
    companion object {
        const val EXPOSURE_DATA = 200
    }

    private val handlerHelper = HandlerHelper { msg ->
        when (msg.what) {
            EXPOSURE_DATA -> {
                if (msg.obj is ExposureViewTraceBean) {
                    val exposure = msg.obj as ExposureViewTraceBean
                    val view = exposure.view as ExposureLayout
                    view.exposure(exposure.page, exposure.eventId)
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
     * 重置
     */
    fun reset() {
        ExposureManager.instance.reset(page)
    }

    /**
     * 对应页面的曝光组件
     */
    fun refresh() {
        ExposureManager.instance.remove(page)
    }

    /**
     * 销毁现场任务
     */
    fun release() {
        handlerHelper.release()
        ExposureManager.instance.remove(page)
    }

}
