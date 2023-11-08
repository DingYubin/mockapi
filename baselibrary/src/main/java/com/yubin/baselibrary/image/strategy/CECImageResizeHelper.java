package com.yubin.baselibrary.image.strategy;

import android.net.Uri;

public class CECImageResizeHelper {

    private static final String OPERATION_TAG = "x-oss-process=image/resize";

    /**
     * 图片缩放（根据显示图片组件的大小来进行动态缩放
     *
     * @param targetWidth  控件的宽度
     * @param targetHeight 控件的高度
     * @return 返回响应的操作字符串
     */
    public static String resize(int targetWidth, int targetHeight) {
        if (targetWidth == 0 || targetHeight == 0) {
            return Uri.EMPTY.toString();
        }
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder(OPERATION_TAG);
        stringBuilder.append(",m_lfit");
        stringBuilder.append(",limit_");
        stringBuilder.append(1);
        stringBuilder.append(",w_");
        stringBuilder.append(targetWidth);
        stringBuilder.append(",h_");
        stringBuilder.append(targetHeight);
        return stringBuilder.toString();
    }

    private CECImageResizeHelper() {
    }
}
