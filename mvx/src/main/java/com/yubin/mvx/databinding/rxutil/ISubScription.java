package com.yubin.mvx.databinding.rxutil;

import io.reactivex.disposables.Disposable;

public interface ISubScription {

    void addSubscription(Disposable disposable);

    void removeSubscription();
}
