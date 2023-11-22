package com.yubin.imagepicker;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/02/22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface SaveMediaToGalleryListener {
    /**
     * 开始
     *
     * @param uri 要保存的图片地址
     */
    void onStart(String uri);

    /**
     * 保存出错了
     *
     * @param uri 要保存的图片地址
     */
    void onError(String uri);

    /**
     * 结束
     *
     * @param uri 要保存的图片地址
     */
    void onComplete(String uri);
}
