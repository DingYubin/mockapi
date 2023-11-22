package com.getech.android.httphelper.util;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


public class FloatTypeAdapter implements JsonDeserializer<Float> {

    @Override
    public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (TextUtils.isEmpty(json.getAsString())) {
            return 0F;
        }
        return Float.valueOf(json.getAsString());
    }
}
