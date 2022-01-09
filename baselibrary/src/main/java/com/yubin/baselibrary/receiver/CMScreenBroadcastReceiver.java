package com.yubin.baselibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * description: 息屏亮屏的广播接收器
 */
public class CMScreenBroadcastReceiver extends BroadcastReceiver {

    private CMScreenStatus mScreenStatus;

    public CMScreenBroadcastReceiver(CMScreenStatus screenStatus) {
        this.mScreenStatus = screenStatus;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            mScreenStatus.setScreenStatus(CMScreenStatus.ON);
        }
    }

    public CMScreenStatus getScreenStatus() {
        return mScreenStatus;
    }
}
