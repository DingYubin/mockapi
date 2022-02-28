package com.yubin.medialibrary.album.been

import android.os.Bundle
import com.yubin.medialibrary.extra.IDiffData
import java.util.*

/**
 *description
 */
class ImagePickerBean : IDiffData {
    /**
     * contentProvider id
     */
    var id = 0

    /**
     * 图片文件路径
     */
    var data: String? = null

    /**
     * 图片名
     */
    var displayName: String? = ""

    /**
     * 所在文件夹名字
     */
    var bucketDisplayName: String? = null

    /**
     * 所在文件夹路径
     */
    var parentFolder: String? = null

    /**
     * 是否被选中
     */
    var isSelect = false

    /**
     * 是否被选中
     */
    var isFolderSelect = false

    /**
     * 当前所在文件夹里面的图片数量
     */
    var currentFolderImageNumber = 0

    /**
     * 当前选中时的序号
     */
    var badge = 0

    /**
     * 最大可选择的数量
     */
    var maxSelectNumber = 9

    /**
     * 是否是视频文件
     */
    var isVideo = false

    /**
     * 视频缩略图路径
     */
    var thumbData: String? = null

    /**
     * 文件大小
     */
    var size: Long = 0

    /**
     * 视频时长
     */
    var duration: Long = 0

    /**
     * 最大可以发送的视频大小
     */
    var maxVideoSize: Int = Int.MAX_VALUE

    /**
     * 文件最后修改时间
     */
    var dateModified: Long = 0

    override fun getItemSameId(): String? {
        return data
    }

    override fun getChangePayload(other: IDiffData?): Bundle? {
        return null
    }

    val showTime: String
        get() = if (duration == 0L) {
            "00:00"
        } else {
            val seconds = duration / 1000
            val hour = seconds / (60 * 60)
            val minute = seconds % (60 * 60) / 60
            val second = seconds % (60 * 60) % 60
            if (hour != 0L) {
                String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second)
            } else {
                String.format(Locale.getDefault(), "%02d:%02d", minute, second)
            }
        }


}