package com.yubin.mvx.rx;

import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *     @author : xiaoqing
 *     e-mail : qing.xiao@getech.cn
 *     time   : 2017/08/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface ISubScription {

    void addSubscription(Disposable disposable);

    void removeSubscription();
}
