package com.yubin.baselibrary.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by yubin.ding on 2022/2/27.
 */
public class HandlerHelper {
    private Handler handler;
    private HandlerThread handlerThread;
    private Handler.Callback callback;

    public HandlerHelper(Handler.Callback callback) {
        this.callback = callback;
    }


    public Handler getHandler() {
        return getBackgroundHandler(callback);
    }

    private Handler getBackgroundHandler(Handler.Callback callback) {
        if (handler == null) {
            handlerThread = new HandlerThread("background_handler_thread");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper(), callback);
        }
        return handler;
    }

    public void clear() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public void release() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (handlerThread != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                handlerThread.quitSafely();
            } else {
                handlerThread.quit();
            }
        }
    }
}
