package com.yubin.medialibrary.manager

import android.graphics.Bitmap

/**
 * 图片策略
 */
open class AlbumStrategy(
    var isShowVideo: Boolean = false,
    var maxCount: Int = 9,
    var maxVideoSize: Int = 1024 * 1024 * 40,
) : MediaStrategy(TYPE_OPEN_ALBUM) {
    var maxWidth: Int = 1080
    var maxHeight: Int = 1920
    var quality: Float = 1F
    //右下角按钮的文本
    var selectedBtnText:String = "发送"
    var fileType: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG

    companion object {
        /**
         * 不压缩模式，直接返回原图
         */
        const val QUALITY_ORIGIN = -1F
    }
}


