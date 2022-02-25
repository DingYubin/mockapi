package com.yubin.medialibrary.manager

import java.io.Serializable

/**
 * 拍照策略
 */
data class CameraStrategy(
    var isShowVideo0: Boolean = false,
    var maxCount0: Int = 9,
    var maxVideoSize0: Int = 1024 * 1024 * 40,
    val cameraFinder: CameraFinder? = null
) : AlbumStrategy(
    isShowVideo = isShowVideo0,
    maxCount = maxCount0,
    maxVideoSize = maxVideoSize0,
) {
    init {
        strategy = TYPE_TAKE_CAMERA
    }
}

/**
 * 设置拍照页面是否有裁剪框，默认不需要
 */
data class CameraFinder(
    val needFinder: Boolean = false,
    val width: Int = 0,
    val height: Int = 0
) : Serializable


