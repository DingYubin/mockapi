package com.getech.android.httphelper;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

import com.getech.android.httphelper.constant.HttpCacheMode;
import com.getech.android.httphelper.httpinterface.AbstractHttpRequestListener;
import com.getech.android.httphelper.httpinterface.BaseHttpResult;
import com.getech.android.httphelper.httpinterface.FileDownloadProgressListener;
import com.getech.android.httphelper.httpinterface.HttpProvider;
import com.getech.android.httphelper.httpinterface.HttpRequestParams;
import com.getech.android.httphelper.httpinterface.ResultConverter;
import com.getech.android.httphelper.httpinterface.interceptor.HttpInterceptor;
import com.getech.android.httphelper.httpinterface.rx.AbstractRxObserver;
import com.getech.android.httphelper.httpinterface.rx.RxUploadDownloadResult;
import com.getech.android.httphelper.impl.OkGoHttpProvider;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

import io.reactivex.Observable;

public class HttpUtils {
    private static HashMap<String, HttpUtils> httpUtilsHashMap;
    private HttpProvider httpProvider;
    private String moduleTag;

    static {
        httpUtilsHashMap = new HashMap<>();
    }

    private HttpUtils(HttpProvider httpProvider) {
        this.httpProvider = httpProvider;
    }

    /**
     * 初始化
     *
     * @param application
     * @return
     */
    public static synchronized HttpUtils init(Application application) {
        String moduleTag = UUID.randomUUID().toString().replace("-", "");
        OkGoHttpProvider okGoHttpProvider = new OkGoHttpProvider(application);
        okGoHttpProvider.init();
        HttpUtils httpUtils = new HttpUtils(okGoHttpProvider);
        httpUtils.setModuleTag(moduleTag);
        httpUtilsHashMap.put(moduleTag, httpUtils);
        return httpUtils;
    }

    /**
     * 移除
     *
     * @param moduleTag
     */
    public static synchronized void removeHtttpUtils(String moduleTag) {
        HttpUtils httpUtils = httpUtilsHashMap.get(moduleTag);
        if (httpUtils != null) {
            httpUtils.cancelAll();
        }
        httpUtilsHashMap.remove(moduleTag);
    }

    public String getModuleTag() {
        return moduleTag;
    }

    public void setModuleTag(String moduleTag) {
        this.moduleTag = moduleTag;
    }

    public HttpUtils setBaseUrl(String baseUrl) {
        httpProvider.baseUrl(baseUrl);
        return this;
    }

    public HttpUtils setCommonHeaders(HashMap<String, String> commonHeaders) {
        httpProvider.commonHeaders(commonHeaders);
        return this;
    }

    public HttpUtils commonTimeout(int commonTimeout) {
        httpProvider.timeout(commonTimeout);
        return this;
    }

    public HttpUtils showLog(boolean showLog) {
        httpProvider.openLog(showLog);
        return this;
    }

    public HttpUtils setCacheMode(HttpCacheMode httpCacheMode) {
        httpProvider.cachedMode(httpCacheMode);
        return this;
    }

    public HttpUtils setCacheTime(long cacheTime) {
        httpProvider.cacheTime(cacheTime);
        return this;
    }

    public HttpUtils setSslFileName(String sslFileName) {
        httpProvider.sslFilePath(sslFileName);
        return this;
    }

    public HttpUtils setRetryCount(int retryCount) {
        httpProvider.retryCount(retryCount);
        return this;
    }

    /**
     * 添加拦截器
     *
     * @param httpInterceptor
     * @return
     */
    public HttpUtils addInterceptor(HttpInterceptor httpInterceptor) {
        httpProvider.addInterceptor(httpInterceptor);
        return this;
    }

    public static HttpUtils getInstance(String providerTag) {
        HttpUtils httpUtils = httpUtilsHashMap.get(providerTag);
        if (httpUtils == null) {
            throw new NullPointerException("should init HttpUtils first");
        }
        return httpUtils;
    }

    /**
     * 取消
     *
     * @param tag http tag
     */
    public void cancel(String tag) {
        httpProvider.cancel(tag);
    }

    /**
     * 取消所有的请求
     */
    public void cancelAll() {
        httpProvider.cancelAll();
    }

