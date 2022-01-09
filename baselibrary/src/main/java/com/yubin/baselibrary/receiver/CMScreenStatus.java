package com.yubin.baselibrary.receiver;

/**
 * description: 屏幕状态实体类
 */
public class CMScreenStatus {

    public static final int ON = 0;
    private static final int UNDEFINED = -1;

    private int mScreenStatus;

    public CMScreenStatus() {
        this.mScreenStatus = UNDEFINED;
    }

    public int getScreenStatus() {
        return mScreenStatus;
    }

    void setScreenStatus(int mScreenStatus) {
        this.mScreenStatus = mScreenStatus;
    }
}
