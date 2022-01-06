package com.yubin.mvp.presenter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yubin.httplibrary.mock_api.BaseObserver;
import com.yubin.httplibrary.mock_api.BaseResponse;
import com.yubin.httplibrary.mock_api.None;
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
        LoginApiService.getInstance(context).login(account, password, new BaseObserver<None>(){
            @Override
            public void onResponse(@NonNull BaseResponse<None> baseResponse, @Nullable None data) {
                if (baseResponse.isSuccessful(0)) {
                    view.onSuccess();
                } else {
                    view.onFailed();
                }
            }

            @Override
            public void onFailure(@NonNull BaseResponse<None> baseResponse, int errorCode, @NonNull String message) {
                view.onFailed();
            }
        });
    }
}
