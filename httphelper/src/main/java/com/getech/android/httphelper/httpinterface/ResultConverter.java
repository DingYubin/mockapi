package com.getech.android.httphelper.httpinterface;

public interface ResultConverter<T> {
    /**
     * 转换
     *
     * @param responseStr 需要转换的对象
     * @return 转换后的结果
     * @throws Exception 转换过程发生的异常
     */
    T convertResponse(String responseStr) throws Exception;
}
