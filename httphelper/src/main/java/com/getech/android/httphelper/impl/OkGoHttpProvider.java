package com.getech.android.httphelper.impl;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.getech.android.httphelper.constant.Constants;
import com.getech.android.httphelper.constant.HttpCacheMode;
import com.getech.android.httphelper.constant.HttpMethodEnum;
import com.getech.android.httphelper.exception.ExceptionHandler;
import com.getech.android.httphelper.exception.UrlEmptyException;
import com.getech.android.httphelper.httpinterface.AbstractHttpRequestListener;
import com.getech.android.httphelper.httpinterface.ApplicationEmptyException;
import com.getech.android.httphelper.httpinterface.BaseHttpResult;
import com.getech.android.httphelper.httpinterface.FileDownloadProgressListener;
import com.getech.android.httphelper.httpinterface.HttpErrorInfo;
import com.getech.android.httphelper.httpinterface.HttpProvider;
import com.getech.android.httphelper.httpinterface.HttpRequestParams;
import com.getech.android.httphelper.httpinterface.ResultConverter;
import com.getech.android.httphelper.httpinterface.StringResultConverter;
import com.getech.android.httphelper.httpinterface.interceptor.HttpInterceptor;
import com.getech.android.httphelper.httpinterface.rx.AbstractRxObserver;
import com.getech.android.httphelper.httpinterface.rx.RxUploadDownloadResult;
import com.getech.android.httphelper.impl.cache.policy.OkGoDefaultCachePolicy;
import com.getech.android.httphelper.impl.cache.policy.OkGoFirstCacheRequestPolicy;
import com.getech.android.httphelper.impl.cache.policy.OkGoNoCachePolicy;
import com.getech.android.httphelper.impl.cache.policy.OkGoNoneCacheRequestPolicy;
import com.getech.android.httphelper.impl.cache.policy.OkGoRequestFailedCachePolicy;
import com.getech.android.httphelper.impl.interceptor.OkGoHttpInterceptor;
import com.getech.android.httphelper.util.DownloadUtil;
import com.getech.android.httphelper.util.GsonConvertUtil;
import com.getech.android.utils.FileUtils;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cache.policy.CachePolicy;
import com.lzy.okgo.convert.FileConvert;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;
import com.lzy.okrx2.adapter.ObservableBody;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;
import com.lzy.okserver.upload.UploadListener;
import com.lzy.okserver.upload.UploadTask;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * OKGo实现的HttpProvider
 */
