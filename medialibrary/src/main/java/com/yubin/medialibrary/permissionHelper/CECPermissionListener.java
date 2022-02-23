package com.yubin.medialibrary.permissionHelper;

/**
 * Created by maiwenchang at 2019/3/26 5:13 PM
 * Description ：监听权限获取事件
 */
public interface CECPermissionListener {

    /**
     * 授权成功
     */
    void onPermissionGranted();

    /**
     * 权限授权失败
     */
    void onPermissionDeclined(String permission);

    /**
     * 权限被永久拒绝（用户选了不再提醒）
     */
    void onPermissionDenied(String permission);

}
