package com.getech.android.httphelper.httpinterface;


import com.getech.android.httphelper.constant.HttpCacheMode;
import com.getech.android.httphelper.httpinterface.interceptor.HttpInterceptor;
import com.getech.android.httphelper.httpinterface.rx.AbstractRxObserver;
import com.getech.android.httphelper.httpinterface.rx.RxUploadDownloadResult;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;

import io.reactivex.Observable;

public interface HttpProvider {
    /**
     * 初始化
     */
    void init();

    /**
     * 取消
     *
     * @param tag http tag
     */
    void cancel(String tag);

    /**
     * 取消所有请求
     */
    void cancelAll();

    /**
     * 设置请求的baseUrl
     *
     * @param baseUrl baseUr
     */
    void baseUrl(String baseUrl);

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间，单位秒
     */
    void timeout(int timeout);

    /**
     * 设置通用的header
     *
     * @param headers 通用header
     */
    void commonHeaders(HashMap headers);

    /**
     * 是否打开日志
     *
     * @param openLog 是否打开日志
     */
    void openLog(boolean openLog);

    /**
     * 缓存模式
     *
     * @param cacheMode
     */
    void cachedMode(HttpCacheMode cacheMode);

    /**
     * 缓存时长
     *
     * @param cacheTime
     */
    void cacheTime(long cacheTime);

    /**
     * 设置ssl证书路径
     *
     * @param sslFilePath
     */
    void sslFilePath(String sslFilePath);

    /**
     * 设置重试次数
     *
     * @param retryCount
     */
    void retryCount(int retryCount);

    /**
     * 添加拦截器
     *
     * @param httpInterceptor
     */
    void addInterceptor(HttpInterceptor httpInterceptor);

    /**
     * 异步网络请求,返回responsebody
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    void requestStringAsync(HttpRequestParams params, AbstractHttpRequestListener listener);

    /**
     * 异步网络请求，执行转换
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    <T> void requestBaseHttpAsync(HttpRequestParams params, AbstractHttpRequestListener<BaseHttpResult<T>> listener, Type type);

    /**
     * 同步网络请求
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    String requestStringSync(HttpRequestParams params, AbstractHttpRequestListener listener);

    /**
     * 同步网络请求
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    <T> BaseHttpResult<T> requestBaseHttpSync(HttpRequestParams params, AbstractHttpRequestListener<BaseHttpResult<T>> listener, Type type);

    /**
     * 下载文件,异步
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    void download(HttpRequestParams params, AbstractHttpRequestListener listener);

    /**
     * 上传文件,异步
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    void uploadFile(HttpRequestParams params, AbstractHttpRequestListener listener);

    /**
     * rx请求，处理生命周期,调用方做数据处理
     *
     * @param params
     * @param typeToken
     * @param observer
     * @param converter
     * @param <T>
     */
    <T> void rxRequest(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter, AbstractRxObserver<T> observer);

    /**
     * rx请求，不处理生命周期，调用方做数据处理
     *
     * @param params
     * @param typeToken
     * @param converter
     * @param <T>
     */
    <T> Observable<T> rxRequest(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter);

    /**
     * rx请求，处理生命周期,返回BaseHttp，并做转换
     *
     * @param params
     * @param typeToken
     * @param observer
     * @param <T>
     */
    <T> void rxBaseHttpRequest(HttpRequestParams params, TypeToken typeToken, AbstractRxObserver<T> observer);

    /**
     * rx请求，不处理生命周期，返回BaseHttp，并做转换
     *
     * @param params
     * @param typeToken
     * @param <T>
     */
    <T> Observable<T> rxBaseHttpRequest(HttpRequestParams params, TypeToken<T> typeToken);

    /**
     * rx请求，获取Http Response字符串body
     *
     * @param params
     */
    Observable<String> rxRequestString(HttpRequestParams params);

    /**
     * rx请求，获取Http Response字符串body
     *
     * @param params
     */
    void rxRequestString(HttpRequestParams params, AbstractRxObserver<String> observer);

    /**
     * rxjava方式上传文件,异步
     *
     * @param params 请求参数
     */
    <T> Observable<RxUploadDownloadResult<T>> rxUploadFile(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter, boolean needString);

    /**
     * rxjava方式上传文件,异步
     *
     * @param params 请求参数
     */
    <T> void rxUploadFile(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter, boolean needString, AbstractRxObserver<RxUploadDownloadResult<T>> observer);

    /**
     * rxjava方式下载文件,异步
     *
     * @param params 请求参数
     */
    Observable<RxUploadDownloadResult<File>> rxDownload(HttpRequestParams params);

    /**
     * rxjava方式下载文件,异步
     *
     * @param params 请求参数
     */
    void rxDownload(HttpRequestParams params, AbstractRxObserver<RxUploadDownloadResult<File>> observer);

    /**
     * 同步下载文件
     *
     * @param httpParams                   http参数
     * @param downloadFolder               下载目标文件夹
     * @param downloadFileName             下载文件名称
     * @param fileDownloadProgressListener 文件下载进度监听器
     */
    void downloadSync(HttpRequestParams httpParams, String downloadFolder, String downloadFileName, FileDownloadProgressListener fileDownloadProgressListener) throws  Exception;
}
