package com.getech.android.httphelper.httpinterface.interceptor;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface HttpInterceptor {
    /**
     * 拦截请求信息，并做修改
     *
     * @param requestInfo
     * @return
     */
    HttpRequestInfo interceptRequestInfo(HttpRequestInfo requestInfo);

    /**
     * 拦截返回数据
     *
     * @param responseInfo
     * @return
     */
    HttpResponseInfo interceptResponseInfo(HttpResponseInfo responseInfo);
}
