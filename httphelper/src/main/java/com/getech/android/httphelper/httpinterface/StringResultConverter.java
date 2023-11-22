package com.getech.android.httphelper.httpinterface;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/15
 *     desc   : 原样返回response的body字符串
 *     version: 1.0
 * </pre>
 */
public class StringResultConverter<T> implements ResultConverter<String> {
    @Override
    public String convertResponse(String responseStr) throws Exception {
        return responseStr;
    }
}
