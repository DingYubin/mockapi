package com.yubin.medialibrary.permissionHelper;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;

import com.yubin.baselibrary.core.BaseApplication;
import com.yubin.baselibrary.ui.basemvvm.BaseActivity;
import com.yubin.medialibrary.R;

import java.util.Arrays;

/**
 * Created by maiwenchang at 2019/3/26 5:11 PM
 * Description ：权限申请Activity
 */
public class PermissionActivity extends BaseActivity {

    public static final String KEY_PERMISSIONS = "permissions";
    public static final String KEY_SHOW_DIALOG = "show_dialog";
    public static final String KEY_DIALOG_MESSAGE = "dialog_message";
    private static final int RC_REQUEST_PERMISSION = 100;
    private static CECPermissionListener CALLBACK;
    private String dialogMessage = "";

    /*
     * 添加一个静态方法方便使用
     */
    public static void request(String[] permissions, CECPermissionListener callback, @StringRes int alertMessage) {
        CALLBACK = callback;
        Intent intent = new Intent(BaseApplication.context, PermissionActivity.class);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        intent.putExtra(KEY_DIALOG_MESSAGE, alertMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApplication.context.startActivity(intent);
    }

    public static void showPermissionRequestDialog(@StringRes int alertMessage) {
        Intent intent = new Intent(BaseApplication.context, PermissionActivity.class);
        intent.putExtra(KEY_SHOW_DIALOG, true);
        intent.putExtra(KEY_DIALOG_MESSAGE, alertMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApplication.context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            overridePendingTransition(android.R.anim.fade_in, R.anim.anim_no);
            super.onCreate(savedInstanceState);
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.hide();
            }
            Intent intent = getIntent();
            if (intent.hasExtra(KEY_DIALOG_MESSAGE)) {
                int resId = intent.getIntExtra(KEY_DIALOG_MESSAGE, 0);
                if (resId == 0) {//如果没有设置提示语，使用默认提示语
                    resId = R.string.permission_message_dialog_default;
                }
                dialogMessage = getResources().getString(resId);
            }
            if (intent.hasExtra(KEY_SHOW_DIALOG) && intent.getBooleanExtra(KEY_SHOW_DIALOG, false)) {
                //直接显示系统设置弹框
                showSettingDialog();
                return;
            }
            //动态申请权限
            if (!intent.hasExtra(KEY_PERMISSIONS)) {
                finish();
                return;
            }
            String[] permissions = getIntent().getStringArrayExtra(KEY_PERMISSIONS);
            if (permissions == null || permissions.length <= 0) {
                if (CALLBACK != null) {
                    CALLBACK.onPermissionGranted();
                }
                finish();
                return;
            }
            ActivityCompat.requestPermissions(this, permissions, RC_REQUEST_PERMISSION);
        } catch (Exception e) {
            finish();
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode != RC_REQUEST_PERMISSION) {
                return;
            }
            // 处理申请结果
            if (permissions.length <= 0 || grantResults.length <= 0) {//没有获取到任何权限
                if (CALLBACK != null) {
                    CALLBACK.onPermissionDenied(Arrays.toString(permissions));
                }
                //跳转到设置页
                showSettingDialog();
                return;
            }
            //逐一处理申请结果
            for (int i = 0; i < permissions.length; ++i) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    continue;
                }
                if (shouldShowRequestPermissionRationale(permissions[i])) {
                    //授权失败，但是用户未选不再提醒
                    if (CALLBACK != null) {
                        CALLBACK.onPermissionDeclined(permissions[i]);
                    }
                    finish();
                    return;
                }
                //授权失败，用户选了不再提醒
                if (CALLBACK != null) {
                    CALLBACK.onPermissionDenied(permissions[i]);
                }
                //跳转到设置页
                showSettingDialog();
                return;
            }
            if (CALLBACK != null) {
                CALLBACK.onPermissionGranted();
            }
            finish();
        } catch (Exception e) {
            finish();
        }
    }


    /**
     * 显示跳转到系统设置的弹框
     */
    private void showSettingDialog() {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_no, android.R.anim.fade_out);
    }
}
