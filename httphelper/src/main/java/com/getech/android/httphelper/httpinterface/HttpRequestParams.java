package com.getech.android.httphelper.httpinterface;

import com.getech.android.httphelper.constant.Constants;
import com.getech.android.httphelper.constant.HttpMethodEnum;

import java.util.HashMap;
import java.util.UUID;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/11
 *     desc   : 请求体
 *     version: 1.0
 * </pre>
 */
public class HttpRequestParams {
    private String url;
    private String tag;
    private int timeout;
    private HashMap<String, String> header;
    private HashMap<String, String> formData;
    private String requestBody;
    private boolean json;
    private HttpMethodEnum httpMethod;
    private String fileDownloadPath;
    private String uploadFilePath;
    private String uploadFileName;
    /**
     * 上传文件使用的key
     */
    private String uploadFileKey;


    public HttpRequestParams() {
        tag = UUID.randomUUID().toString().replace("-", "");
        timeout = Constants.HTTP_DEFAULT_TIMEOUT;
    }

    public String getUrl() {
        return url;
    }

    public HttpRequestParams setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public HttpRequestParams setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public HttpRequestParams setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public HashMap<String, String> getHeader() {
        return header;
    }

    public HttpRequestParams setHeader(HashMap<String, String> header) {
        this.header = header;
        return this;
    }

    public HashMap<String, String> getFormData() {
        return formData;
    }

    public HttpRequestParams setFormData(HashMap<String, String> formData) {
        this.formData = formData;
        return this;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public HttpRequestParams setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public boolean isJson() {
        return json;
    }

    public HttpRequestParams setJson(boolean json) {
        this.json = json;
        return this;
    }

    public HttpMethodEnum getHttpMethod() {
        return httpMethod;
    }

    public HttpRequestParams setHttpMethod(HttpMethodEnum httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public String getFileDownloadPath() {
        return fileDownloadPath;
    }

    public HttpRequestParams setFileDownloadPath(String fileDownloadPath) {
        this.fileDownloadPath = fileDownloadPath;
        return this;
    }

    public String getUploadFilePath() {
        return uploadFilePath;
    }

    public HttpRequestParams setUploadFilePath(String uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
        return this;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public HttpRequestParams setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
        return this;
    }

    public String getUploadFileKey() {
        return uploadFileKey;
    }

    public HttpRequestParams setUploadFileKey(String uploadFileKey) {
        this.uploadFileKey = uploadFileKey;
        return this;
    }
}
