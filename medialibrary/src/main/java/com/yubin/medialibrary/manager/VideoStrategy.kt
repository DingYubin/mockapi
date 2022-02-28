package com.yubin.medialibrary.manager

/**
 * 播放视频
 */
data class VideoStrategy(
    var url: String? = null,
    var isMute: Boolean = false,
) : MediaStrategy(TYPE_PLAY_VIDEO)


