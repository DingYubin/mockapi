package com.getech.android.httphelper.impl;


import com.getech.android.httphelper.httpinterface.BaseHttpResult;
import com.getech.android.httphelper.util.GsonConvertUtil;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.convert.Converter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OKGoResultConverter<T> implements Converter<T> {

    private Type type;
    private Class<T> clazz;

    public OKGoResultConverter() {
    }

    public OKGoResultConverter(Type type) {
        this.type = type;
    }

    public OKGoResultConverter(Class<T> clazz) {
        this.clazz = clazz;
    }


    @Override
    public T convertResponse(Response response) throws Throwable {
        if (type == null) {
            if (clazz == null) {
                // 如果没有通过构造函数传进来，就自动解析父类泛型的真实类型（有局限性，继承后就无法解析到）
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                return parseClass(response, clazz);
            }
        }

        if (type instanceof ParameterizedType) {
            return parseParameterizedType(response, (ParameterizedType) type);
        } else if (type instanceof Class) {
            return parseClass(response, (Class<?>) type);
        } else {
            return parseType(response, type);
        }
    }

    private T parseClass(Response response, Class<?> rawType) throws Exception {
        if (rawType == null) {
            return null;
        }
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        JsonReader jsonReader = new JsonReader(body.charStream());

        if (rawType == String.class) {
            //noinspection unchecked
            return (T) body.string();
        } else if (rawType == JSONObject.class) {
            //noinspection unchecked
            return (T) new JSONObject(body.string());
        } else if (rawType == JSONArray.class) {
            //noinspection unchecked
            return (T) new JSONArray(body.string());
        } else {
            T t = GsonConvertUtil.getGson().fromJson(jsonReader, rawType);
            response.close();
            return t;
        }
    }

    private T parseType(Response response, Type type) throws Exception {
        if (type == null) {
            return null;
        }
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        JsonReader jsonReader = new JsonReader(body.charStream());
        // 泛型格式如下： new JsonCallback<任意JavaBean>(this)
        T t = GsonConvertUtil.getGson().fromJson(jsonReader, type);
        response.close();
        return t;
    }

    private T parseParameterizedType(Response response, ParameterizedType type) throws Exception {
        if (type == null) {
            return null;
        }
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        JsonReader jsonReader = new JsonReader(body.charStream());
        // 泛型的实际类型
        Type rawType = type.getRawType();
        // 泛型的参数
        Type typeArgument = type.getActualTypeArguments()[0];
        if (rawType != BaseHttpResult.class) {
            // 泛型格式如下： new JsonCallback<外层BaseBean<内层JavaBean>>(this)
            T t = GsonConvertUtil.getGson().fromJson(jsonReader, type);
            response.close();
            return t;
        } else {
            BaseHttpResult httpResult = null;
            if (typeArgument == Boolean.class) {
                // 泛型格式如下： new JsonCallback<HttpResult<Void>>(this)
                SimpleResponse simpleResponse = GsonConvertUtil.getGson().fromJson(jsonReader, SimpleResponse.class);
                //noinspection unchecked
                httpResult = simpleResponse.toHttpResult();
            } else {
                // 泛型格式如下： new JsonCallback<HttpResult<内层JavaBean>>(this)
                httpResult = GsonConvertUtil.getGson().fromJson(jsonReader, type);
            }
            response.close();
            return (T) httpResult;
        }
    }
}
