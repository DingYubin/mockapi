package com.yubin.medialibrary.manager

import java.io.Serializable

/**
 * 策略
 */
open class MediaStrategy(
    var strategy: Int
) : Serializable {
    companion object {
        const val TYPE_TAKE_CAMERA = 0x001
        const val TYPE_OPEN_ALBUM = 0x002
        const val TYPE_PLAY_VIDEO = 0x003
        const val TYPE_RECORD_VIDEO = 0x004
    }
}


