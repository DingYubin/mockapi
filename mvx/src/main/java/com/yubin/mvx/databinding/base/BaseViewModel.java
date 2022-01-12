package com.yubin.mvx.databinding.base;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;

import com.yubin.mvx.databinding.rxutil.ISubScription;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *     @author : dingyubin
 *     time   : 2017/08/03
 *     desc   : ViewModel
 *     version: 1.0
 * </pre>
 */

public class BaseViewModel extends AndroidViewModel implements ISubScription {

    private CompositeDisposable composite;
    public Context context;

    public BaseViewModel(Application application) {
        super(application);
        this.context = application;
    }

    protected void init() {
    }

    @Override
    public void addSubscription(Disposable baseSubscriber) {
        if (null == composite) {
            composite = new CompositeDisposable();
        }
        composite.add(baseSubscriber);
    }

    @Override
    public void removeSubscription() {
        if (this.composite != null) {
            this.composite.clear();
        }
    }

}
