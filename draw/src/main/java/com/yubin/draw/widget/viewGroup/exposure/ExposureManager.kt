package com.yubin.draw.widget.viewGroup.exposure

import androidx.collection.ArrayMap
import com.yubin.draw.bean.ExposureViewTraceBean

class ExposureManager private constructor(){

    private val map : MutableMap<String, MutableList<ExposureViewTraceBean>> = ArrayMap()

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
    fun add(pageName : String, bean : ExposureViewTraceBean) {
        val exposures = query(pageName)
        exposures.forEach {
            if (it.eventId == bean.eventId){
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
    fun remove(pageName : String) {
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
    fun remove(pageName : String, bean : ExposureViewTraceBean) {
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
    fun update(pageName : String, bean : ExposureViewTraceBean) {
        val exposures = query(pageName)
        if (exposures.isNotEmpty()) {
            exposures.find {
                it.eventId == bean.eventId
            }.let {
                it?.time = bean.time
                it?.exposed = bean.exposed
            }
        }
    }

    /**
     * 查找相应页面的曝光组件
     * @param pageName 曝光的页面
     */
    fun query(pageName : String) : MutableList<ExposureViewTraceBean> {

        return map[pageName] ?: mutableListOf()
    }
}