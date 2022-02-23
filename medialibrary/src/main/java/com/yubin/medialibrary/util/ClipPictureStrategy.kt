package com.yubin.medialibrary.util

import java.io.Serializable

/**
 * 裁剪图片策略
 */
open class ClipPictureStrategy(
        val clipmsg: String = "",//二维码矩形底部文案
        val screenHeightFloat: Float = 0.3f,//二维码矩形距离顶部占据屏幕高度的百分比（默认距离屏幕顶部的距离为屏幕高度的30%）
        val screenWidthFloat: Float = 0.6f,//二维码矩形的宽占据屏幕宽度的百分比（默认宽度为屏幕宽度的60%）
) : Serializable {

}


