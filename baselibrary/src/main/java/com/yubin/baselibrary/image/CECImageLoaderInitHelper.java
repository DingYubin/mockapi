package com.yubin.baselibrary.image;

import android.app.Application;
import android.net.Uri;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.yubin.baselibrary.util.CMUnitHelper;

import okhttp3.OkHttpClient;

/**
 * description: 开思图片加载初始化
 */
public class CECImageLoaderInitHelper {
    /**
     * 在application中进行初始化
     *
     * @param application 图片加载初始化(HttpUrlConnection发起图片的请求)
     * @param okhttpClient
     */
    public static void initWithApplication(Application application, OkHttpClient okhttpClient) {
        if (application == null) {
            return;
        }
        if (Fresco.hasBeenInitialized()) {
            return;
        }
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder(application, okhttpClient)
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(application).build())
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(application, config);
    }


    public static void showThumb(SimpleDraweeView draweeView, String url, int resizeWidthDp, int resizeHeightDp) {
        if (url == null || "".equals(url)) {
            return;
        }
        if (draweeView == null) {
            return;
        }

        int  width = (int) CMUnitHelper.INSTANCE.dp2px(resizeWidthDp);
        int  height = (int) CMUnitHelper.INSTANCE.dp2px(resizeHeightDp);
        if(width==0){
            width=100;
        }
        if(height==0){
            height=100;
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(width,height))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .setControllerListener(new BaseControllerListener<>())
                .build();
        draweeView.setController(controller);
    }


    private CECImageLoaderInitHelper() {
    }
}
