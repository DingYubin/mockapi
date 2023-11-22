package com.getech.android.utils.model;

import android.content.Context;
import android.os.Build;

import com.getech.android.utils.AppUtils;
import com.getech.android.utils.BarUtils;
import com.getech.android.utils.DeviceUtils;
import com.getech.android.utils.ScreenUtils;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/02/26
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SystemInfo {
    /**
     * 当前H5应用的标识符
     */
    public String identifier;
    /**
     * 当前H5应用的版本号
     */
    public String widgetVersion;
    /**
     * 底座App的appId
     */
    public String appId;
    /**
     * 底座App的版本号
     */
    public String appVersion;
    /**
     * 底座App的版本构建号
     */
    public int appBuildVersion;
    /**
     * 设备id
     */
    public String deviceId;
    /**
     * 操作系统版本号
     */
    public String osVersion;
    /**
     * 宽*高
     */
    public String screen;
    /**
     * 设备型号
     */
    public String model;
    /**
     * 手机品牌
     */
    public String brand;
    /**
     * 设备像素比
     */
    public String pixelRatio;
    /**
     * 可使用窗口宽度
     */
    public int windowWidth;
    /**
     * 可使用窗口高度
     */
    public int windowHeight;
    /**
     * 状态栏高度
     */
    public int statusBarHeight;
    /**
     * 平台类型，ios，android
     */
    public String platform;

    public void initBasicInfo(Context context) {
        this.appVersion = AppUtils.getAppVersionName();
        this.appBuildVersion = AppUtils.getAppVersionCode();
        this.deviceId = DeviceUtils.getDeviceId(context);
        this.osVersion = DeviceUtils.getSDKVersionName();
        this.screen = ScreenUtils.getScreenWidth() + "*" + ScreenUtils.getScreenHeight();
        this.model = DeviceUtils.getModel();
        this.brand = Build.BRAND;
        this.pixelRatio = (ScreenUtils.getScreenWidth() * 1.0 / ScreenUtils.getScreenDensityDpi()) + "";
        this.windowWidth = ScreenUtils.getScreenWidth();
        this.windowHeight = ScreenUtils.getScreenHeight() - BarUtils.getStatusBarHeight();
        this.statusBarHeight = BarUtils.getStatusBarHeight();
        this.platform = "android";
    }
}
