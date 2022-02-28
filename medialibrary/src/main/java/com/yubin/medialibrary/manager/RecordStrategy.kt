package com.yubin.medialibrary.manager

/**
 * 录制视频
 */
data class RecordStrategy(
    var seconds: Int = 20 * 2000,
) : MediaStrategy(TYPE_RECORD_VIDEO)


