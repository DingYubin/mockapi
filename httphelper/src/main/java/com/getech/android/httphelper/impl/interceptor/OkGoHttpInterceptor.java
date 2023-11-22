package com.getech.android.httphelper.impl.interceptor;

import com.getech.android.httphelper.httpinterface.interceptor.HttpInterceptor;
import com.getech.android.httphelper.httpinterface.interceptor.HttpRequestInfo;
import com.getech.android.httphelper.httpinterface.interceptor.HttpResponseInfo;
import com.lzy.okgo.utils.IOUtils;
import com.lzy.okgo.utils.OkLogger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpMethod;
import okio.Buffer;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OkGoHttpInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    /**
     * 拦截
     */
    HttpInterceptor httpInterceptor;

    public OkGoHttpInterceptor(HttpInterceptor httpInterceptor) {
        this.httpInterceptor = httpInterceptor;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //更新请求信息
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        String requestUrl = httpUrl.url().toString();
        HttpUrl.Builder urlBuilder = request.url().newBuilder();
        Request.Builder builder = request.newBuilder().url(urlBuilder.build());
        HttpRequestInfo originHttpRequestInfo = getHttpRequestInfoFromChain(chain);
        originHttpRequestInfo.initChanged();
        HttpRequestInfo changedHttpRequestInfo = httpInterceptor.interceptRequestInfo(originHttpRequestInfo);
        if (changedHttpRequestInfo != null && !changedHttpRequestInfo.equals(originHttpRequestInfo)) {
            //formData
            if (changedHttpRequestInfo.getNeedRemoveFormDatakeys() != null) {
                for (String key : changedHttpRequestInfo.getNeedRemoveFormDatakeys()) {
                    urlBuilder.removeAllQueryParameters(key);
                }
            }
            if (changedHttpRequestInfo.getFormData() != null) {
                for (Map.Entry<String, String> entry : changedHttpRequestInfo.getFormData().entrySet()) {
                    urlBuilder.setQueryParameter(entry.getKey(), entry.getValue());
                }
            }
            //header
            if (changedHttpRequestInfo.getNeedRemoveHeaders() != null) {
                for (String key : changedHttpRequestInfo.getNeedRemoveHeaders()) {
                    builder.removeHeader(key);
                }
            }
            if (changedHttpRequestInfo.getHeaders() != null) {
                for (Map.Entry<String, String> entry : changedHttpRequestInfo.getHeaders().entrySet()) {
                    builder.header(entry.getKey(), entry.getValue());
                }
            }
            //请求的body
            if (changedHttpRequestInfo.isCanChangeBody()) {
                RequestBody requestBody = RequestBody.create(request.body().contentType(), changedHttpRequestInfo.getRequestBody());
                builder.method(request.method(), requestBody);
            }
        }
        Response response;
        HttpResponseInfo originResponseInfo;
        try {
            response = chain.proceed(builder.build());
        } catch (Throwable e) {
            originResponseInfo = new HttpResponseInfo(e);
            httpInterceptor.interceptResponseInfo(originResponseInfo);
            throw e;
        }
        //处理response
        String responseBodyStr = "";
        long contentLength = 0;
        String mediaTypeStr = "";
        ResponseBody responseBody = response.body();
        if (responseBody == null || !isPlaintext(responseBody.contentType())) {
            httpInterceptor.interceptResponseInfo(new HttpResponseInfo(false));
            return response;
        }
        MediaType mediaType = responseBody.contentType();
        HashMap<String, String> responseHeaders = new HashMap<>();
        if (responseBody != null) {
            byte[] bytes = IOUtils.toByteArray(responseBody.byteStream());
            MediaType contentType = responseBody.contentType();
            responseBodyStr = new String(bytes, getCharset(contentType));
            contentLength = responseBodyStr.length();
            mediaTypeStr = mediaType.toString();
            Headers headers = response.headers();
            for (int i = 0; i < headers.size(); i++) {
                responseHeaders.put(headers.name(i), headers.value(i));
            }
        }
        originResponseInfo = new HttpResponseInfo(response.code(), responseBodyStr, responseHeaders, contentLength, mediaTypeStr);
        originResponseInfo.initChanged();
        //拦截器修改response
        HttpResponseInfo changedResponseInfo = httpInterceptor.interceptResponseInfo(originResponseInfo);
        if (changedResponseInfo != null && !changedResponseInfo.equals(originResponseInfo)) {
            Response.Builder responseBuilder = response.newBuilder();
            ResponseBody newBody = ResponseBody.create(mediaType, changedResponseInfo.getResponseBody());
            //修改response body
            responseBuilder.body(newBody);
            //修改状态码
            responseBuilder.code(changedResponseInfo.getStatusCode());
            return responseBuilder.build();
        }
        return response;
    }

    private HttpRequestInfo getHttpRequestInfoFromChain(Chain chain) {
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        String requestUrl = httpUrl.url().toString();
        HttpRequestInfo info = new HttpRequestInfo(requestUrl, request.method());
        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            String value = headers.value(i);
            if (value != null) {
                info.addHeader(name, value);
            }
        }
        RequestBody requestBody = request.body();
        if (requestBody == null) {
            info.setRequestBody("");
            info.setCanChangeBody(false);
        } else {
            boolean isPlain = isPlaintext(requestBody.contentType()) && HttpMethod.requiresRequestBody(request.method());
            info.setCanChangeBody(isPlain);
            if (isPlain) {
                String requestBodyStr = bodyToString(request);
                info.setRequestBody(requestBodyStr);
            }
        }
        return info;
    }


    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) {
                return true;
            }
        }
        return false;
    }

    private String bodyToString(Request request) {
        String bodyStr = "";
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) {
                return "";
            }
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
            bodyStr = buffer.readString(charset);
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        }
        return bodyStr;
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) {
            charset = UTF8;
        }
        return charset;
    }
}
