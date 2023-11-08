package com.yubin.baselibrary.image.strategy;

import android.net.Uri;
import android.text.TextUtils;

public class CECImageFormatHelper {

    private static final String OPERATION_TAG = "/format,";
    private static final String OPERATION_TAG_FULL = "x-oss-process=image/format,";

    /**
     * 图片的动态转码(针对android4.0以上使用webp在图片质量和大小之间得到一个好的权衡)
     *
     * @param format 图片的转码格式,使用{@link CECImageFormat}里面的常量
     * @param isFull 是否需要一个全的图片处理的标签
     * @return 返回响应的操作字符串
     */
    public static String format(String format, boolean isFull) {
        if (TextUtils.isEmpty(format)) {
            return Uri.EMPTY.toString();
        }
        StringBuilder stringBuilder;
        if (isFull) {
            stringBuilder = new StringBuilder(OPERATION_TAG_FULL);
        } else {
            stringBuilder = new StringBuilder(OPERATION_TAG);
        }
        stringBuilder.append(format);
        return stringBuilder.toString();
    }

    private CECImageFormatHelper() {
    }
}
