package com.yubin.baselibrary.util;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.annotation.Nullable;

/**
 * date: created on 2019/2/22
 * <p>
 */
public class CECDeviceHelper {

    /**
     * 获取屏幕宽度
     *
     * @param context context {@link Context}
     * @return 屏幕宽度
     */
    public static int screenWidthWithContext(@Nullable Context context) {
        if (context == null) {
            return 0;
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context context {@link Context}
     * @return 屏幕高度
     */
    public static int screenHeightWithContext(@Nullable Context context) {
        if (context == null) {
            return 0;
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取状态栏的高度
     *
     * @param context context {@link Context}
     * @return 状态栏的高度
     */
    public static int statusBarHeightWithContext(@Nullable Context context) {
        int statusBarHeight = 0;
        if (context == null) {
            return statusBarHeight;
        }
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    private CECDeviceHelper() {
    }


    /**
     * 检测麦克风是否正在使用
     * @return 返回true就是没有被占用。
     */
    public static boolean validateMicAvailability(){
        Boolean available = true;
        AudioRecord recorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_DEFAULT, 44100);
        try{
            if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED ){
                available = false;

            }

            recorder.startRecording();
            if(recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING){
                recorder.stop();
                available = false;

            }
            recorder.stop();
        } finally{
            recorder.release();
            recorder = null;
        }
        return available;
    }


}
