package com.yubin.imagepicker.loader;

import android.app.Activity;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * ImageLoader抽象类，外部需要实现这个类去加载图片
 */
public interface ImageLoader extends Serializable {
    /**
     * 显示图片
     * displayImagePreview
     *
     * @param activity
     * @param path
     * @param imageView
     * @param width
     * @param height
     */
    void displayImage(Activity activity, String path, ImageView imageView, int width, int height);

    /**
     * 显示预览图
     *
     * @param activity
     * @param path
     * @param imageView
     * @param width
     * @param height
     */
    void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height);


    /**
     * 清空内存缓存
     */
    void clearMemoryCache();

    /**
     * 显示视频第一帧
     * displayImagePreview
     *
     * @param activity
     * @param path
     * @param imageView
     * @param width
     * @param height
     */
    void displayVideo(Activity activity, String path, ImageView imageView, int width, int height);

    /**
     * 显示视频第一帧
     *
     * @param activity
     * @param path
     * @param imageView
     * @param width
     * @param height
     */
    void displayVideoPreview(Activity activity, String path, ImageView imageView, int width, int height);

    /**
     * 获取视频第一帧图片地址
     *
     * @param activity
     * @param path
     * @return
     */
    String getVideoFirstFrameImagePath(Activity activity, String path);
}
