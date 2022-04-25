package com.yubin.draw.widget.viewGroup.exposure

import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.bean.ExposureViewTraceBean
import java.util.concurrent.ConcurrentHashMap

class ExposureManager private constructor() {

    private val exposeMap: MutableMap<String, MutableList<ExposureViewTraceBean>> = ConcurrentHashMap()
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
        exposeMap[pageName] = exposures
        LogUtil.i("线程${Thread.currentThread().name} ---- add : $exposeMap")
    }

    /**
     * 删除对应页面的曝光组件
     * @param pageName 曝光页面
     */
    fun remove(pageName: String) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposeMap.remove(pageName)
        }
        LogUtil.i("线程${Thread.currentThread().name} ----  remove1 : $exposeMap")
    }


    /**
     * 删除对应页面的曝光组件的某个元素
     * @param pageName 曝光页面
     * @param eventId 曝光数据源id
     */
    fun remove(pageName: String, eventId: String) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposures.find {
                it.eventId == eventId
            }.let {
                exposures.remove(it)
            }
        }

        LogUtil.i("线程${Thread.currentThread().name} ----  remove2 : $exposeMap")
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
        LogUtil.i("线程${Thread.currentThread().name} ----  update : $exposeMap")
    }

    /**
     * 查询是否已经曝光过
     */
    fun isExposed(pageName: String, eventId: String): Boolean? {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposures.find {
                it.eventId == eventId
            }.let {
                return it?.isExpose
            }
        }

        return true
    }

    /**
     * 查找相应页面的曝光组件
     * @param pageName 曝光的页面
     */
    fun query(pageName: String): MutableList<ExposureViewTraceBean> {

        return exposeMap[pageName] ?: mutableListOf()
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
        LogUtil.i("线程${Thread.currentThread().name} ----  reset : $exposeMap")
    }

    /**
     * 重置某个页面
     */
    fun resetAll() {
        if (exposeMap.isEmpty()) return
        exposeMap.map {
            it.value
        }.flatten().filter { event.contains(it.eventId) }
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
     * 删除需要曝光的事件
     */
    fun removeEvent(eventId: String) {
        event.remove(eventId)
    }

    /**
     * 判断是否存在曝光数据
     */
    fun isExistExposureId(bean: ExposureViewTraceBean) : Boolean{

        return event.contains(bean.eventId)
    }

    /**
     * 推出app的时候清理所有需要曝光的事件
     */
    fun clearEvent() {
        event.clear()
    }
}