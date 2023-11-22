package com.getech.android.httphelper.impl;

import com.getech.android.httphelper.httpinterface.BaseHttpResult;
import com.getech.android.httphelper.httpinterface.ResultConverter;
import com.getech.android.httphelper.util.GsonConvertUtil;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BaseHttpResultConverter<T> implements ResultConverter<T> {

    private Type type;
    private Class<T> clazz;

    public BaseHttpResultConverter(Type type) {
        this.type = type;
    }

    public BaseHttpResultConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象，生成onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(String responseStr) throws Exception {
        if (type == null) {
            if (clazz == null) {
                // 如果没有通过构造函数传进来，就自动解析父类泛型的真实类型（有局限性，继承后就无法解析到）
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                return parseClass(responseStr, clazz);
            }
        }

        if (type instanceof ParameterizedType) {
            return parseParameterizedType(responseStr, (ParameterizedType) type);
        } else if (type instanceof Class) {
            return parseClass(responseStr, (Class<T>) type);
        } else {
            return parseType(responseStr, type);
        }
    }

    private T parseClass(String responseStr, Class<T> rawType) throws Exception {
        if (rawType == null) {
            return null;
        }
        if (responseStr == null) {
            return null;
        }
        if (rawType == String.class) {
            //noinspection unchecked
            return (T) responseStr;
        } else if (rawType == JSONObject.class) {
            //noinspection unchecked
            return (T) new JSONObject(responseStr);
        } else if (rawType == JSONArray.class) {
            //noinspection unchecked
            return (T) new JSONArray(responseStr);
        } else {
            T t = fromJson(responseStr, rawType);
            return t;
        }
    }

    private T parseType(String responseStr, Type type) throws Exception {
        if (type == null) {
            return null;
        }
        if (responseStr == null) {
            return null;
        }
        // 泛型格式如下： new JsonCallback<任意JavaBean>(this)
        T t = fromJson(responseStr, type);
        return t;
    }

    private T parseParameterizedType(String responseStr, ParameterizedType type) throws Exception {
        if (type == null) {
            return null;
        }
        if (responseStr == null) {
            return null;
        }

        Type rawType = type.getRawType();
        Type typeArgument = type.getActualTypeArguments()[0];
        if (rawType != BaseHttpResult.class) {
            // 泛型格式如下： new JsonCallback<外层BaseBean<内层JavaBean>>(this)
            T t = fromJson(responseStr, type);
            return t;
        } else {
            BaseHttpResult lzyResponse = fromJson(responseStr, type);
            return (T) lzyResponse;
        }
    }

    public static <T> T fromJson(String json, Class<T> type) throws JsonIOException, JsonSyntaxException {
        return GsonConvertUtil.getGson().fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        return GsonConvertUtil.getGson().fromJson(json, type);
    }
}
