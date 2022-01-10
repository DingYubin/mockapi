package com.yubin.baselibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * description: 息屏亮屏的广播接收器
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {

    private ScreenStatus mScreenStatus;

    public ScreenBroadcastReceiver(ScreenStatus screenStatus) {
        this.mScreenStatus = screenStatus;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            mScreenStatus.setScreenStatus(ScreenStatus.ON);
        }
    }

    public ScreenStatus getScreenStatus() {
        return mScreenStatus;
    }
}
