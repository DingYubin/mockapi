package com.yubin.draw.widget.viewGroup.exposure.manager

import com.yubin.draw.widget.viewGroup.exposure.bean.ExposureTraceBean
import com.yubin.draw.widget.viewGroup.exposure.tracker.ExposureTracker
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class ExposureManager private constructor() {

    private val exposeMap: MutableMap<String, CopyOnWriteArrayList<ExposureTraceBean>> =
        ConcurrentHashMap()
    private val exposureIds: MutableSet<String> = HashSet()
    private val tracker = ExposureTracker("exposure_activity")
    private var isRunning: Boolean = false

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
    fun add(pageName: String, bean: ExposureTraceBean) {
        val exposures = query(pageName)
        exposures.forEach {
            if (it.exposureId == bean.exposureId) {
                return
            }
        }
        exposures.add(bean)
        exposeMap[pageName] = exposures

//        startTracker()
    }

    /**
     * 删除对应页面的曝光组件
     * @param pageName 曝光页面
     */
    fun remove(pageName: String) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            removeExposureIds(exposures)
            exposeMap.remove(pageName)
        }
//        stopTracker()
    }

    /**
     * 更新相应页面的曝光组件
     * @param pageName 曝光的页面
     * @param bean 曝光数据源
     */
    fun update(pageName: String, bean: ExposureTraceBean) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposures.find {
                it.exposureId == bean.exposureId
            }.let {
                it?.timeStep = bean.timeStep
                it?.isExposeAble = bean.isExposeAble
            }
        }
    }

    /**
     * 查询是否已经曝光过
     */
    fun checkExposed(pageName: String, exposureId: String): Boolean {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposures.find {
                it.exposureId == exposureId
            }.let {
                return it?.isExposeAble ?: true
            }
        }
        return true
    }

    /**
     * 查找相应页面的曝光组件
     * @param pageName 曝光的页面
     */
    fun query(pageName: String): CopyOnWriteArrayList<ExposureTraceBean> {

        return exposeMap[pageName] ?: CopyOnWriteArrayList()
    }

    /**
     * 重置某个页面
     */
    fun reset(pageName: String) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposures.filter {
                exposureIds.contains(it.exposureId)
            }.forEach {
                it.timeStep = 0
                it.isExposeAble = true
            }
        }
    }

    /**
     * 重置某个页面
     */
    fun resetAll() {
        if (exposeMap.isEmpty()) return
        exposeMap.map {
            it.value
        }.flatten().filter { exposureIds.contains(it.exposureId) }
            .forEach {
                it.timeStep = 0
                it.isExposeAble = true
            }
    }

    /**
     * 重置某个事件
     */
    fun resetExposure(pageName: String, exposureId: String) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposures.find {
                it.exposureId == exposureId
            }.let {
                it?.timeStep = 0
                it?.isExposeAble = true
            }
        }
    }

    /**
     * 添加需要曝光的事件Id
     */
    fun addExposureId(exposureId: String) {
        exposureIds.add(exposureId)
    }


    /**
     * 退出app的时候清理所有需要曝光的事件
     */
    fun clear() {
        exposeMap.clear()
        exposureIds.clear()
//        tracker.release()
    }

    /**
     * 开启轮询器
     */
    private fun startTracker() {
        if (exposureIds.size == 1 && !isRunning) {//当曝光组件当个数为1当时候，开启曝光轮询组件
            tracker.startTask()
            isRunning = true
        }
    }

    /**
     * 停止轮询器
     */
    private fun stopTracker() {
        if (exposureIds.isEmpty() && isRunning) {//当曝光组件为空的时候，停止曝光组件
            tracker.clearTask()
            isRunning = false
        }
    }

    /**
     * 清除页面对应的曝光id
     */
    private fun removeExposureIds(bean: MutableList<ExposureTraceBean>) {
        bean.forEach {
            if (exposureIds.contains(it.exposureId)) {
                exposureIds.remove(it.exposureId)
            }
        }
    }

}