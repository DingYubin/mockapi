package com.yubin.baselibrary.image.strategy;

public class CECImageQualityHelper {

    private static final String OPERATION_TAG = "/quality,Q_";

    public static String quality(int quality) {
        return OPERATION_TAG + quality;
    }

    private CECImageQualityHelper() {
    }
}
