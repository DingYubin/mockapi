package com.yubin.baselibrary.viewmodel


/**
 * description: 网络状态枚举
 *
 */
enum class NetworkState {

    /**
     * 初始化成功
     */
    INITIALIZED,

    /**
     * 加载中
     */
    LOADING,

    /**
     * 加载成功
     */
    SUCCESS,

    /**
     * 加载失败
     */
    FAIL,

    /**
     * 加载为空
     */
    EMPTY,

    /**
     * 无权限
     */
    UNAUTHORIZED,

    /**
     * 网络异常
     */
    EXCEPTION
}
