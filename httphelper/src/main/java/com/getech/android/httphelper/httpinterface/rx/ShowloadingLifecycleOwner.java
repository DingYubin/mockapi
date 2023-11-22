package com.getech.android.httphelper.httpinterface.rx;


import androidx.lifecycle.LifecycleOwner;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface ShowloadingLifecycleOwner extends LifecycleOwner {
    /**
     * 显示加载框
     */
    void showLoading();

    /**
     * 隐藏加载框
     */
    void hideLoading();
}
