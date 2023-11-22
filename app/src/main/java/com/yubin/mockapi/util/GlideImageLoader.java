package com.yubin.mockapi.util;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yubin.imagepicker.loader.ImageLoader;
import com.yubin.mockapi.R;

import java.io.File;

/**
 * <pre>
 *     @author : xiaoqing
 *     e-mail : qing.xiao@getech.cn
 *     time   : 2018-3-14
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        RequestOptions options = RequestOptions
                .placeholderOf(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(activity)
                .load(Uri.fromFile(new File(path)))
                .apply(options)
                .into(imageView);
    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity)
                .load(Uri.fromFile(new File(path)))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
    }

    @Override
    public void displayVideo(Activity activity, String path, ImageView imageView, int width, int height) {
        RequestOptions options = RequestOptions
                .placeholderOf(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(activity)
                .load(Uri.fromFile(new File(path)))
                .apply(options)
                .into(imageView);
    }

    @Override
    public void displayVideoPreview(Activity activity, String path, ImageView imageView, int width, int height) {

    }

    @Override
    public String getVideoFirstFrameImagePath(Activity activity, String path) {
        return null;
    }
}
