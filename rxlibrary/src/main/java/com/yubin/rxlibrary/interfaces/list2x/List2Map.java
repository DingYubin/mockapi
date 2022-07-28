package com.yubin.rxlibrary.interfaces.list2x;

import java.util.List;
import java.util.Map;

/**
 * 传入数据List,返回处理之后的数据Map
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/07/25
 *     desc    : 会话
 *     version : 1.0
 * </pre>
 */
public interface List2Map<I, K, V>{
    Map<K, V> call(List<I> list);
}
