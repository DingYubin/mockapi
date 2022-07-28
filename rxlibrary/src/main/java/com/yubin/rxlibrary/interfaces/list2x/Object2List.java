package com.yubin.rxlibrary.interfaces.list2x;

import java.util.List;

/**
 * 传入数据List,分组大小，返回处理之后的List(不保证顺序)
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/07/25
 *     desc    : 会话
 *     version : 1.0
 * </pre>
 */
public interface Object2List<I, R> {
    List<R> call(I i);
}