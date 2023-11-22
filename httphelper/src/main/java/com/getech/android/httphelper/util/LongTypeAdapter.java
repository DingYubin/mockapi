package com.getech.android.httphelper.util;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class LongTypeAdapter implements JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (TextUtils.isEmpty(json.getAsString())) {
            return 0L;
        }
        return Long.valueOf(json.getAsString());
    }
}