public class OkGoHttpProvider implements HttpProvider {
    /**
     * 最大线程个数
     */
    private static final int MAX_POLL_SIZE = Runtime.getRuntime().availableProcessors();
    /**
     * http请求的dispatcher,使用LinkedBlockingQeque
     */
    private static final Dispatcher okgoDispatcher = new Dispatcher(new ThreadPoolExecutor(MAX_POLL_SIZE, MAX_POLL_SIZE, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), okhttp3.internal.Util.threadFactory("OkGoHttpProvider", false)));

    private Application application;
    private String baseUrl;
    private int commonTimeout;
    private HashMap<String, String> commonHeaders;
    private boolean openLog;
    private String downloadFolder;
    private OkDownload okDownload;
    private OkUpload okUpload;
    private HashMap<String, OkHttpClient> okHttpClientMap;
    private HttpCacheMode httpCacheMode = HttpCacheMode.NO_CACHE;
    private long cacheTime;
    private String sslFilePath;
    private int retryCount = Constants.HTTP_RETRY_COUNT;
    boolean inited = false;
    /**
     * The Common builder.
     */
    OkHttpClient.Builder commonBuilder;
    /**
     * The Common http client.
     */
    OkHttpClient commonHttpClient;
    HttpHeaders commonHttpHeaders;
    private ArrayList<HttpInterceptor> interceptors;

    public OkGoHttpProvider(Application application) {
        this.setApplication(application);
    }

    @Override
    public void init() {
        if (application == null) {
            throw new ApplicationEmptyException("application is null,please check");
        }
        if (inited) {
            return;
        }
        inited = true;
        okHttpClientMap = new HashMap<>();
        if (commonTimeout == 0) {
            commonTimeout = Constants.HTTP_DEFAULT_TIMEOUT;
        }
        //初始化okgo
        OkGo.getInstance().init(application);
        okDownload = OkDownload.getInstance();
        downloadFolder = Environment.getExternalStorageDirectory().getPath() + "/" + (application == null ? "" : application.getPackageName()) + "/download/";
        File folder = new File(downloadFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        okDownload.setFolder(downloadFolder);
        //设置一次只能下载一个文件
        okDownload.getThreadPool().setCorePoolSize(1);

        okUpload = OkUpload.getInstance();
        //设置一次只能上传一个文件
        okUpload.getThreadPool().setCorePoolSize(1);
        //通用头
        commonHttpHeaders = new HttpHeaders();
        if (commonHeaders == null || commonHeaders.size() == 0) {
            commonHeaders = new HashMap<>();
        }
        if (commonHeaders != null && commonHeaders.size() > 0) {
            Set<String> keys = commonHeaders.keySet();
            for (String key : keys) {
                commonHttpHeaders.put(key, commonHeaders.get(key));
            }
        }
        commonBuilder = getBuilderWithTimeout(commonTimeout, openLog);
        commonHttpClient = commonBuilder.build();
        OkGo.getInstance().init(application);
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    /**
     * 取消
     *
     * @param tag http tag
     */
    @Override
    public void cancel(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        if (okHttpClientMap != null && okHttpClientMap.size() > 0) {
            OkHttpClient okHttpClient = okHttpClientMap.get(tag);
            if (okHttpClient != null && okHttpClient != commonHttpClient) {
                for (Call call : okHttpClient.dispatcher().queuedCalls()) {
                    if (tag.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
                for (Call call : okHttpClient.dispatcher().runningCalls()) {
                    if (tag.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
                removeHttpClient(tag);
            }
        }
        //取消通用HttpClient中包含的请求
        if (commonHttpClient != null) {
            for (Call call : commonHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            for (Call call : commonHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }
        OkGo.getInstance().cancelTag(tag);

        DownloadTask downloadTask = okDownload.getTask(tag);
        if (downloadTask != null) {
            downloadTask.remove(true);
            downloadTask = null;
        }

        UploadTask uploadTask = okUpload.getTask(tag);
        if (uploadTask != null) {
            uploadTask.remove();
            uploadTask = null;
        }
    }

    /**
     * 取消所有请求
     */
    @Override
    public void cancelAll() {
        if (okHttpClientMap != null && okHttpClientMap.size() > 0) {
            for (OkHttpClient httpClient : okHttpClientMap.values()) {
                for (Call call : httpClient.dispatcher().queuedCalls()) {
                    call.cancel();
                }
                for (Call call : httpClient.dispatcher().runningCalls()) {
                    call.cancel();
                }
            }
        }
        if (commonHttpClient != null) {
            for (Call call : commonHttpClient.dispatcher().queuedCalls()) {
                call.cancel();
            }
            for (Call call : commonHttpClient.dispatcher().runningCalls()) {
                call.cancel();
            }
        }
        OkGo.getInstance().cancelAll();
        okHttpClientMap.clear();
        okDownload.removeAll();
        okDownload.removeAll();
    }

    /**
     * 设置请求的baseUrl
     *
     * @param baseUrl baseUr
     */
    @Override
    public void baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间，单位秒
     */
    @Override
    public void timeout(int timeout) {
        this.commonTimeout = timeout;
    }

    /**
     * 设置通用的header
     *
     * @param headers 通用header
     */
    @Override
    public void commonHeaders(HashMap headers) {
        this.commonHeaders = headers;
    }

    @Override
    public void cachedMode(HttpCacheMode cacheMode) {
        this.httpCacheMode = cacheMode;
    }

    @Override
    public void cacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    @Override
    public void sslFilePath(String sslFilePath) {
        this.sslFilePath = sslFilePath;
    }

    @Override
    public void retryCount(int retryCount) {
        if (retryCount > Constants.MAX_RETRY_COUNT) {
            retryCount = Constants.MAX_RETRY_COUNT;
        }
        this.retryCount = retryCount;
    }

    /**
     * 是否打开日志
     *
     * @param openLog 是否打开日志
     */
    @Override
    public void openLog(boolean openLog) {
        this.openLog = openLog;
    }

    /**
     * 添加拦截器
     *
     * @param httpInterceptor
     */
    @Override
    public void addInterceptor(HttpInterceptor httpInterceptor) {
        if (interceptors == null) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(httpInterceptor);
    }

    private String contactRequestUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            if (TextUtils.isEmpty(baseUrl)) {
                throw new UrlEmptyException();
            }
            return baseUrl;
        }
        if (url.startsWith("http") || url.startsWith("https")) {
            return url;
        } else {
            if (TextUtils.isEmpty(baseUrl)) {
                throw new UrlEmptyException();
            }
            if (baseUrl.endsWith("/") || url.startsWith("/")) {
                if (baseUrl.endsWith("/") && url.startsWith("/")) {
                    if (url.length() > 1) {
                        return baseUrl + url.substring(1);
                    } else {
                        return baseUrl;
                    }
                }
                return baseUrl + url;
            } else {
                return baseUrl + "/" + url;
            }
        }
    }

    private OkHttpClient prepareClient(int timeout) {
        OkHttpClient.Builder builder = commonBuilder;
        OkHttpClient okHttpClient = commonHttpClient;
        if (timeout > 0 && timeout != commonTimeout) {
            builder = getBuilderWithTimeout(timeout, openLog);
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    private Request setHttpClientParams(Request request, HashMap<String, String> formData, HashMap<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (!isHashMapEmpty(commonHeaders)) {
            Set<String> keys = commonHeaders.keySet();
            for (String key : keys) {
                httpHeaders.put(key, commonHeaders.get(key));
            }
        }
        if (!isHashMapEmpty(headers)) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                httpHeaders.put(key, headers.get(key));
            }
        }
        if (httpHeaders.getNames() != null && httpHeaders.getNames().size() > 0) {
            request = request.headers(httpHeaders);
        }
        HttpParams httpParams = new HttpParams();
        if (!isHashMapEmpty(formData)) {
            Set<String> keys = formData.keySet();
            for (String key : keys) {
                httpParams.put(key, formData.get(key));
            }
        }
        if (formData != null && formData.size() > 0) {
            request = request.params(httpParams);
        }
        return request;
    }

    private Request getRequestByParam(String url, HttpMethodEnum method, HashMap<String, String> headers, HashMap<String, String> formData, String requestBody, boolean json, String tag, boolean download, int timeout) {
        Request request;
        String requestUrl = contactRequestUrl(url);
        OkHttpClient okHttpClient = prepareClient(timeout);
        if (HttpMethodEnum.POST == method) {
            request = OkGo.<String>post(requestUrl);
            request.client(okHttpClient)
                    .cacheMode(transformCachedMode(httpCacheMode))
                    .cacheTime(cacheTime)
                    .retryCount(retryCount)
                    .tag(tag);

            if (!TextUtils.isEmpty(requestBody)) {
                PostRequest postRequest = (PostRequest) request;
                if (!json) {
                    postRequest.upString(requestBody);
                } else {
                    postRequest.upJson(requestBody);
                }
            }
        } else {
            request = OkGo.<String>get(requestUrl);
            if (!download) {
                request.client(okHttpClient)
                        .cacheMode(transformCachedMode(httpCacheMode))
                        .cacheTime(cacheTime)
                        .retryCount(retryCount)
                        .tag(tag);
            } else {
                request.client(okHttpClient)
                        .cacheMode(CacheMode.NO_CACHE)
                        .cacheTime(cacheTime)
                        .tag(tag);
            }
        }
        CachePolicy cachePolicy = preparePolicy(request, transformCachedMode(httpCacheMode));
        if (download) {
            cachePolicy = new OkGoNoCachePolicy<>(request);
        }
        request = request.cachePolicy(cachePolicy);
        setHttpClientParams(request, formData, headers);
        if (okHttpClientMap.get(tag) == null && okHttpClient != commonHttpClient) {
            okHttpClientMap.put(tag, okHttpClient);
        }
        return request;
    }

    @Override
    public void requestStringAsync(HttpRequestParams params, AbstractHttpRequestListener listener) {
        checkUrl(params.getUrl(), baseUrl);
        prepareHeader(params);
        Request request = getRequestByParam(params.getUrl(), params.getHttpMethod(), params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), false, params.getTimeout());
        request.execute(new CustomStringCallback(listener, params.getTag()) {
            @Override
            public void removeHttpClient(String tag) {
                OkGoHttpProvider.this.removeHttpClient(params.getTag());
            }
        });
    }

    @Override
    public String requestStringSync(HttpRequestParams params, AbstractHttpRequestListener listener) {
        checkUrl(params.getUrl(), baseUrl);
        prepareHeader(params);
        String responseStr = "";
        Request request = getRequestByParam(params.getUrl(), params.getHttpMethod(), params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), false, params.getTimeout());
        int httpStatusCode = 0;
        try {
            if (listener != null) {
                listener.onStart(params.getTag());
                listener.onProgress(params.getTag(), 0, 0, 0);
            }
            okhttp3.Response response = request.execute();
            ResponseBody responseBody = response.body();
            httpStatusCode = response.code();
            responseStr = responseBody.string();
            if (listener != null) {
                listener.onProgress(params.getTag(), 100, response == null ? 0 : responseStr.length(), response == null ? 0 : responseStr.length());
                listener.onFinish(params.getTag());
            }
            removeHttpClient(params.getTag());
            response.close();
        } catch (Throwable e) {
            if (listener != null) {
                listener.onError(params.getTag(), new HttpErrorInfo(httpStatusCode, e));
            }
        }
        return responseStr;
    }

    @Override
    public <T> void requestBaseHttpAsync(HttpRequestParams params, AbstractHttpRequestListener<BaseHttpResult<T>> listener, Type type) {
        checkUrl(params.getUrl(), baseUrl);
        prepareHeader(params);
        Request request = getRequestByParam(params.getUrl(), params.getHttpMethod(), params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), false, params.getTimeout());
        request.execute(new CustomJsonCallback(listener, params.getTag(), type) {
            @Override
            public void removeHttpClient(String tag) {
                OkGoHttpProvider.this.removeHttpClient(tag);
            }
        });
    }

    @Override
    public <T> BaseHttpResult<T> requestBaseHttpSync(HttpRequestParams params, AbstractHttpRequestListener<BaseHttpResult<T>> listener, Type type) {
        checkUrl(params.getUrl(), baseUrl);
        prepareHeader(params);
        Request request = getRequestByParam(params.getUrl(), params.getHttpMethod(), params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), false, params.getTimeout());
        String responseStr = "";
        int httpStatusCode = 0;
        try {
            if (listener != null) {
                listener.onStart(params.getTag());
                listener.onProgress(params.getTag(), 0, 0, 0);
            }
            okhttp3.Response response = request.execute();
            ResponseBody responseBody = response.body();
            httpStatusCode = response.code();

            ResultConverter<BaseHttpResult<T>> externalConverter = listener.getConverter();
            BaseHttpResult<T> result;
            if (externalConverter != null) {
                result = externalConverter.convertResponse(responseBody.string());
            } else {
                OKGoResultConverter<BaseHttpResult<T>> okGoResultConverter = new OKGoResultConverter<>(type);
                result = okGoResultConverter.convertResponse(response);
            }
            if (listener != null) {
                listener.onProgress(params.getTag(), 100, response == null ? 0 : responseStr.length(), response == null ? 0 : responseStr.length());
                listener.onFinish(params.getTag());
            }
            removeHttpClient(params.getTag());
            response.close();
            return result;
        } catch (Throwable e) {
            if (listener != null) {
                listener.onError(params.getTag(), new HttpErrorInfo(httpStatusCode, e));
            }
        }
        return null;
    }

    @Override
    public void download(HttpRequestParams params, final AbstractHttpRequestListener listener) {
        checkUrl(params.getUrl(), baseUrl);
        if (okDownload.getTask(params.getTag()) != null) {
            DownloadTask existsTask = okDownload.getTask(params.getTag());
            existsTask.remove(true);
            okDownload.removeTask(params.getTag());
        }
        Request request = getRequestByParam(params.getUrl(), HttpMethodEnum.GET, params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), true, params.getTimeout());
        request.converter(new FileConvert());
        DownloadTask downloadTask = OkDownload.request(params.getTag(), request);

        String tempFolder = DownloadUtil.getTempCacheFolder(application);
        String tempFileName = DownloadUtil.getTempCacheFileName();
        //下载到临时文件夹中
        downloadTask.folder(tempFolder);
        downloadTask.fileName(tempFileName);

        downloadTask.register(new DownloadListener(params.getTag()) {
            @Override
            public void onStart(Progress progress) {
                if (listener != null) {
                    listener.onStart(progress.tag);
                }
            }

            @Override
            public void onProgress(Progress progress) {
                Log.d("download", "threadL " + Thread.currentThread().getName() + " onProgress: " + progress.fraction);
                if (listener != null) {
                    listener.onProgress(progress.tag, (int) (progress.fraction * 100), progress.currentSize, progress.totalSize);
                }
            }

            @Override
            public void onError(Progress progress) {
                if (listener != null) {
                    listener.onError(progress.tag, new HttpErrorInfo(progress.status, progress.exception));
                }
                FileUtils.delete(tempFolder + tempFileName);
                removeHttpClient(progress.tag);
                downloadTask.remove(true);
                okDownload.removeTask(progress.tag);
            }

            @Override
            public void onFinish(File file, Progress progress) {
                if (listener != null) {
                    try {
                        String folder = "";
                        String fileName = "";
                        if (!TextUtils.isEmpty(params.getFileDownloadPath())) {
                            int lastIndex = params.getFileDownloadPath().lastIndexOf("/");
                            if (lastIndex > 0) {
                                folder = params.getFileDownloadPath().substring(0, lastIndex);
                                fileName = params.getFileDownloadPath().substring(lastIndex + 1);
                            }
                        }
                        //移动文件
                        boolean moveSuccess = false;
                        if (!TextUtils.isEmpty(folder) && !TextUtils.isEmpty(fileName)) {
                            moveSuccess = FileUtils.moveFile(file, new File(params.getFileDownloadPath()));
                        }
                        if (!moveSuccess) {
                            listener.onSuccess(progress.tag, file.getAbsolutePath());
                            downloadTask.remove(false);
                        } else {
                            listener.onSuccess(progress.tag, params.getFileDownloadPath());
                            downloadTask.remove(true);
                        }
                    } catch (Throwable e) {
                        listener.onError(progress.tag, new HttpErrorInfo(HttpErrorInfo.ERROR_CODE_PROCESS_RESPONSE, e));
                        downloadTask.remove(true);
                        okDownload.removeTask(progress.tag);
                        removeHttpClient(progress.tag);
                        return;
                    }
                    listener.onFinish(progress.tag);
                }
                okDownload.removeTask(progress.tag);
                removeHttpClient(progress.tag);
            }

            @Override
            public void onRemove(Progress progress) {
                removeHttpClient(progress.tag);
            }
        })
                .save()
                .start();
    }

    @Override
    public void uploadFile(HttpRequestParams params, final AbstractHttpRequestListener AbstractHttpRequestListener) {
        if (TextUtils.isEmpty(params.getUploadFilePath())) {
            throw new NullPointerException("empty upload file path");
        }
        File uploadFile = new File(params.getUploadFilePath());
        if (uploadFile == null || !uploadFile.exists()) {
            throw new NullPointerException("file not exists");
        }
        if (TextUtils.isEmpty(params.getUploadFileKey())) {
            throw new NullPointerException("empty upload file key");
        }
        checkUrl(params.getUrl(), baseUrl);
        Request request = getRequestByParam(params.getUrl(), HttpMethodEnum.POST, params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), false, params.getTimeout());
        PostRequest fileUploadRequest = (PostRequest) request;
        fileUploadRequest.params(params.getUploadFileKey(), new File(params.getUploadFilePath()));
        fileUploadRequest.converter(new StringConvert());
        UploadTask uploadTask = OkUpload.request(params.getTag(), fileUploadRequest);
        uploadTask.register(new UploadListener<String>(fileUploadRequest) {
            @Override
            public void onStart(Progress progress) {
                if (AbstractHttpRequestListener != null) {
                    try {
                        AbstractHttpRequestListener.onStart(progress.tag);
                    } catch (Throwable e) {
                        AbstractHttpRequestListener.onError(progress.tag, new HttpErrorInfo(HttpErrorInfo.ERROR_CODE_PROCESS_RESPONSE, e));
                        removeHttpClient(progress.tag);
                    }
                }
            }

            @Override
            public void onProgress(Progress progress) {
                if (AbstractHttpRequestListener != null) {
                    AbstractHttpRequestListener.onProgress(progress.tag, (int) (progress.fraction * 100), progress.currentSize, progress.totalSize);
                }
            }

            @Override
            public void onError(Progress progress) {
                if (AbstractHttpRequestListener != null) {
                    AbstractHttpRequestListener.onError(progress.tag, new HttpErrorInfo(progress.status, progress.exception));
                }
                removeHttpClient(progress.tag);
                uploadTask.remove();
                okUpload.removeTask(progress.tag);
            }

            @Override
            public void onFinish(String uploadResponse, Progress progress) {
                if (AbstractHttpRequestListener != null) {
                    try {
                        AbstractHttpRequestListener.onSuccess(progress.tag, uploadResponse);
                    } catch (Throwable e) {
                        AbstractHttpRequestListener.onError(progress.tag, new HttpErrorInfo(HttpErrorInfo.ERROR_CODE_PROCESS_RESPONSE, e));
                    }
                    AbstractHttpRequestListener.onFinish(progress.tag);
                }
                removeHttpClient(progress.tag);
                uploadTask.remove();
                okUpload.removeTask(progress.tag);
            }

            @Override
            public void onRemove(Progress progress) {
                removeHttpClient(progress.tag);
            }
        })
                .save()
                .start();
    }

    private void removeHttpClient(String tag) {
        if (okHttpClientMap.get(tag) != null && okHttpClientMap.get(tag) != commonHttpClient) {
            okHttpClientMap.remove(tag);
        }
    }

    private OkHttpClient.Builder getBuilderWithTimeout(int timeout, boolean openLog) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        if (openLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("YTHttp");
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            loggingInterceptor.setColorLevel(Level.INFO);
            builder.addInterceptor(loggingInterceptor);
        }
        if (timeout == 0) {
            timeout = Constants.HTTP_DEFAULT_TIMEOUT;
        }
        builder.readTimeout(timeout, TimeUnit.SECONDS);
        builder.writeTimeout(timeout, TimeUnit.SECONDS);
        builder.connectTimeout(timeout, TimeUnit.SECONDS);
        if (interceptors != null) {
            for (HttpInterceptor interceptor : interceptors) {
                builder.addInterceptor(new OkGoHttpInterceptor(interceptor));
            }
        }
        boolean sslSuccess = false;
        try {
            boolean needSsl = false;
            if (!TextUtils.isEmpty(sslFilePath)) {
                File sslFile = new File(sslFilePath);
                if (sslFile != null && sslFile.exists()) {
                    needSsl = true;
                }
            }
            if (needSsl) {
                HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(application.getAssets().open(sslFilePath));
                builder.sslSocketFactory(sslParams3.sSLSocketFactory, sslParams3.trustManager);
                sslSuccess = true;
            }
        } catch (Throwable e) {
            if (openLog && e != null) {
                e.printStackTrace();
            }
            sslSuccess = false;
        }
        if (!sslSuccess) {
            HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
            builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        }
        builder.hostnameVerifier(new SafeHostnameVerifier());
        builder.dispatcher(okgoDispatcher);
        return builder;
    }

    private boolean isHashMapEmpty(HashMap map) {
        return map == null || map.size() == 0;
    }

    private CacheMode transformCachedMode(HttpCacheMode httpCacheMode) {
        switch (httpCacheMode) {
            case DEFAULT:
                return CacheMode.DEFAULT;
            case NO_CACHE:
                return CacheMode.NO_CACHE;
            case REQUEST_FAILED_READ_CACHE:
                return CacheMode.REQUEST_FAILED_READ_CACHE;
            case IF_NONE_CACHE_REQUEST:
                return CacheMode.IF_NONE_CACHE_REQUEST;
            case FIRST_CACHE_THEN_REQUEST:
                return CacheMode.FIRST_CACHE_THEN_REQUEST;
            default:
                return CacheMode.DEFAULT;
        }
    }

    private <T> CachePolicy<T> preparePolicy(Request request, CacheMode cacheMode) {
        CachePolicy<T> policy;
        switch (cacheMode) {
            case NO_CACHE:
                policy = new OkGoNoCachePolicy<>(request);
                break;
            case IF_NONE_CACHE_REQUEST:
                policy = new OkGoNoneCacheRequestPolicy<>(request);
                break;
            case FIRST_CACHE_THEN_REQUEST:
                policy = new OkGoFirstCacheRequestPolicy<>(request);
                break;
            case REQUEST_FAILED_READ_CACHE:
                policy = new OkGoRequestFailedCachePolicy<>(request);
                break;
            case DEFAULT:
            default:
                policy = new OkGoDefaultCachePolicy<>(request);
                break;

        }
        return policy;
    }

    private <T> Observable<T> getRxPostRequestByParam(String url, HashMap<String, String> headers, HashMap<String, String> formData, String requestBody, boolean json, String tag, int timeout, ResultConverter<T> converter, boolean needString) {
        String requestUrl = contactRequestUrl(url);
        OkHttpClient okHttpClient = prepareClient(timeout);
        PostRequest<String> postRequest = OkGo.<String>post(requestUrl)
                .client(okHttpClient)
                .cacheMode(transformCachedMode(httpCacheMode))
                .cacheTime(cacheTime)
                .retryCount(retryCount)
                .tag(tag);

        if (!TextUtils.isEmpty(requestBody)) {
            if (!json) {
                postRequest.upString(requestBody);
            } else {
                postRequest.upJson(requestBody);
            }
        }
        CachePolicy cachePolicy = preparePolicy(postRequest, transformCachedMode(httpCacheMode));
        postRequest = postRequest.cachePolicy(cachePolicy);
        setHttpClientParams(postRequest, formData, headers);
        if (okHttpClientMap.get(tag) == null && okHttpClient != commonHttpClient) {
            okHttpClientMap.put(tag, okHttpClient);
        }
        Observable<String> observable = postRequest.converter(new StringConvert())
                .adapt(new ObservableBody<String>())
                .compose(io_main());
        if (needString) {
            return (Observable<T>) observable.onErrorResumeNext(new ErrorResumeFunction<>(tag));
        }
        if (converter instanceof BaseHttpResultConverter) {
            return observable.compose(handleBaseHttpResult((BaseHttpResultConverter<T>) converter, tag));
        } else {
            return observable.compose(handleResponse(converter, tag));
        }
    }

    private <T> Observable<T> getRxGetRequestByParam(String url, HashMap<String, String> headers, HashMap<String, String> formData, String tag, int timeout, ResultConverter<T> converter, boolean needString) {
        String requestUrl = contactRequestUrl(url);
        OkHttpClient okHttpClient = prepareClient(timeout);
        GetRequest<String> getRequest = OkGo.<String>get(requestUrl)
                .client(okHttpClient)
                .cacheMode(transformCachedMode(httpCacheMode))
                .cacheTime(cacheTime)
                .retryCount(retryCount)
                .tag(tag);
        setHttpClientParams(getRequest, formData, headers);
        if (okHttpClientMap.get(tag) == null && okHttpClient != commonHttpClient) {
            okHttpClientMap.put(tag, okHttpClient);
        }
        CachePolicy cachePolicy = preparePolicy(getRequest, transformCachedMode(httpCacheMode));
        getRequest = getRequest.cachePolicy(cachePolicy);
        Observable<String> observable = getRequest.converter(new StringConvert())
                .adapt(new ObservableBody<String>())
                .compose(io_main());
        if (needString) {
            return (Observable<T>) observable.onErrorResumeNext(new ErrorResumeFunction<>(tag));
        }
        if (converter instanceof BaseHttpResultConverter) {
            return observable.compose(handleBaseHttpResult((BaseHttpResultConverter<T>) converter, tag));
        } else {
            return observable.compose(handleResponse(converter, tag));
        }
    }

    @Override
    public <T> void rxRequest(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter, AbstractRxObserver<T> observer) {
        Observable<T> observable = rxRequest(params, typeToken, converter);
        observable.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(observer.getLifecycleOwner())))
                .subscribeWith(observer);
    }

    @Override
    public <T> Observable<T> rxRequest(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter) {
        checkUrl(params.getUrl(), baseUrl);
        prepareHeader(params);
        if (params.getHttpMethod() == HttpMethodEnum.GET) {
            return getRxGetRequestByParam(params.getUrl(), params.getHeader(), params.getFormData(), params.getTag(), params.getTimeout(), converter, false);
        } else {
            return getRxPostRequestByParam(params.getUrl(), params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), params.getTimeout(), converter, false);
        }
    }

    @Override
    public <T> void rxBaseHttpRequest(HttpRequestParams params, TypeToken typeToken, AbstractRxObserver<T> observer) {
        Observable<T> observable = rxBaseHttpRequest(params, typeToken);
        observable.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(observer.getLifecycleOwner())))
                .subscribeWith(observer);
    }

    @Override
    public <T> Observable<T> rxBaseHttpRequest(HttpRequestParams params, TypeToken<T> typeToken) {
        checkUrl(params.getUrl(), baseUrl);
        prepareHeader(params);
        if (params.getHttpMethod() == HttpMethodEnum.GET) {
            return getRxGetRequestByParam(params.getUrl(), params.getHeader(), params.getFormData(), params.getTag(), params.getTimeout(), new BaseHttpResultConverter<T>(typeToken.getType()), false);
        } else {
            return getRxPostRequestByParam(params.getUrl(), params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), params.getTimeout(), new BaseHttpResultConverter<T>(typeToken.getType()), false);
        }
    }

    /**
     * rx请求，获取Http Response字符串body
     *
     * @param params
     */
    @Override
    public Observable<String> rxRequestString(HttpRequestParams params) {
        checkUrl(params.getUrl(), baseUrl);
        prepareHeader(params);
        if (params.getHttpMethod() == HttpMethodEnum.GET) {
            return getRxGetRequestByParam(params.getUrl(), params.getHeader(), params.getFormData(), params.getTag(), params.getTimeout(), new StringResultConverter(), true);
        } else {
            return getRxPostRequestByParam(params.getUrl(), params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), params.getTimeout(), new StringResultConverter(), true);
        }
    }

    /**
     * rx请求，获取Http Response字符串body
     *
     * @param params
     */
    @Override
    public void rxRequestString(HttpRequestParams params, AbstractRxObserver<String> observer) {
        Observable<String> observable = rxRequestString(params);
        observable.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(observer.getLifecycleOwner())))
                .subscribeWith(observer);
    }


    public void checkUrl(String url, String baseUrl) {
        if (TextUtils.isEmpty(url) && TextUtils.isEmpty(baseUrl)) {
            throw new UrlEmptyException();
        }
    }

    public void prepareHeader(HttpRequestParams params) {
        if (isHashMapEmpty(params.getHeader()) && (isHashMapEmpty(commonHeaders) || !commonHeaders.containsKey("Content-Type"))) {
            HashMap<String, String> header = new HashMap<>();
            header.put("Content-Type", "text/plain");
            params.setHeader(header);
        }
    }

    private static <T> ObservableTransformer<T, T> io_main() {
        return upstream ->
                upstream.subscribeOn(Schedulers.from(okgoDispatcher.executorService()))
                        .observeOn(AndroidSchedulers.mainThread());
    }

    private static <T> ObservableTransformer<String, T> handleResponse(ResultConverter<T> converter, String tag) {
        return upstream -> upstream
                .onErrorResumeNext(new ErrorResumeFunction<>(tag))
                .flatMap(new ResponseFunction<T>(converter, tag));
    }

    /**
     * 非服务器产生的异常，比如本地无无网络请求，Json数据解析错误等等。
     *
     * @param <T>
     */
    private static class ErrorResumeFunction<T> implements Function<Throwable, ObservableSource<String>> {
        String tag;

        public ErrorResumeFunction(String tag) {
            this.tag = tag;
        }

        @Override
        public ObservableSource<String> apply(Throwable throwable) throws Exception {
            return Observable.error(ExceptionHandler.handleException(throwable, tag));
        }
    }

    /**
     * 字符串转为指定类型数据
     *
     * @param <T>
     */
    private static class ResponseFunction<T> implements Function<String, ObservableSource<T>> {
        ResultConverter<T> converter;
        String tag;

        public ResponseFunction(ResultConverter<T> resultConverter, String tag) {
            this.converter = resultConverter;
            this.tag = tag;
        }

        @Override
        public ObservableSource<T> apply(String responseStr) throws Exception {
            T t = converter.convertResponse(responseStr);
            if (t != null) {
                return Observable.just(converter.convertResponse(responseStr));
            } else {
                return Observable.error(ExceptionHandler.handleSuccessButDataNull("服务器返回数据为空，或格式不正确", tag));
            }
        }
    }

    private static <T> ObservableTransformer<String, T> handleBaseHttpResult(BaseHttpResultConverter<T> converter, String tag) {
        return upstream -> upstream
                .onErrorResumeNext(new ErrorResumeFunction<>(tag))
                .flatMap(new BaseHttpResultFunction<T>(converter, tag));
    }

    /**
     * 转为BaseHttpResult,返回data值
     *
     * @param <T>
     */
    private static class BaseHttpResultFunction<T> implements Function<String, ObservableSource<T>> {
        BaseHttpResultConverter<T> converter;
        String tag;

        public BaseHttpResultFunction(BaseHttpResultConverter<T> resultConverter, String tag) {
            this.converter = resultConverter;
            this.tag = tag;
        }

        @Override
        public ObservableSource<T> apply(String responseStr) throws Exception {
            BaseHttpResult baseHttpResult = GsonConvertUtil.getGson().fromJson(responseStr, BaseHttpResult.class);

            if (baseHttpResult.isSuccess()) {
                if (null != baseHttpResult.data) {
                    return Observable.just(converter.convertResponse(GsonConvertUtil.getGson().toJson(baseHttpResult.data)));
                } else {
                    return Observable.error(ExceptionHandler.handleSuccessButDataNull(baseHttpResult.msg, tag));
                }
            } else {
                return Observable.error(ExceptionHandler.handleProtocolException(baseHttpResult.code, baseHttpResult.msg, tag));
            }
        }
    }

    /**
     * rxjava方式上传文件,异步
     *
     * @param params 请求参数
     */
    @Override
    public <T> Observable<RxUploadDownloadResult<T>> rxUploadFile(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter, boolean needString) {
        if (TextUtils.isEmpty(params.getUploadFilePath())) {
            throw new NullPointerException("empty upload file path");
        }
        File uploadFile = new File(params.getUploadFilePath());
        if (uploadFile == null || !uploadFile.exists()) {
            throw new NullPointerException("file not exists");
        }
        if (TextUtils.isEmpty(params.getUploadFileKey())) {
            throw new NullPointerException("empty upload file key");
        }
        ResultConverter<T> resultConverter = converter;
        if (converter == null) {
            resultConverter = new BaseHttpResultConverter<T>(typeToken.getType());
        }
        if (needString) {
            resultConverter = new StringResultConverter();
        }
        final ResultConverter<T> finalResultConverter = resultConverter;
        checkUrl(params.getUrl(), baseUrl);
        Request request = getRequestByParam(params.getUrl(), HttpMethodEnum.POST, params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), false, params.getTimeout());
        request = request.cacheMode(CacheMode.NO_CACHE);
        PostRequest fileUploadRequest = (PostRequest) request;
        fileUploadRequest.params(params.getUploadFileKey(), uploadFile);
        fileUploadRequest.converter(new StringConvert());
        Observable<RxUploadDownloadResult<T>> observable = Observable.create(new ObservableOnSubscribe<RxUploadDownloadResult<T>>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<RxUploadDownloadResult<T>> emitter) throws Exception {
                UploadTask uploadTask = OkUpload.request(params.getTag(), fileUploadRequest);
                uploadTask.register(new UploadListener<String>(fileUploadRequest) {
                    @Override
                    public void onStart(Progress progress) {
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        RxUploadDownloadResult uploadDownloadResult = new RxUploadDownloadResult((int) (progress.fraction * 100), progress.currentSize, progress.totalSize, null, params.getTag(), RxUploadDownloadResult.UPLOAD_DOWNLOAD_STATUS_PROCESSING);
                        emitter.onNext(uploadDownloadResult);
                    }

                    @Override
                    public void onError(Progress progress) {
                        emitter.onError(progress.exception);
                        removeHttpClient(progress.tag);
                        uploadTask.remove();
                        okUpload.removeTask(progress.tag);
                    }

                    @Override
                    public void onFinish(String uploadResult, Progress progress) {
                        try {
                            T result = finalResultConverter.convertResponse(uploadResult);
                            RxUploadDownloadResult uploadDownloadResult = new RxUploadDownloadResult(100, progress.currentSize, progress.totalSize, result, params.getTag(), RxUploadDownloadResult.UPLOAD_DOWNLOAD_STATUS_FINISHED);
                            emitter.onNext(uploadDownloadResult);
                        } catch (Throwable e) {
                            emitter.onError(progress.exception);
                        }
                        removeHttpClient(progress.tag);
                        uploadTask.remove();
                        okUpload.removeTask(progress.tag);
                    }

                    @Override
                    public void onRemove(Progress progress) {
                        emitter.onNext(new RxUploadDownloadResult((int) (progress.fraction * 100), progress.currentSize, progress.totalSize, null, params.getTag(), RxUploadDownloadResult.UPLOAD_DOWNLOAD_STATUS_CANCELED));
                        removeHttpClient(progress.tag);
                    }
                }).save()
                        .start();
            }
        });
        return observable;
    }

    @Override
    public <T> void rxUploadFile(HttpRequestParams params, TypeToken typeToken, ResultConverter<T> converter, boolean needString, AbstractRxObserver<RxUploadDownloadResult<T>> observer) {
        Observable<RxUploadDownloadResult<T>> resultObservable = rxUploadFile(params, typeToken, converter, needString);
        resultObservable.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(observer.getLifecycleOwner())))
                .subscribeWith(observer);
    }

    /**
     * rxjava方式下载文件,异步
     *
     * @param params 请求参数
     */
    @Override
    public Observable<RxUploadDownloadResult<File>> rxDownload(HttpRequestParams params) {
        checkUrl(params.getUrl(), baseUrl);
        if (okDownload.getTask(params.getTag()) != null) {
            DownloadTask existsTask = okDownload.getTask(params.getTag());
            existsTask.remove(true);
            okDownload.removeTask(params.getTag());
        }
        Request request = getRequestByParam(params.getUrl(), HttpMethodEnum.GET, params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), true, params.getTimeout());
        request = request.cacheMode(CacheMode.NO_CACHE);
        request.converter(new FileConvert());
        DownloadTask downloadTask = OkDownload.request(params.getTag(), request);

        String tempFolder = DownloadUtil.getTempCacheFolder(application);
        String tempFileName = DownloadUtil.getTempCacheFileName();
        //下载到临时文件夹中
        downloadTask.folder(tempFolder);
        downloadTask.fileName(tempFileName);

        Observable<RxUploadDownloadResult<File>> observable = Observable.create(new ObservableOnSubscribe<RxUploadDownloadResult<File>>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<RxUploadDownloadResult<File>> emitter) throws Exception {
                downloadTask.register(new DownloadListener(params.getTag()) {
                    @Override
                    public void onStart(Progress progress) {
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        Log.d("rxdownload", "threadL " + Thread.currentThread().getName() + " onProgress: " + progress.fraction);
                        RxUploadDownloadResult uploadDownloadResult = new RxUploadDownloadResult((int) (progress.fraction * 100), progress.currentSize, progress.totalSize, null, params.getTag(), RxUploadDownloadResult.UPLOAD_DOWNLOAD_STATUS_PROCESSING);
                        emitter.onNext(uploadDownloadResult);
                    }

                    @Override
                    public void onError(Progress progress) {
                        emitter.onError(progress.exception);
                        removeHttpClient(progress.tag);
                        downloadTask.remove(true);
                        okDownload.removeTask(progress.tag);
                    }

                    @Override
                    public void onFinish(File file, Progress progress) {
                        RxUploadDownloadResult uploadDownloadResult = null;
                        try {
                            String folder = "";
                            String fileName = "";
                            if (!TextUtils.isEmpty(params.getFileDownloadPath())) {
                                int lastIndex = params.getFileDownloadPath().lastIndexOf("/");
                                if (lastIndex > 0) {
                                    folder = params.getFileDownloadPath().substring(0, lastIndex);
                                    fileName = params.getFileDownloadPath().substring(lastIndex + 1);
                                }
                            }
                            //移动文件
                            boolean moveSuccess = false;
                            if (!TextUtils.isEmpty(folder) && !TextUtils.isEmpty(fileName)) {
                                moveSuccess = FileUtils.moveFile(file, new File(params.getFileDownloadPath()));
                            }
                            if (!moveSuccess) {
                                uploadDownloadResult = new RxUploadDownloadResult(100, progress.currentSize, progress.totalSize, file, params.getTag(), RxUploadDownloadResult.UPLOAD_DOWNLOAD_STATUS_FINISHED);
                                downloadTask.remove();
                            } else {
                                uploadDownloadResult = new RxUploadDownloadResult(100, progress.currentSize, progress.totalSize, new File(params.getFileDownloadPath()), params.getTag(), RxUploadDownloadResult.UPLOAD_DOWNLOAD_STATUS_FINISHED);
                                downloadTask.remove(true);
                            }
                            emitter.onNext(uploadDownloadResult);
                        } catch (Throwable e) {
                            emitter.onError(e);
                        }
                        removeHttpClient(progress.tag);
                        okUpload.removeTask(progress.tag);
                    }

                    @Override
                    public void onRemove(Progress progress) {
                        emitter.onNext(new RxUploadDownloadResult((int) (progress.fraction * 100), progress.currentSize, progress.totalSize, null, params.getTag(), RxUploadDownloadResult.UPLOAD_DOWNLOAD_STATUS_CANCELED));
                        removeHttpClient(progress.tag);
                    }
                }).save().start();
            }
        });
        return observable;
    }

    /**
     * rxjava方式下载文件,异步
     *
     * @param params 请求参数
     */
    @Override
    public void rxDownload(HttpRequestParams params, AbstractRxObserver<RxUploadDownloadResult<File>> observer) {
        Observable<RxUploadDownloadResult<File>> resultObservable = rxDownload(params);
        resultObservable.as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(observer.getLifecycleOwner())))
                .subscribeWith(observer);
    }

    /**
     * 同步下载文件
     *
     * @param params                       http参数
     * @param downloadFolder               下载目标文件夹
     * @param downloadFileName             下载文件名称
     * @param fileDownloadProgressListener 文件下载进度监听器
     */
    @Override
    public void downloadSync(HttpRequestParams params, String downloadFolder, String downloadFileName, FileDownloadProgressListener fileDownloadProgressListener) throws Exception {
        checkUrl(params.getUrl(), baseUrl);
        prepareHeader(params);
        Request request = getRequestByParam(params.getUrl(), params.getHttpMethod(), params.getHeader(), params.getFormData(), params.getRequestBody(), params.isJson(), params.getTag(), false, params.getTimeout());
        okhttp3.Response response = request.execute();
        OkGoFileConvert fileConvert = new OkGoFileConvert(downloadFolder, downloadFileName, fileDownloadProgressListener);
        File appModuleZipFile = fileConvert.convert(response.body().byteStream(), response.body().contentLength());
        removeHttpClient(params.getTag());
        response.close();
    }
}
