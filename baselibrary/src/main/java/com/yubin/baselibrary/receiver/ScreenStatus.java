package com.yubin.baselibrary.receiver;

/**
 * description: 屏幕状态实体类
 */
public class ScreenStatus {

    public static final int ON = 0;
    private static final int UNDEFINED = -1;

    private int mScreenStatus;

    public ScreenStatus() {
        this.mScreenStatus = UNDEFINED;
    }

    public int getScreenStatus() {
        return mScreenStatus;
    }

    void setScreenStatus(int mScreenStatus) {
        this.mScreenStatus = mScreenStatus;
    }
}
