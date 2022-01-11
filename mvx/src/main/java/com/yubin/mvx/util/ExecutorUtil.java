package com.yubin.mvx.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * <pre>
 *     time    : 2018/1/8
 *     desc    : 线程池工具类
 *     version : 1.0
 * </pre>
 */
public class ExecutorUtil {
    private static Map<SingleExecutorType, Executor> singleExecutors = new ConcurrentHashMap<>(2);

    private ExecutorUtil() {
        //no instance
    }

    public static Executor getSingleScheduler(SingleExecutorType type) {
        Executor executor = singleExecutors.get(type);
        if (null == executor) {
            executor = Executors.newSingleThreadExecutor();
            singleExecutors.put(type, executor);
        }
        return executor;
    }

    public enum SingleExecutorType {
        CHAT,
        DB
    }
}
