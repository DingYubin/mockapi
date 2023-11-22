package com.getech.android.httphelper.httpinterface.interceptor;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/15
 *     desc   : http请求返回值信息
 *     version: 1.0
 * </pre>
 */
public final class HttpResponseInfo {
    private int statusCode;
    private String responseBody;
    private Throwable e;
    private HashMap<String, String> headers;
    private long contentLength;
    private String mediaType;
    private boolean changed;
    private boolean canChangedResponseBody = true;

    public HttpResponseInfo(Throwable e) {
        this.e = e;
    }

    public HttpResponseInfo(boolean canChange) {
        this.canChangedResponseBody = canChange;
    }

    public HttpResponseInfo(int statusCode, String responseBody, HashMap<String, String> headers, long contentLength, String mediaType) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.headers = headers;
        this.contentLength = contentLength;
        this.mediaType = mediaType;
        changed = true;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        changed = true;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        changed = true;
    }

    public Throwable getE() {
        return e;
    }

    public void setE(Throwable e) {
        this.e = e;
        changed = true;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void initChanged() {
        changed = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HttpResponseInfo) || obj == null) {
            return false;
        }
        if (changed) {
            return false;
        }
        return statusCode == ((HttpResponseInfo) obj).statusCode
                && TextUtils.equals(responseBody, ((HttpResponseInfo) obj).responseBody);
    }
}
