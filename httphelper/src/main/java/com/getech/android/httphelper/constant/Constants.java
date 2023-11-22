package com.getech.android.httphelper.constant;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Constants {
    /**
     * 默认的超时时长
     */
    public static final int HTTP_DEFAULT_TIMEOUT = 60;
    /**
     * http缓存永不过期
     */
    public static final long HTTP_CACHE_NEVER_EXPIRE = -1L;
    /**
     * http请求重试次数
     */
    public static final int HTTP_RETRY_COUNT = 3;
    /**
     * 网络请求，成功
     */
    public static final int HTTP_SUCCESS_CODE = 0;
    /**
     * 最大重试次数
     */
    public static final int MAX_RETRY_COUNT = 10;
}
