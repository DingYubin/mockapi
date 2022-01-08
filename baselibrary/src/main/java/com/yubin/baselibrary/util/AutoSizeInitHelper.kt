package com.yubin.baselibrary.util

import me.jessyan.autosize.AutoSizeConfig

/**
 * description: AutoSize初始化
 */
object AutoSizeInitHelper {

    fun init(): Boolean {
        AutoSizeConfig.getInstance()
            .setLog(true)
            .setExcludeFontScale(true).isUseDeviceSize = false //限制系统对APP的字体大小修改
        return true
    }
}