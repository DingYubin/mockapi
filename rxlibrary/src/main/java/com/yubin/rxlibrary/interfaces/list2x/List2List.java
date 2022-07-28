package com.yubin.rxlibrary.interfaces.list2x;

import java.util.List;

/**
 * 传入数据List,返回处理之后的List(不保证顺序)
 * @param <I>
 * @param <R>
 */
public interface List2List<I,R> {
    List<R> call(List<I> list);
}

