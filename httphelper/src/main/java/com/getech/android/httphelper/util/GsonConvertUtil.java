package com.getech.android.httphelper.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GsonConvertUtil {
    public static Gson getGson() {
        Gson gson = null;
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Double.class, new DoubleSerializer());
        builder.registerTypeAdapter(Integer.class, new IntegerTypeAdapter());
        builder.registerTypeAdapter(Long.class, new LongTypeAdapter());
        builder.registerTypeAdapter(Double.class, new DoubleTypeAdapter());
        builder.registerTypeAdapter(Float.class, new FloatTypeAdapter());
        builder.registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory<>());
        builder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
        builder.setLenient();
        builder.serializeNulls();
        gson = builder.create();
        return gson;
    }
}
