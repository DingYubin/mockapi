package com.yubin.mvp.interfaces;

/**
 * Created by minhui.zhu on 2017/2/16.
 */

public interface LoginInterface {

    interface View {
        void onSuccess();

        void onFailed();

    }

    interface Presenter {
        void login(String account, String password);
    }
}
