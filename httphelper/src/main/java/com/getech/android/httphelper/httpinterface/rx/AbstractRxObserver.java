package com.getech.android.httphelper.httpinterface.rx;

import com.getech.android.httphelper.BuildConfig;
import com.getech.android.httphelper.exception.ApiException;
import com.getech.android.httphelper.exception.ExceptionHandler;
import com.getech.android.httphelper.httpinterface.HttpErrorInfo;
import com.getech.android.httphelper.httpinterface.HttpErrorProcessor;

import io.reactivex.observers.ResourceObserver;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class AbstractRxObserver<T> extends ResourceObserver<T> {
    private ShowloadingLifecycleOwner lifecycleOwner;
    private boolean mShowLoading;
    private boolean mShowErrorToast;
    private HttpErrorProcessor errorProcessor;

    public AbstractRxObserver(ShowloadingLifecycleOwner lifecycleOwner, HttpErrorProcessor errorProcessor) {
        this(lifecycleOwner, errorProcessor, true);
    }

    public AbstractRxObserver(ShowloadingLifecycleOwner lifecycleOwner, HttpErrorProcessor errorProcessor, boolean showLoading) {
        this.lifecycleOwner = lifecycleOwner;
        this.mShowLoading = showLoading;
        this.mShowErrorToast = true;
        this.errorProcessor = errorProcessor;
    }

    public AbstractRxObserver(ShowloadingLifecycleOwner lifecycleOwner, HttpErrorProcessor errorProcessor, boolean showLoading, boolean showErrorToast) {
        this.lifecycleOwner = lifecycleOwner;
        this.mShowLoading = showLoading;
        this.mShowErrorToast = showErrorToast;
        this.errorProcessor = errorProcessor;
    }

    public ShowloadingLifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != lifecycleOwner && mShowLoading) {
            lifecycleOwner.showLoading();
        }
    }

    @Override
    public void onComplete() {
        if (null != lifecycleOwner) {
            lifecycleOwner.hideLoading();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (null != lifecycleOwner) {
            lifecycleOwner.hideLoading();
        }
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
        ApiException exception = null;
        if (e instanceof ApiException) {
            exception = (ApiException) e;
        } else {
            exception = ExceptionHandler.handleException(e, "");
        }
        HttpErrorInfo httpErrorInfo = new HttpErrorInfo(exception.getHttpCode(), exception);
        errorProcessor.processHttpError(exception.getTag(), httpErrorInfo);
        onHttpError(e);
    }

    @Override
    public void onNext(T t) {
        onReceiveHttpResponse(t);
    }

    public abstract void onReceiveHttpResponse(T t);

    public abstract void onHttpError(Throwable e);

}
