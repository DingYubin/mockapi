package com.getech.android.httphelper.util;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * des：json 解析适配器 Double
 * Author ChenQ on 2017-10-12
 * email：wxchenq@getech.cn
 */
public class DoubleTypeAdapter implements JsonDeserializer<Double> {

    @Override
    public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (TextUtils.isEmpty(json.getAsString())) {
            return 0.0;
        }
        return Double.valueOf(json.getAsString());
    }
}
