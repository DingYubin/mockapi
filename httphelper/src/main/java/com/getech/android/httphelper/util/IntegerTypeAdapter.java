package com.getech.android.httphelper.util;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


public class IntegerTypeAdapter implements JsonDeserializer<Integer> {


    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (TextUtils.isEmpty(json.getAsString())) {
            return 0;
        }
        Double dValue = Double.valueOf(json.getAsString());
        return dValue.intValue();
    }
}
