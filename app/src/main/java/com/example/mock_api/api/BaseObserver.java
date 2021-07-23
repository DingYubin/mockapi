package com.example.mock_api.api;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BaseObserver<E> implements Observer<BaseResponse<E>> {

    @Override
    public void onSubscribe(Disposable d) {
        //do nothing
    }

    @Override
    public void onNext(BaseResponse<E> baseResponse) {
        if (baseResponse == null) {
            return;
        }
        if (baseResponse.getErrorCode() == 0) {
            onResponse(baseResponse, baseResponse.getData());
        } else {
            String message = baseResponse.getMessage();
            if (TextUtils.isEmpty(message)) {
                message = "服务器繁忙";
            }
            //noinspection ConstantConditions
            onFailure(baseResponse, baseResponse.getErrorCode(), message);
        }
    }

    @Override
    public void onError(Throwable e) {
        // 拦截错误信息
        BaseResponse<E> baseResponse = new HttpErrorResponse<E>(e).getBaseResponse();
        String message = baseResponse.getMessage();
        if (TextUtils.isEmpty(message)) {
            message = "服务器繁忙";
        }
        //noinspection ConstantConditions
        onFailure(baseResponse, baseResponse.getErrorCode(), message);
    }

    @Override
    public void onComplete() {
        //do nothing
    }

    /**
     * 请求成功时回调
     *
     * @param baseResponse BaseResponse{errorCode: number;data: any;message: string;teamCode: number;}
     * @param data         数据实体
     */
    public void onResponse(@NonNull BaseResponse<E> baseResponse, @Nullable E data) {
        //do nothing
    }

    /**
     * 请求失败时回调
     *
     * @param baseResponse BaseResponse{errorCode: number;data: any;message: string;teamCode: number;}
     * @param errorCode    错误代码
     * @param message      错误提示
     */
    public void onFailure(@NonNull BaseResponse<E> baseResponse, int errorCode, @NonNull String message) {
        Log.e(getClass().getSimpleName(), "Http Error : " + message);
        if (errorCode == 652) { //鉴权失败不提醒
            return;
        }
        if (TextUtils.isEmpty(message)) {
            return;
        }
//        ToastUtil.showToast(message);
    }
}
