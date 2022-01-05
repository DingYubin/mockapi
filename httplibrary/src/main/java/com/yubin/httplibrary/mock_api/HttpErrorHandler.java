package com.yubin.httplibrary.mock_api;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 *
 * @author WenChang Mai
 * @date 2019/1/19 17:15
 * Description:
 */
public class HttpErrorHandler {

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    public static ErrorResponseBody handle(Throwable throwable) {
        if (throwable instanceof CTNoNetworkException) {
            String noNetworkMessage = "网络不可用，请检查网络设置";
            String errorCode = "NETWORK_UNAVAILABLE";
            return new ErrorResponseBody(-1,
                    noNetworkMessage, noNetworkMessage,
                    errorCode, errorCode, noNetworkMessage);
        } else if (throwable instanceof HttpException) {
            // 非200
            HttpException error = (HttpException) throwable;
            try {
                Response<?> response = error.response();
                if (response == null) {
                    return null;
                }
                ResponseBody body = response.errorBody();
                if (body != null) {
                    String responseBody = getBodyString(body);
                    return new Gson().fromJson(responseBody, ErrorResponseBody.class);
                }
            } catch (Exception e) {
//                LogUtil.e(e.getMessage());
            }
        } else { //其他异常
            return new ErrorResponseBody(-1,
                    throwable.getMessage(), throwable.getMessage(),
                    "", "", throwable.getMessage());
        }
        return null;
    }

    /**
     * 获取ResponseBody 字符串
     *
     * @param responseBody ResponseBody
     * @return String
     * @throws IOException
     */
    private static String getBodyString(ResponseBody responseBody) throws IOException {
        if (responseBody == null) {
            return "";
        }
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE);
        // Buffer the entire body.
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        if (charset == null) {
            charset = UTF8;
        }

        return buffer.clone().readString(charset);

    }
}
