package com.yubin.rxlibrary.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yubin.baselibrary.router.path.RouterPath;
import com.yubin.baselibrary.ui.basemvvm.NativeActivity;
import com.yubin.baselibrary.util.LogUtil;
import com.yubin.httplibrary.mockapi.BaseResponse;
import com.yubin.rxlibrary.bean.UserEntity;
import com.yubin.rxlibrary.databinding.ActivityRxBinding;
import com.yubin.rxlibrary.executor.ObservableExecutor;
import com.yubin.rxlibrary.interfaces.list2x.List2List;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/07/28
 *     desc    : rx测试页面
 *     version : 1.0
 * </pre>
 */

@Route(path = RouterPath.RxPage.PATH_RX_JAVA)
public class RxActivity extends NativeActivity<ActivityRxBinding> {

    @NonNull
    @Override
    public ActivityRxBinding getViewBinding() {
        return ActivityRxBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().hide();
        }

        testRxjava();
    }

    /**
     * rxjava操作符测试
     */
    private void testRxjava() {

        getBinding().flatMap.setOnClickListener(view -> {
            long startTime = System.currentTimeMillis();
            testFlatMap();
            long endTime = System.currentTimeMillis();
            LogUtil.i("thread : " + Thread.currentThread().getName() + ", spent time : " + (endTime - startTime));
        });

        getBinding().concatMap.setOnClickListener(view -> {
            textConcatMap();
        });

        getBinding().collect.setOnClickListener(view -> {
            textCollect();
        });

        getBinding().reduce.setOnClickListener(view -> {
            long startTime = System.currentTimeMillis();
            textReduce();
            long endTime = System.currentTimeMillis();
            LogUtil.i("thread : " + Thread.currentThread().getName() + ", spent time : " + (endTime - startTime));
        });

        getBinding().executor.setOnClickListener(view -> {
            textExecutor();
        });

        getBinding().concurrency.setOnClickListener(view -> {
            long startTime = System.currentTimeMillis();
            textConcurrency();
            long endTime = System.currentTimeMillis();
            LogUtil.i("thread : " + Thread.currentThread().getName() + ", spent time : " + (endTime - startTime));
        });

        getBinding().concurrency0.setOnClickListener(view -> {
            long startTime = System.currentTimeMillis();
            textConcurrency0();
            long endTime = System.currentTimeMillis();
            LogUtil.i("thread : " + Thread.currentThread().getName() + ", spent time : " + (endTime - startTime));
        });

        getBinding().concurrencyExecutor.setOnClickListener(view -> {
            long startTime = System.currentTimeMillis();
            textConcurrencyExecutor();
            long endTime = System.currentTimeMillis();
            LogUtil.i("thread : " + Thread.currentThread().getName() + ", spent time : " + (endTime - startTime));

        });

        getBinding().concurrencyComputation.setOnClickListener(view -> {
            long startTime = System.currentTimeMillis();
            textConcurrencyComputation();
            long endTime = System.currentTimeMillis();
            LogUtil.i("thread : " + Thread.currentThread().getName() + ", spent time : " + (endTime - startTime));

        });
    }

    private void textConcurrency(){

        ObservableExecutor.INSTANCE().executeObservable(getUsers(), 10, new List2List<UserEntity, UserEntity>() {
            @Override
            public List<UserEntity> call(List<UserEntity> users) {
                LogUtil.i( "call thread : " + Thread.currentThread().getName());
                return getUsers(users);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<UserEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<UserEntity> users) {
                        LogUtil.i( "onNext thread : " + Thread.currentThread().getName() + ", userEntities = " + users.toString());

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void textConcurrency0(){

        ObservableExecutor.INSTANCE().executeObservable0(getUsers(), 10, new List2List<UserEntity, UserEntity>() {
                    @Override
                    public List<UserEntity> call(List<UserEntity> users) {
                        LogUtil.i( "call thread : " + Thread.currentThread().getName());
                        return getUsers(users);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<UserEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<UserEntity> users) {
                        LogUtil.i( "onNext thread : " + Thread.currentThread().getName() + ", userEntities = " + users.toString());

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void textConcurrencyComputation(){

        List<UserEntity> users = getUsers();
        List<List<UserEntity>> lists = getLists(users);

        Observable.fromIterable(lists)
                .flatMap((Function<List<UserEntity>, Observable<BaseResponse<List<UserEntity>>>>) quoteResults -> {
//                        LogUtil.i("thread : " + Thread.currentThread().getName());
                    return getTmsObservable(quoteResults)
                            .subscribeOn(Schedulers.io());
                }).collect(new Callable<BaseResponse<List<UserEntity>>>() {
                    @Override
                    public BaseResponse<List<UserEntity>> call() throws Exception {
                        return new BaseResponse(0, "success", users, 200);
                    }
                }, (listBaseResponse, listBaseResponse2) -> {
                    LogUtil.i("collect thread : " + Thread.currentThread().getName());
                }).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse<List<UserEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseResponse<List<UserEntity>> listBaseResponse) {
                        if (listBaseResponse.isSuccessful(0)) {
                            LogUtil.i( "onNext thread : " + Thread.currentThread().getName() + ", baseResponseObservable = " + listBaseResponse.getData().toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void textConcurrencyExecutor(){
        int threadNum = Runtime.getRuntime().availableProcessors() + 1;

        final ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        final Scheduler scheduler = Schedulers.from(executorService);

        List<UserEntity> users = getUsers();
        List<List<UserEntity>> lists = getLists(users);

        Observable.fromIterable(lists)
                .flatMap(new Function<List<UserEntity>, Observable<BaseResponse<List<UserEntity>>>>() {
                    @Override
                    public Observable<BaseResponse<List<UserEntity>>> apply(List<UserEntity> quoteResults) throws Exception {
//                        LogUtil.i("thread : " + Thread.currentThread().getName());
                        return getTmsObservable(quoteResults)
                                .subscribeOn(scheduler).cache();
                    }
                }).collect(new Callable<BaseResponse<List<UserEntity>>>() {
                    @Override
                    public BaseResponse<List<UserEntity>> call() throws Exception {
                        return new BaseResponse(0, "success", users, 200);
                    }
                }, (listBaseResponse, listBaseResponse2) -> {
                    LogUtil.i("thread : " + Thread.currentThread().getName());
                }).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        executorService.shutdown();
                    }
                })
                .subscribe(new Observer<BaseResponse<List<UserEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseResponse<List<UserEntity>> listBaseResponse) {
                        if (listBaseResponse.isSuccessful(0)) {
                            LogUtil.i( "onNext thread : " + Thread.currentThread().getName() + ", baseResponseObservable = " + listBaseResponse.getData().toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void textExecutor(){
        int threadNum = Runtime.getRuntime().availableProcessors() + 1;

        final ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        final Scheduler scheduler = Schedulers.from(executorService);

        Observable.range(1, 100)
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        return Observable.just(integer)
                                .subscribeOn(scheduler)
                                .map(new Function<Integer, String>() {
                                    @Override
                                    public String apply(Integer integer) throws Exception {
                                        LogUtil.i( "Observable thread : " + Thread.currentThread().getName() );
                                        return integer.toString();
                                    }
                                });
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        executorService.shutdown();
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.i( "accept Next thread : " + Thread.currentThread().getName() + ", Next: " + s);

                        LogUtil.i( "Next: " + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        LogUtil.i("Error.");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtil.i("Complete.");
                    }
                });
    }

    private void testFlatMap(){
        List<UserEntity> users = getUsers();
        List<List<UserEntity>> lists = getLists(users);

        Observable.fromIterable(lists)
                .flatMap(new Function<List<UserEntity>, Observable<BaseResponse<List<UserEntity>>>>() {
                    @Override
                    public Observable<BaseResponse<List<UserEntity>>> apply(List<UserEntity> quoteResults) throws Exception {
                        LogUtil.i("thread : " + Thread.currentThread().getName());
                        return getTmsObservable(quoteResults);
                    }
                }).collect(new Callable<BaseResponse<List<UserEntity>>>() {
                    @Override
                    public BaseResponse<List<UserEntity>> call() throws Exception {
                        return new BaseResponse(0, "success", users, 200);
                    }
                }, (listBaseResponse, listBaseResponse2) -> {
                    LogUtil.i("thread : " + Thread.currentThread().getName());
                }).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseResponse<List<UserEntity>> listBaseResponse) {
                        if (listBaseResponse.isSuccessful(0)) {
                            LogUtil.i( "onNext thread : " + Thread.currentThread().getName() + ", baseResponseObservable = " + listBaseResponse.getData().toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void textCollect(){
        List<UserEntity> users = getUsers();
        List<List<UserEntity>> lists = getLists(users);

        Observable.fromIterable(lists).concatMap(new Function<List<UserEntity>, Observable<BaseResponse<List<UserEntity>>>>() {

                    @Override
                    public Observable<BaseResponse<List<UserEntity>>> apply(List<UserEntity> quoteResults) throws Exception {

                        return getTmsObservable(quoteResults);
                    }
                }).collect(new Callable<BaseResponse<List<UserEntity>>>() {
                    @Override
                    public BaseResponse<List<UserEntity>> call() throws Exception {
                        return new BaseResponse(0, "success", users, 200);
                    }
                }, (listBaseResponse, listBaseResponse2) -> {
//                    LogUtil.i("thread : " + Thread.currentThread().getName() + ", spent time : " + (System.currentTimeMillis() - startTime));
                }).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseResponse<List<UserEntity>> listBaseResponse) {
                        if (listBaseResponse.isSuccessful(0)) {
                            LogUtil.i( "onNext thread : " + Thread.currentThread().getName() + ", baseResponseObservable = " + listBaseResponse.getData().toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void textReduce(){
        List<UserEntity> users = getUsers();
        List<UserEntity> result = getUsers();
        List<List<UserEntity>> lists = getLists(users);

        Observable.fromIterable(lists).concatMap(new Function<List<UserEntity>, Observable<BaseResponse<List<UserEntity>>>>() {

                    @Override
                    public Observable<BaseResponse<List<UserEntity>>> apply(List<UserEntity> quoteResults) throws Exception {
                        LogUtil.i("thread : " + Thread.currentThread().getName() + ", quoteResults = " + quoteResults.toString());

                        return getTmsObservable(quoteResults);
                    }
                }).reduce(result, new BiFunction<List<UserEntity>, BaseResponse<List<UserEntity>>, List<UserEntity>>() {
                    @Override
                    public List<UserEntity> apply(List<UserEntity> userEntities, BaseResponse<List<UserEntity>> listBaseResponse) throws Exception {
                        userEntities.addAll(listBaseResponse.getData());
                        return userEntities;
                    }
                })
                .toObservable()
                .map(new Function<List<UserEntity>, BaseResponse<List<UserEntity>>>() {
                    @Override
                    public BaseResponse<List<UserEntity>> apply(List<UserEntity> userEntities) throws Exception {
                        return new BaseResponse<>(0, "success", userEntities, 200);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse<List<UserEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseResponse<List<UserEntity>> baseResponseObservable) {
                        if (baseResponseObservable.isSuccessful(0)) {
                            LogUtil.i("onNext thread : " + Thread.currentThread().getName() + ", baseResponseObservable = " + baseResponseObservable.getData().toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        LogUtil.i("onComplete thread : " + Thread.currentThread().getName());
                    }
                });
    }

    /**
     * 有序合并
     */
    private void textConcatMap() {

        List<UserEntity> users = getUsers();
        List<List<UserEntity>> lists = getLists(users);
        Observable.fromIterable(lists).concatMap((Function<List<UserEntity>, Observable<BaseResponse<List<UserEntity>>>>) this::getTmsObservable)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<>() {
                    @Override
                    public void onNext(BaseResponse<List<UserEntity>> listBaseResponse) {
                        LogUtil.i( "onNext thread : " + Thread.currentThread().getName() + ", baseResponseObservable = " + listBaseResponse.getData().toString());

                    }
                    @Override
                    public void onError(Throwable e) {

                    }
                    @Override
                    public void onComplete() {
                    }
                });

    }

    private List<UserEntity> getUsers(List<UserEntity> quoteResults) {
        return quoteResults.stream().peek(userEntity -> userEntity.setLogin(false)).collect(Collectors.toList());
    }

    private Observable<BaseResponse<List<UserEntity>>> getTmsObservable(List<UserEntity> quoteResults) {
        quoteResults.stream().peek(userEntity -> userEntity.setLogin(false)).collect(Collectors.toList());
        return Observable.just(new BaseResponse<>(0, "success", quoteResults, 200));
    }

    private List<UserEntity> getUsers() {

        List<UserEntity> quoteResults = new ArrayList<>();
        for (int i = 0; i < 51; i ++) {
            quoteResults.add(new UserEntity("accessToken"+i, "xiaohong" + i, true));
        }
        //备用
        quoteResults.add(new UserEntity("独特的一份", "007", true));

        return quoteResults;
    }

    private List<List<UserEntity>> getLists(List<UserEntity> users) {
        List<UserEntity> result = new ArrayList<>();
        for (UserEntity entity : users) {
            if (Objects.equals(entity.getUserLoginId(), "007")) continue;
            result.add(entity);
        }
        List<List<UserEntity>> lists = new ArrayList<>();
        int pageSize = 10;
        int pageNumber = result.size() / pageSize;
        int lastNumber = result.size() % pageSize;
        for (int pageIndex = 0; pageIndex <= pageNumber; pageIndex++) {
            if (pageIndex < pageNumber) {//满数组
//                LogUtil.i( "start = " + pageIndex * pageSize + ", end = " + (pageIndex * pageSize + pageSize));
                lists.add(result.subList(pageIndex * pageSize, pageIndex * pageSize + pageSize));
            } else {
                if (lastNumber > 0) {
//                    LogUtil.i("start = " + pageIndex * pageSize + ", end = " + result.size());
                    lists.add(result.subList(pageIndex * pageSize, result.size()));
                }
            }
        }
        return lists;
    }
}
