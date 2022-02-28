package com.yubin.medialibrary.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

/**
 * description: 开思网络相关辅助类
 * <p>
 * date: created on 2019/2/22
 * <p>
 * author:   侯军(A01082)
 */
@SuppressLint("MissingPermission")
public class NetworkHelper {

    private NetworkHelper() {
    }

    /**
     * 移动网络类型
     */
    public static String getNetWorkClass(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return Constants.NETWORK_CLASS_4_G;
        }
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return Constants.NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return Constants.NETWORK_CLASS_3_G;
            default:
                return Constants.NETWORK_CLASS_4_G;
        }
    }

    /**
     * 是否是wifi
     */
    public static boolean isWifiWithContext(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return true;
        }
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            return true;
        }
        return info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断是否为移动网络
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        return connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 网络是否连接
     */
    public static boolean isNetwordConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    public class Constants {

        /**
         * "2G" networks
         */
        public static final String NETWORK_CLASS_2_G = "2G";

        /**
         * "3G" networks
         */
        public static final String NETWORK_CLASS_3_G = "3G";

        /**
         * "4G" networks
         */
        public static final String NETWORK_CLASS_4_G = "4G";
        /**
         * "5G" networks
         */
        public static final String NETWORK_CLASS_5_G = "5G";

    }
}
