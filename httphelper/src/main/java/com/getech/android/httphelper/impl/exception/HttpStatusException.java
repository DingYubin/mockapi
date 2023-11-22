package com.getech.android.httphelper.impl.exception;

import com.lzy.okgo.exception.HttpException;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class HttpStatusException extends HttpException {
    private int httpCode;

    public HttpStatusException(String message, int httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }
}