    /**
     * 异步网络请求
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    public void requestStringAsync(HttpRequestParams params, AbstractHttpRequestListener listener) {
        httpProvider.requestStringAsync(params, listener);
    }

    /**
     * 异步网络请求
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    public void requestBaseHttpAsync(HttpRequestParams params, AbstractHttpRequestListener listener, Type type) {
        httpProvider.requestBaseHttpAsync(params, listener, type);
    }

    /**
     * 同步网络请求
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    public String requestStringSync(HttpRequestParams params, AbstractHttpRequestListener listener) {
        return httpProvider.requestStringSync(params, listener);
    }

    /**
     * 同步网络请求
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    public <T> BaseHttpResult<T> requestBaseHttpSync(HttpRequestParams params, AbstractHttpRequestListener<BaseHttpResult<T>> listener, Type type) {
        return httpProvider.requestBaseHttpSync(params, listener, type);
    }

    /**
     * 下载文件,异步
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    public void download(HttpRequestParams params, AbstractHttpRequestListener listener) {
        httpProvider.download(params, listener);
    }

    /**
     * 上传文件,异步
     *
     * @param params   请求参数
     * @param listener http请求监听器
     */
    public void uploadFile(HttpRequestParams params, AbstractHttpRequestListener listener) {
        httpProvider.uploadFile(params, listener);
    }

    /**
     * rx请求，处理生命周期,调用方做数据处理
     *
     * @param params
     * @param typeToken
     * @param observer
     * @param converter
     * @param <T>
     */
    public <T> void rxRequest(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter, AbstractRxObserver<T> observer) {
        httpProvider.rxRequest(params, typeToken, converter, observer);
    }

    /**
     * rx请求，不处理生命周期，调用方做数据处理
     *
     * @param params
     * @param typeToken
     * @param converter
     * @param <T>
     */
    public <T> Observable<T> rxRequest(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter) {
        return httpProvider.rxRequest(params, typeToken, converter);
    }

    /**
     * rx请求，处理生命周期,返回BaseHttp，并做转换
     *
     * @param params
     * @param typeToken
     * @param observer
     * @param <T>
     */
    public <T> void rxBaseHttpRequest(HttpRequestParams params, TypeToken typeToken, AbstractRxObserver<T> observer) {
        httpProvider.rxBaseHttpRequest(params, typeToken, observer);
    }

    /**
     * rx请求，不处理生命周期，返回BaseHttp，并做转换
     *
     * @param params
     * @param typeToken
     * @param <T>
     */
    public <T> Observable<T> rxBaseHttpRequest(HttpRequestParams params, TypeToken<T> typeToken) {
        return httpProvider.rxBaseHttpRequest(params, typeToken);
    }

    /**
     * rx请求，获取Http Response字符串body
     *
     * @param params
     */
    public Observable<String> rxRequestString(HttpRequestParams params) {
        return httpProvider.rxRequestString(params);
    }

    /**
     * rx请求，获取Http Response字符串body
     *
     * @param params
     */
    public void rxRequestString(HttpRequestParams params, AbstractRxObserver<String> observer) {
        httpProvider.rxRequestString(params, observer);
    }

    /**
     * rxjava方式上传文件,异步
     *
     * @param params 请求参数
     */
    public <T> Observable<RxUploadDownloadResult<T>> rxUploadFile(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter, boolean needString) {
        return httpProvider.rxUploadFile(params, typeToken, converter, needString);
    }

    /**
     * rxjava方式上传文件,异步
     *
     * @param params 请求参数
     */
    public <T> void rxUploadFile(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter, boolean needString, AbstractRxObserver<RxUploadDownloadResult<T>> observer) {
        httpProvider.rxUploadFile(params, typeToken, converter, needString, observer);
    }

    /**
     * rxjava方式下载文件，异步
     *
     * @param params
     */
    public Observable<RxUploadDownloadResult<File>> rxDownload(HttpRequestParams params) {
        return httpProvider.rxDownload(params);
    }

    public void rxDownload(HttpRequestParams params, AbstractRxObserver<RxUploadDownloadResult<File>> observer) {
        httpProvider.rxDownload(params, observer);
    }

    /**
     * 同步下载文件
     *
     * @param params                       http参数
     * @param downloadFolder               下载目标文件夹
     * @param downloadFileName             下载文件名称
     * @param fileDownloadProgressListener 文件下载进度监听器
     */
    public void downloadSync(HttpRequestParams params, String downloadFolder, String downloadFileName, FileDownloadProgressListener fileDownloadProgressListener) throws Exception {
        if (TextUtils.isEmpty(downloadFolder)) {
            downloadFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (TextUtils.isEmpty(downloadFileName)) {
            downloadFileName = System.currentTimeMillis() + "";
        }
        httpProvider.downloadSync(params, downloadFolder, downloadFileName, fileDownloadProgressListener);
    }
}
