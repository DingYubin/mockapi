package com.getech.android.httphelper.impl;

import com.getech.android.httphelper.httpinterface.AbstractHttpRequestListener;
import com.getech.android.httphelper.httpinterface.HttpErrorInfo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class CustomStringCallback extends StringCallback {
    private AbstractHttpRequestListener listener;
    private String tag;

    public CustomStringCallback(AbstractHttpRequestListener listener, String tag) {
        this.listener = listener;
        this.tag = tag;
    }

    @Override
    public void onSuccess(Response<String> response) {
        if (listener != null) {
            try {
                listener.onSuccess(tag, response.body());
            } catch (Throwable e) {
                listener.onError(tag, new HttpErrorInfo(HttpErrorInfo.ERROR_CODE_PROCESS_RESPONSE, e));
            }
        }
        removeHttpClient(tag);
    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        if (listener != null) {
            listener.onStart(tag);
        }
    }

    @Override
    public void onError(Response<String> response) {
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
