package com.yubin.baselibrary.image.strategy;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.yubin.baselibrary.util.CECNetworkHelper;

public class CECImageUrlHelper {

    private static final int DEFAULT_QUALITY = 90;
    private static final String DEFAULT_FORMAT = CECImageFormat.WEBP;


    /**
     * 图片url动态图片处理参数拼装
     *
     * @param originImageUrl 图片原始url
     * @param targetWidth    目标宽度
     * @param targetHeight   目标高度
     * @param quality        质量大小
     * @return 拼装url
     */
    public static String process(String originImageUrl, int targetWidth, int targetHeight, int quality) {
        return process(originImageUrl, targetWidth, targetHeight, DEFAULT_FORMAT, quality);
    }

    /**
     * 图片url动态图片处理参数拼装
     *
     * @param originImageUrl 图片原始url
     * @param targetWidth    目标宽度
     * @param targetHeight   目标高度
     * @return 拼装url
     */
    public static String process(Context context, String originImageUrl, int targetWidth, int targetHeight) {
        int quality = DEFAULT_QUALITY;
        if (context != null) {
            quality = qualityWithNetwork(context);
        }
        return process(originImageUrl, targetWidth, targetHeight, DEFAULT_FORMAT, quality);
    }

    /**
     * 图片url动态图片处理参数拼装
     *
     * @param originImageUrl 图片原始url
     * @param targetWidth    目标宽度
     * @param targetHeight   目标高度
     * @param format         转码格式
     * @param quality        质量大小
     * @return 拼装url
     */
    public static String process(String originImageUrl, int targetWidth, int targetHeight, String format, int quality) {
        if (TextUtils.isEmpty(originImageUrl)) {
            return originImageUrl;
        }
        if (!originImageUrl.startsWith("http")) {
            return originImageUrl;
        }
        if (originImageUrl.contains("?")) {
            return originImageUrl;
        }
        StringBuilder stringBuilder = new StringBuilder(originImageUrl);
        stringBuilder.append("?");
        String resizeResult = CECImageResizeHelper.resize(targetWidth, targetHeight);
        stringBuilder.append(resizeResult);
        String formatResult = CECImageFormatHelper.format(format, TextUtils.isEmpty(resizeResult));
        stringBuilder.append(formatResult);
        String qualityResult = CECImageQualityHelper.quality(quality);
        stringBuilder.append(qualityResult);
        return stringBuilder.toString();
    }

    /**
     * 根据网络情况获取图片质量
     */
    private static int qualityWithNetwork(@NonNull Context context) {
        if (CECNetworkHelper.isWifiWithContext(context)) {
            return 100;
        }
        if (CECNetworkHelper.isMobile(context)) {
            String type = CECNetworkHelper.getNetWorkClass(context);
            if (CECNetworkHelper.Constants.NETWORK_CLASS_4_G.equals(type)) {
                return 90;
            } else if (CECNetworkHelper.Constants.NETWORK_CLASS_3_G.equals(type)) {
                return 80;
            } else if (CECNetworkHelper.Constants.NETWORK_CLASS_2_G.equals(type)) {
                return 70;
            }
        }
        return DEFAULT_QUALITY;
    }

    private CECImageUrlHelper() {
    }
}
