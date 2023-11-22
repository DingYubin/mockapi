package com.getech.android.httphelper.exception;

import android.net.ParseException;

import androidx.annotation.IntDef;

import com.getech.android.httphelper.impl.exception.HttpStatusException;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ExceptionHandler {

    @IntDef({ExceptionType.UNKNOWN, ExceptionType.PARSE_ERROR, ExceptionType.NETWORK_ERROR, ExceptionType.PROTOCOL_ERROR, ExceptionType.SUCCESS_BUT_NULL, ExceptionType.HTTP_CODE_BIGGER_400})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExceptionType {
        /**
         * 未知错误
         */
        int UNKNOWN = 1;
        /**
         * 解析错误
         */
        int PARSE_ERROR = 2;
        /**
         * 网络错误
         */
        int NETWORK_ERROR = 3;
        /**
         * 协议错误
         */
        int PROTOCOL_ERROR = 4;
        /**
         * 后台返回code为success,但是data节点为null,一般是接口成功了,但是没有相应对象返回
         */
        int SUCCESS_BUT_NULL = 5;
        /**
         * 网络请求状态码大于400
         */
        int HTTP_CODE_BIGGER_400 = 6;
    }

    public static ApiException handleException(Throwable e, String tag) {

        ApiException ex;
        if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException
                || e instanceof NullPointerException) {
            //解析错误
            ex = new ApiException(ExceptionType.PARSE_ERROR, ExceptionType.PARSE_ERROR, e.getMessage(), "数据解析错误", tag);
        } else if (e instanceof ConnectException) {
            ex = new ApiException(ExceptionType.NETWORK_ERROR, ExceptionType.NETWORK_ERROR, e.getMessage(), "网络中断,请检查您的网络状态", tag);
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(ExceptionType.NETWORK_ERROR, ExceptionType.NETWORK_ERROR, e.getMessage(), "没有联网,请检查您的网络设置", tag);
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(ExceptionType.NETWORK_ERROR, ExceptionType.NETWORK_ERROR, e.getMessage(), "请求数据超时,请稍后重试", tag);
        } else if (e instanceof HttpStatusException) {
            ex = new ApiException(ExceptionType.HTTP_CODE_BIGGER_400, ExceptionType.NETWORK_ERROR, e.getMessage(), "网络连接错误，请稍后重试", ((HttpStatusException) e).getHttpCode(), tag);
        } else {
            ex = new ApiException(ExceptionType.UNKNOWN, ExceptionType.UNKNOWN, e.getMessage(), e.getMessage(), tag);
        }
        return ex;
    }

    public static ApiException handleProtocolException(int code, String msg, String tag) {
        return new ApiException(ExceptionType.PROTOCOL_ERROR, code, msg, msg, tag);
    }

    public static ApiException handleSuccessButDataNull(String msg, String tag) {
        return new ApiException(ExceptionType.SUCCESS_BUT_NULL, ExceptionType.SUCCESS_BUT_NULL, msg, msg, tag);
    }
}

