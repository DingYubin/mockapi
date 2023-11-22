package com.getech.android.httphelper.httpinterface;

public interface HttpErrorProcessor {
    /**
     * 错误处理
     * @param tag 出错的网络请求tag
     * @param e 异常信息
     */
    void processHttpError(String tag, Exception e);
}
