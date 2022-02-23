package com.yubin.medialibrary.permissionHelper;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.yubin.baselibrary.core.BaseApplication;
import com.yubin.medialibrary.R;

import java.util.Arrays;

/**
 * Created by maiwenchang at 2019/3/26 5:08 PM
 * Description ：动态权限授权帮助类
 */
public class CECPermissionHelper {

    private CECPermissionHelper() {
    }

    /**
     * 日历权限
     */
    public static void requestForCalendar(CECPermissionListener listener) {
        requestPermissions(new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
        }, R.string.permission_message_calendar, listener);
    }

    /**
     * 相机权限
     */
    public static void requestForCamera(CECPermissionListener listener) {
        requestPermissions(new String[]{
                Manifest.permission.CAMERA
        }, R.string.permission_message_camera, listener);
    }

    /**
     * 手机通讯录权限
     */
    public static void requestReadContacts(CECPermissionListener listener) {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS
        }, R.string.permission_message_contacts, listener);
    }

    /**
     * 位置权限
     */
    public static void requestForLocation(CECPermissionListener listener) {
        requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, R.string.permission_message_location, listener);
    }

    /**
     * 麦克风权限
     */
    public static void requestForMicrophone(CECPermissionListener listener) {
        requestPermissions(new String[]{
                Manifest.permission.RECORD_AUDIO
        }, R.string.permission_message_microphone, listener);
    }

    /**
     * 手机信息权限
     */
    public static void requestForPhone(CECPermissionListener listener) {
        requestPermissions(new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.ADD_VOICEMAIL,
                Manifest.permission.USE_SIP,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
        }, R.string.permission_message_phone, listener);
    }

    /**
     * 运动传感器权限
     */
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public static void requestForSensors(CECPermissionListener listener) {
        requestPermissions(new String[]{
                Manifest.permission.BODY_SENSORS
        }, R.string.permission_message_sensors, listener);
    }

    /**
     * 短信权限
     */
    public static void requestForSMS(CECPermissionListener listener) {
        requestPermissions(new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_WAP_PUSH,
                Manifest.permission.RECEIVE_MMS
        }, R.string.permission_message_sms, listener);
    }

    /**
     * 拍照和存储
     */
    public static void requestForCameraAndStorage(CECPermissionListener listener) {
        requestPermissions(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, R.string.permission_message_camera, listener);
    }

    /**
     * 手机读写存储权限
     */
    public static void requestForStorage(CECPermissionListener listener) {
        requestPermissions(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, R.string.permission_message_strorage, listener);
    }

    /**
     * 视频录制权限
     */
    public static void requestForVideoRecord(CECPermissionListener listener) {
        requestPermissions(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        }, R.string.permission_message_record, listener);
    }

    /**
     * 申请权限
     *
     * @param permissions 权限集合
     * @param listener    权限申请回调
     */
    private static void requestPermissions(String[] permissions, @StringRes int alertMessage, CECPermissionListener listener) {
        if (hasPermissions(permissions)) {
            if (listener != null) {
                listener.onPermissionGranted();
            }
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionActivity.request(permissions, listener, alertMessage);
            return;
        }
        // 低于6.0的手机无法动态申请权限
        if (listener != null) {
            listener.onPermissionDenied(Arrays.toString(permissions));
        }
        //显示提醒系统权限设置弹窗
        PermissionActivity.showPermissionRequestDialog(alertMessage);
    }

    /**
     * 判断权限集合是否已授权
     *
     * @param permissions 权限信息
     * @return 是否授权
     */
    public static boolean hasPermissions(String... permissions) {
        Context context = BaseApplication.context;
        for (String permission : permissions) {
            boolean hasPermission = selfPermissionGranted(context, permission);
            if (!hasPermission) {
                return false;
            }
        }
        return true;
    }

    /**
     * 兼容targetSdkVersion<23，检查6.0权限情况的解决方案
     *
     * @param context    Context
     * @param permission 权限信息
     * @return 是否授权
     */
    private static boolean selfPermissionGranted(Context context, String permission) {
        int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        if (targetSdkVersion >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            //targetSdkVersion<23,需要用另一种方式获取权限
            return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
        }
    }
}
