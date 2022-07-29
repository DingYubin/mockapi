package com.yubin.rxlibrary.executor;

import com.yubin.rxlibrary.helper.ObservableHelper;
import com.yubin.rxlibrary.interfaces.list2x.Function0;
import com.yubin.rxlibrary.interfaces.list2x.List2List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/07/29
 *     desc    : 会话
 *     version : 1.0
 * </pre>
 */
public class ObservableExecutor {

    private final ObservableHelper helper = ObservableHelper.INSTANCE();

    private static class ExecutorHolder {
        private static final ObservableExecutor executor = new ObservableExecutor();
    }

    private ObservableExecutor(){}

    public static ObservableExecutor INSTANCE() {
        return ExecutorHolder.executor;
    }
    

    /**
     * 传入List数据，分组大小，为每一组数据分配一条线程处理，返回List结构数据，自己实现List2List接口
     * @param input
     * @param partitionSize
     * @param func
     * @param <I>
     * @param <R>
     * @return
     */
    public <I, R> Observable<List<R>> executeObservable0(List<I> input, int partitionSize,
                                            final List2List<I, R> func) {
        if(input==null || input.size()==0){
            return Observable.just(Collections.emptyList());
        }

        if (partitionSize <= 0)
            partitionSize = 10;
        List<List<I>> partitions = partition(input, partitionSize);

        return Observable.fromIterable(partitions).concatMap(new Function<List<I>, Observable<List<R>>>() {
            @Override
            public Observable<List<R>> apply(List<I> is) throws Exception {

                return helper.createObservable(new Function0<List<R>>() {
                    @Override
                    public List<R> call() throws Exception {
                        return func.call(is);
                    }
                });
            }
        }).onErrorReturn(new Function<Throwable, List<R>>() {
            @Override
            public List<R> apply(Throwable throwable) throws Exception {
                return Collections.emptyList();
            }
        }).collect(new Callable<List<R>>() {
            @Override
            public List<R> call() throws Exception {
                return new ArrayList<>();
            }
        }, new BiConsumer<List<R>, List<R>>() {
            @Override
            public void accept(List<R> rs, List<R> rs2) throws Exception {
                rs.addAll(rs2);
            }
        }).toObservable();
//                .blockingGet();
    }

    public <T, R> Observable<List<R>> executeObservable(List<T> input, int partitionSize,
                                                        final List2List<T, R> list) {
        if(input==null || input.size()==0){
            return Observable.just(Collections.emptyList());
        }

        if (partitionSize <= 0)
            partitionSize = 10;
        List<List<T>> partitions = partition(input, partitionSize);

        return Observable.fromIterable(partitions).flatMap(new Function<List<T>, Observable<List<R>>>() {
            @Override
            public Observable<List<R>> apply(List<T> is) throws Exception {

                return helper.createObservable(new Function<List<T>, List<R>>() {
                    @Override
                    public List<R> apply(List<T> is) throws Exception {
                        return list.call(is);
                    }
                }, is);
            }
        }).onErrorReturn(new Function<Throwable, List<R>>() {
            @Override
            public List<R> apply(Throwable throwable) throws Exception {
                return Collections.emptyList();
            }
        }).collect(new Callable<List<R>>() {
            @Override
            public List<R> call() throws Exception {
                return new ArrayList<>();
            }
        }, new BiConsumer<List<R>, List<R>>() {
            @Override
            public void accept(List<R> rs, List<R> rs2) throws Exception {
                rs.addAll(rs2);
            }
        }).toObservable();
    }

    /**
     * 分组
     * @param input
     * @param partitionSize
     * @param <I>
     * @return
     */
    private <I> List<List<I>> partition(List<I> input, int partitionSize) {

        List<List<I>> lists = new ArrayList<>();
        int pageNumber = input.size() / partitionSize;
        int lastNumber = input.size() % partitionSize;
        for (int pageIndex = 0; pageIndex <= pageNumber; pageIndex++) {
            if (pageIndex < pageNumber) {//满数组
                lists.add(input.subList(pageIndex * partitionSize, pageIndex * partitionSize + partitionSize));
            } else {
                if (lastNumber > 0) {
                    lists.add(input.subList(pageIndex * partitionSize, input.size()));
                }
            }
        }
        return lists;
    }
}
