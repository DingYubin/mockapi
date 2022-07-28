package com.yubin.rxlibrary.interfaces.list2x;

import java.util.Map;

/**
 * 传入数据List,分组大小，返回处理之后的数据Map
 * @param <I>
 * @param <K>
 * @param <V>
 */
public interface Object2Map<I, K, V> {
    Map<K, V> call(I i);
}