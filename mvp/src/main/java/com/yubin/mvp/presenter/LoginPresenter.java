package com.yubin.mvp.presenter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yubin.httplibrary.mockapi.BaseObserver;
import com.yubin.httplibrary.mockapi.BaseResponse;
import com.yubin.mvp.api.LoginApiService;
import com.yubin.mvp.interfaces.LoginInterface;

public class LoginPresenter implements LoginInterface.Presenter {
    private final Activity context;
    private final LoginInterface.View view;

    public LoginPresenter(Activity context, LoginInterface.View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void login(String account, String password) {
        LoginApiService.getInstance(context).login(account, password, new BaseObserver<Object>(){
            @Override
            public void onResponse(@NonNull BaseResponse<Object> baseResponse, @Nullable Object data) {
                if (baseResponse.isSuccessful(0)) {
                    view.onSuccess();
                } else {
                    view.onFailed();
                }
            }

            @Override
            public void onFailure(@NonNull BaseResponse<Object> baseResponse, int errorCode, @NonNull String message) {
                view.onFailed();
            }
        });
    }
}
