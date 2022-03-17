package com.casstime.base.util;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;

/**
 * author: Yangxusong
 * created on: 2019/9/3 0003
 */
public class EmptyUtil {
    private EmptyUtil() {
    }

    public static boolean isStringEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    public static boolean isStringNotEmpty(String str) {
        return !isStringEmpty(str);
    }

    public static <T,U> boolean isMapEmpty(Map<T, U> map) {
        return null == map || map.isEmpty();
    }

    public static <T,U> boolean isMapNotEmpty(Map<T, U> map) {
        return !isMapEmpty(map);
    }


    public static <T> boolean isCollectionEmpty(Collection<T> collection) {
        return null == collection || collection.isEmpty();
    }

    public static <T> boolean isCollectionNotEmpty(Collection<T> collection) {
        return !isCollectionEmpty(collection);
    }

}
