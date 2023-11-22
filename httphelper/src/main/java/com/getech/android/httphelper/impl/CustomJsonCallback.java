package com.getech.android.httphelper.impl;

import com.getech.android.httphelper.httpinterface.AbstractHttpRequestListener;
import com.getech.android.httphelper.httpinterface.HttpErrorInfo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class CustomJsonCallback<T> extends AbsCallback<T> {
    private AbstractHttpRequestListener listener;
    private String tag;
    private Type type;
    private Class<T> clazz;

    public CustomJsonCallback(AbstractHttpRequestListener listener, String tag, Type type) {
        this.listener = listener;
        this.tag = tag;
        this.type = type;
    }

    public CustomJsonCallback(AbstractHttpRequestListener listener, String tag, Class<T> clazz) {
        this.listener = listener;
        this.tag = tag;
        this.clazz = clazz;
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(okhttp3.Response response) throws Throwable {
        if (listener != null && listener.getConverter() != null) {
            return (T) listener.getConverter().convertResponse(response.body().string());
        }
        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                OKGoResultConverter<T> convert = new OKGoResultConverter<>(clazz);
                return convert.convertResponse(response);
            }
        }

        OKGoResultConverter<T> convert = new OKGoResultConverter<>(type);
        return convert.convertResponse(response);
    }

    @Override
    public void onSuccess(Response<T> response) {
        if (listener != null) {
            try {
                listener.onReceiveResult(tag, response.body());
            } catch (Throwable e) {
                listener.onError(tag, new HttpErrorInfo(HttpErrorInfo.ERROR_CODE_PROCESS_RESPONSE, e));
            }
        }
        removeHttpClient(tag);
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        if (listener != null) {
            listener.onStart(tag);
        }
    }

    @Override
    public void onError(Response<T> response) {
        if (listener != null) {
            HttpErrorInfo errorInfo = new HttpErrorInfo(response.code(), response.getException());
            listener.onError(tag, errorInfo);
        }
        removeHttpClient(tag);
    }

    @Override
    public void onFinish() {
        if (listener != null) {
            listener.onFinish(tag);
        }
        removeHttpClient(tag);
    }

    @Override
    public void uploadProgress(Progress progress) {
        if (listener != null) {
            listener.onProgress(tag, (int) (progress.fraction * 100), progress.currentSize, progress.totalSize);
        }
    }

    @Override
    public void downloadProgress(Progress progress) {
        if (listener != null) {
            listener.onProgress(tag, (int) (progress.fraction * 100), progress.currentSize, progress.totalSize);
        }
    }

    public abstract void removeHttpClient(String tag);
}
