package com.yubin.httplibrary.mock_api;

import android.text.TextUtils;

public class HttpErrorResponse<E> {

    private BaseResponse<E> baseResponse;

    private Throwable e;

    public HttpErrorResponse(Throwable e) {
        this.e = e;
    }

    public BaseResponse<E> getBaseResponse() {
        if (baseResponse != null) {
            return baseResponse;
        }
        ErrorResponseBody responseBody = HttpErrorHandler.handle(e);
        String unknownMessage = "服务器异常";
        if (responseBody == null) {
            if (e != null) {
                unknownMessage = e.getMessage();
            }
            baseResponse = new BaseResponse<>(-1, unknownMessage, null, 1000);
            return baseResponse;
        }
        // 服务器有返回错误信息
        String responseErrorMessage;
        if (!TextUtils.isEmpty(responseBody.getErrorMessage())) {
            responseErrorMessage = responseBody.getErrorMessage();
        } else if (!TextUtils.isEmpty(responseBody.getError())) {
            responseErrorMessage = responseBody.getError();
        } else if (!TextUtils.isEmpty(responseBody.getMessage())) {
            responseErrorMessage = responseBody.getMessage();
        } else {
            responseErrorMessage = unknownMessage;
        }
        baseResponse = new BaseResponse<>(responseBody.getStatusCode(), responseErrorMessage, null, 1000);
        return baseResponse;
    }
}
