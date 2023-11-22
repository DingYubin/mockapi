package com.getech.android.httphelper.httpinterface.rx;


import androidx.fragment.app.Fragment;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class AbstractRxFragment extends Fragment {
    /**
     * 显示加载框
     */
    public abstract void showLoading();

    /**
     * 隐藏加载框
     */
    public abstract void hideLoading();
}
