package com.getech.android.httphelper.httpinterface;

public class HttpErrorInfo extends Exception {
    /**
     * 处理结果时出错
     */
    public static final int ERROR_CODE_PROCESS_RESPONSE = -1;
    /**
     * 状态码,大于0表示http请求状态码，
     */
    public int statusCode;
    /**
     * 异常信息
     */
    public Throwable throwable;

    public HttpErrorInfo(int statusCode, Throwable throwable) {
        super(throwable);
        this.statusCode = statusCode;
        this.throwable = throwable;
    }
}
