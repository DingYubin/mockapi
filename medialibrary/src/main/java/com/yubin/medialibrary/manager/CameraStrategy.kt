package com.yubin.medialibrary.manager

/**
 * 拍照策略
 */
data class CameraStrategy(
    var isShowVideo0: Boolean = false,
    var maxCount0: Int = 9,
    var maxVideoSize0: Int = 1024 * 1024 * 40,
) : AlbumStrategy(
    isShowVideo = isShowVideo0,
    maxCount = maxCount0,
    maxVideoSize = maxVideoSize0,
) {
    init {
        strategy = TYPE_TAKE_CAMERA
    }
}


