package com.yubin.baselibrary.image;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.yubin.baselibrary.image.strategy.CECImageUrlHelper;
import com.yubin.baselibrary.util.LogUtil;

public class CECImageUrlView extends SimpleDraweeView implements ICECImageLoader {

    private String imageUri;

    public CECImageUrlView(Context context) {
        super(context);
    }

    public CECImageUrlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CECImageUrlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageUri(Uri uri) {
        super.setImageURI(uri);
        this.imageUri = uri.toString();
    }

    @Override
    public void setImageUrl(String imageUrl) {
        String ossUrl = CECImageUrlHelper.process(getContext(), imageUrl, getWidth(), getHeight());
        super.setImageURI(ossUrl);
        this.imageUri = imageUrl;
    }

    public String getImageUri() {
        return imageUri;
    }

    @Override
    public void setImageUriAdjustHeight(String imageUrl) {
        setImageUriAdjustHeight(imageUrl, null);
    }

    @Override
    public void setImageUriAdjustHeight(String imageUrl, OnImageLoadedListener listener) {
        final ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                int width = imageInfo.getWidth();
                int height = imageInfo.getHeight();
                int viewWidth = CECImageUrlView.this.getWidth();
                layoutParams.height = (int) (viewWidth * height * 1.0000f / width);
                if (layoutParams.height < getMinimumHeight()) {
                    layoutParams.height = getMinimumHeight();
                } else if (layoutParams.height > getMaxHeight()) {
                    layoutParams.height = getMaxHeight();
                }
                CECImageUrlView.this.setLayoutParams(layoutParams);
                if (listener != null) {
                    listener.onLoaded(id, width, height);
                }
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                LogUtil.e(throwable.getMessage());
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(Uri.parse(imageUrl))
                .build();
        this.post(() -> CECImageUrlView.this.setController(controller));
    }

    @Override
    public void setImageUriAdjustWidth(String imageUrl) {
        setImageUriAdjustWidth(imageUrl, null);
    }

    @Override
    public void setImageUriAdjustWidth(String imageUrl, OnImageLoadedListener listener) {
        final ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<>() {
            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                int height = imageInfo.getHeight();
                int width = imageInfo.getWidth();
                int viewHeight = CECImageUrlView.this.getHeight();
                layoutParams.width = (int) (viewHeight * width * 1.0000f / height);
                if (layoutParams.width < getMinimumWidth()) {
                    layoutParams.width = getMinimumWidth();
                } else if (layoutParams.width > getMaxWidth()) {
                    layoutParams.width = getMaxWidth();
                }
                CECImageUrlView.this.setLayoutParams(layoutParams);
                if (listener != null) {
                    listener.onLoaded(id, width, height);
                }
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                LogUtil.e(throwable.getMessage());
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(Uri.parse(imageUrl))
                .build();
        this.post(() -> CECImageUrlView.this.setController(controller));
    }

    public interface OnImageLoadedListener {
        void onLoaded(String id, int width, int height);
    }
}
