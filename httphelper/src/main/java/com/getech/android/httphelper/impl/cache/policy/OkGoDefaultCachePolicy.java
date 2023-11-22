package com.getech.android.httphelper.impl.cache.policy;

import android.graphics.Bitmap;

import com.getech.android.httphelper.impl.exception.HttpStatusException;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cache.policy.DefaultCachePolicy;
import com.lzy.okgo.db.CacheManager;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.lzy.okgo.utils.HeaderParser;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Headers;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OkGoDefaultCachePolicy<T> extends DefaultCachePolicy<T> {
    public OkGoDefaultCachePolicy(Request<T, ? extends Request> request) {
        super(request);
    }

    @Override
    protected Response<T> requestNetworkSync() {
        try {
            okhttp3.Response response = rawCall.execute();
            int responseCode = response.code();

            //network error
            if (responseCode >= 400) {
                HttpStatusException statusException = new HttpStatusException("network error! http response code >= 400", responseCode);
                return Response.error(false, rawCall, response, statusException);
            }

            T body = request.getConverter().convertResponse(response);
            //save cache when request is successful
            saveCache(response.headers(), body);
            return Response.success(false, body, rawCall, response);
        } catch (Throwable throwable) {
            if (throwable instanceof SocketTimeoutException && currentRetryCount < request.getRetryCount()) {
                currentRetryCount++;
                rawCall = request.getRawCall();
                if (canceled) {
                    rawCall.cancel();
                } else {
                    requestNetworkSync();
                }
            }
            return Response.error(false, rawCall, null, throwable);
        }
    }

    @Override
    protected void requestNetworkAsync() {
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (e instanceof SocketTimeoutException && currentRetryCount < request.getRetryCount()) {
                    //retry when timeout
                    currentRetryCount++;
                    rawCall = request.getRawCall();
                    if (canceled) {
                        rawCall.cancel();
                    } else {
                        rawCall.enqueue(this);
                    }
                } else {
                    if (!call.isCanceled()) {
                        Response<T> error = Response.error(false, call, null, e);
                        onError(error);
                    }
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                int responseCode = response.code();

                //network error
                if (responseCode >= 400) {
                    HttpStatusException statusException = new HttpStatusException("network error! http response code >= 400", responseCode);
                    Response<T> error = Response.error(false, call, response,statusException);
                    onError(error);
                    return;
                }

                if (onAnalysisResponse(call, response)) {
                    return;
                }

                try {
                    T body = request.getConverter().convertResponse(response);
                    //save cache when request is successful
                    saveCache(response.headers(), body);
                    Response<T> success = Response.success(false, body, call, response);
                    onSuccess(success);
                } catch (Throwable throwable) {
                    Response<T> error = Response.error(false, call, response, throwable);
                    onError(error);
                }
            }
        });
    }

    /**
     * 请求成功后根据缓存模式，更新缓存数据
     *
     * @param headers 响应头
     * @param data    响应数据
     */
    private void saveCache(Headers headers, T data) {
        //不需要缓存,直接返回
        if (request.getCacheMode() == CacheMode.NO_CACHE) {
            return;
        }
        //Bitmap没有实现Serializable,不能缓存
        if (data instanceof Bitmap) {
            return;
        }

        CacheEntity<T> cache = HeaderParser.createCacheEntity(headers, data, request.getCacheMode(), request.getCacheKey());
        if (cache == null) {
            //服务器不需要缓存，移除本地缓存
            CacheManager.getInstance().remove(request.getCacheKey());
        } else {
            //缓存命中，更新缓存
            CacheManager.getInstance().replace(request.getCacheKey(), cache);
        }
    }
}
