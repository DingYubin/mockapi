package com.yubin.baselibrary.image;

import android.net.Uri;

public interface ICECImageLoader {

    /**
     * ø
     * 设置图片加载的uri
     *
     * @param uri 图片的加载uri
     */
    void setImageUri(Uri uri);

    /**
     * 设置图片的url
     *
     * @param imageUrl 图片的加载url
     */
    void setImageUrl(String imageUrl);

    /**
     * 设置图片的url, 高度自适应
     *
     * @param imageUrl 图片的加载url
     */
    void setImageUriAdjustHeight(String imageUrl);

    void setImageUriAdjustHeight(String imageUrl, CECImageUrlView.OnImageLoadedListener listener);


    /**
     * 设置图片的url, 宽度自适应
     *
     * @param imageUrl 图片的加载url
     */
    void setImageUriAdjustWidth(String imageUrl);

    void setImageUriAdjustWidth(String imageUrl, CECImageUrlView.OnImageLoadedListener listener);
}
