package com.yubin.mvx.rx;


import static com.yubin.mvx.util.ExecutorUtil.getSingleScheduler;

import android.os.Build;

import com.yubin.mvx.util.ExecutorUtil;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *     @author : xiaoqing
 *     e-mail : qing.xiao@getech.cn
 *     time   : 2017/08/01
 *     desc   : RXJava工具类
 *     version: 1.0
 * </pre>
 */

public class RxUtil {
    private static final int THREAD_COUNT = 5;
    public static final Executor netWorkExecutor = Executors.newFixedThreadPool(THREAD_COUNT);

    private RxUtil() {
    }

    public static <T> FlowableTransformer<T, T> doInBackground() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> doInNetwork() {
        return observable -> observable
                .subscribeOn(Schedulers.from(netWorkExecutor))
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static <T> SingleTransformer<T, T> singleDoInBackground() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> singleDoInNetwork() {
        return observable -> observable
                .subscribeOn(Schedulers.from(netWorkExecutor))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> MaybeTransformer<T, T> maybeDoInBackground() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> MaybeTransformer<T, T> maybeDoInNetwork() {
        return observable -> observable
                .subscribeOn(Schedulers.from(netWorkExecutor))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> allInBackground() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> FlowableTransformer<T, T> allInNetwork() {
        return observable -> observable
                .subscribeOn(Schedulers.from(netWorkExecutor))
                .observeOn(Schedulers.from(netWorkExecutor));
    }

    public static <T> SingleTransformer<T, T> singleAllInBackground() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> SingleTransformer<T, T> singleAllInNetwork() {
        return observable -> observable
                .subscribeOn(Schedulers.from(netWorkExecutor))
                .observeOn(Schedulers.from(netWorkExecutor));
    }

    public static <T> MaybeTransformer<T, T> maybeAllInBackground() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    public static <T> MaybeTransformer<T, T> maybeAllInNetowrk() {
        return observable -> observable
                .subscribeOn(Schedulers.from(netWorkExecutor))
                .observeOn(Schedulers.from(netWorkExecutor));
    }

    public static <T> FlowableTransformer<T, T> doInBackground(ExecutorUtil.SingleExecutorType type) {
        return observable -> observable
                .subscribeOn(Schedulers.from(getSingleScheduler(type)))
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Function<List<T>, Publisher<T>> getFromList() {
        return list -> Flowable.just(list.get(0));
    }

    public static <T> Function<List<T>, Maybe<T>> getFromList4Maybe() {
        return list -> Maybe.just(list.get(0));
    }

    /**
     * 权限判断默认6.0以下都是有权限
     *
     * @return
     */
    public static ObservableTransformer<Boolean, Boolean> filterAndroidVersion() {   //compose判断结果
        return new ObservableTransformer<Boolean, Boolean>() {
            @Override
            public ObservableSource<Boolean> apply(Observable<Boolean> aBoolean) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return Observable.just(true);
                } else {
                    return aBoolean;
                }
            }
        };
    }

    /**
     * 统一线程处理
     */
    public static <T> FlowableTransformer<T, T> rxSchedulerHelper() {    //compose简化线程
        return new FlowableTransformer<T, T>() {
            @Override
            public Flowable<T> apply(Flowable<T> observable) {
                return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 生成Flowable
     */
    public static <T> Flowable<T> createData(final T t) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(t);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, BackpressureStrategy.BUFFER);
    }

    public static Scheduler networkSchedulers() {
        return Schedulers.from(netWorkExecutor);
    }
}