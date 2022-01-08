package com.yubin.httplibrary.util;

import android.content.Context;
import android.net.Uri;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.reactivex.annotations.NonNull;

public class ToolUtil {
    /**
     * 从asset中获取内容
     */
    public static String stringFromAssets(@NonNull Context context, String fileName) {
        BufferedReader bufReader = null;
        InputStreamReader inputReader = null;
        try {
            String line;
            inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            bufReader = new BufferedReader(inputReader);
            StringBuilder builder = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (Exception ignored) {
        } finally {
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException ignored) {
                }
            }
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return Uri.EMPTY.toString();
    }
}
