package com.yubin.draw.widget.viewGroup.exposure

import com.yubin.baselibrary.util.HandlerHelper
import com.yubin.baselibrary.util.LogUtil

/**
 * 曝光控制器
 */
class ExposureTracker(private val page: String) {
    companion object {
        const val EXPOSURE_DATA = 200
    }

    private val handlerHelper = HandlerHelper { msg ->
        LogUtil.i("线程: ${Thread.currentThread().name} handle message --> ${msg.what}")
        when (msg.what) {
            EXPOSURE_DATA -> {
                LogUtil.i("线程: ${Thread.currentThread().name} 进行曝光操作")
                val view = msg.obj as ExposureLayout
                view.exposePara.forEach { (key, value) -> LogUtil.i("曝光数据 --> $key: $value") }
                view.exposure()
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
        ExposureManager.instance.remove(page)
    }

    /**
     * 重置任务
     */
    fun resetTask() {
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
