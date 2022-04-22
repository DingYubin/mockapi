package com.yubin.draw.widget.viewGroup.exposure

import androidx.collection.ArrayMap
import com.yubin.draw.bean.ExposureViewTraceBean

class ExposureManager private constructor() {

    private val map: MutableMap<String, MutableList<ExposureViewTraceBean>> = ArrayMap()
    private val event: MutableSet<String> = HashSet()

    companion object {
        val instance = Helper.instance
    }

    private object Helper {
        val instance = ExposureManager()
    }

    /**
     * 添加相应页面的曝光组件
     * @param pageName 曝光的页面
     * @param bean 曝光数据源
     */
    fun add(pageName: String, bean: ExposureViewTraceBean) {
        val exposures = query(pageName)
        exposures.forEach {
            if (it.eventId == bean.eventId) {
                return
            }
        }
        exposures.add(bean)
        map[pageName] = exposures
    }

    /**
     * 删除对应页面的曝光组件
     * @param pageName 曝光页面
     */
    fun remove(pageName: String) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            map.remove(pageName)
        }
    }

    /**
     * 删除对应页面的曝光组件的某个元素
     * @param pageName 曝光页面
     * @param bean 曝光数据源
     */
    fun remove(pageName: String, bean: ExposureViewTraceBean) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposures.remove(bean)
        }
    }

    /**
     * 更新相应页面的曝光组件
     * @param pageName 曝光的页面
     * @param bean 曝光数据源
     */
    fun update(pageName: String, bean: ExposureViewTraceBean) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposures.find {
                it.eventId == bean.eventId
            }.let {
                it?.time = bean.time
                it?.isExpose = bean.isExpose
            }
        }
    }

    /**
     * 查找相应页面的曝光组件
     * @param pageName 曝光的页面
     */
    fun query(pageName: String): MutableList<ExposureViewTraceBean> {

        return map[pageName] ?: mutableListOf()
    }

    /**
     * 重置某个页面
     */
    fun reset(pageName: String) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposures.filter {
                event.contains(it.eventId)
            }.forEach {
                it.time = 0
                it.isExpose = true
            }
        }
    }

    /**
     * 重置某个页面
     */
    fun resetAll(pages: Array<String>) {
        if (pages.isNullOrEmpty()) return

        pages.map {
            query(it)
        }.filter {
            it.isNotEmpty()
        }.flatten().filter { event.contains(it.eventId)}
            .forEach {
            it.time = 0
            it.isExpose = true
        }
    }

    /**
     * 添加需要曝光的事件
     */
    fun addEvent(eventId: String) {
        event.add(eventId)
    }

    /**
     * 推出app的时候清理所有需要曝光的事件
     */
    fun clearEvent() {
        event.clear()
    }
}