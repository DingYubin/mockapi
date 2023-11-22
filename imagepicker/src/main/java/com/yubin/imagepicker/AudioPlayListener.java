package com.yubin.imagepicker;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/02/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface AudioPlayListener {
    /**
     * 音频文件无法下载
     */
    int PLAY_ERROR_DOWNLOAD_FAILED = 0;
    /**
     * url为空
     */
    int PLAY_ERROR_EMPTY_URL = 1;
    /**
     * 不是音频文件
     */
    int PLAY_ERROR_NOT_AUDIO = 2;
    /**
     * 正在播放音频
     */
    int PLAY_ERROR_IS_PLAYING = 3;
    /**
     * 视频播放出错
     */
    int PLAY_ERROR_PLAY = 3;

    /**
     * 开始播放
     *
     * @param url
     */
    void onStart(String url);

    /**
     * 播放失败
     *
     * @param url
     */
    void onError(String url, int errorInfo);

    void onComplete(String url);
}
