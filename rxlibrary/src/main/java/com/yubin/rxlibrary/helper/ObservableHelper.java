package com.yubin.rxlibrary.helper;


import com.yubin.rxlibrary.interfaces.list2x.Function0;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/07/29
 *     desc    : 线程池
 *     version : 1.0
 * </pre>
 */
public class ObservableHelper {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(2 * CPU_COUNT + 1, 2 * CPU_COUNT + 1, 5, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(128));

    private static final int threadNum = Runtime.getRuntime().availableProcessors() + 1;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

    private ObservableHelper(){}

    private static class ClassHolder{
        private static final ObservableHelper observableHelper = new ObservableHelper();
    }

    public static ObservableHelper INSTANCE(){
        return ClassHolder.observableHelper;
    }

    public <T> Observable<T> createObservable(final Function0<T> func) {

        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(func.call());
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.from(executorService)).cache();
    }

    public <T, R> Observable<R> createObservable(final Function<T, R> func, T t) {

        return Observable.create(new ObservableOnSubscribe<R>() {
            @Override
            public void subscribe(ObservableEmitter<R> emitter) throws Exception {
                try {
                    emitter.onNext(func.apply(t));
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.from(executor)).cache();
    }
}
