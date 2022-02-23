package com.yubin.medialibrary.manager

import java.io.Serializable

/**
 *description 返回的多媒体详细信息
 *
 *@author laiwei
 *@date create at 4/26/21 10:41 AM
 */
data class MediaInfo(
    var isVideo: Boolean = false,
    var duration: Long = 0,
    var size: Long = 0,
    var uri: String,
    //视频封面缩略图,Base64编码
    var thumb: String = ""
) : Serializable
