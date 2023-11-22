package com.getech.android.httphelper.httpinterface.interceptor;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/15
 *     desc   : htt请求参数信息
 *     version: 1.0
 * </pre>
 */
public final class HttpRequestInfo {
    /**
     * header数据
     */
    private HashMap<String, String> headers;
    /**
     * 请求的参数
     */
    private HashMap<String, String> formData;
    /**
     * post请求的body
     */
    private String requestBody;
    /**
     * 需要删除的header key
     */
    private ArrayList<String> needRemoveHeaders;
    /**
     * 需要删除的formData key
     */
    private ArrayList<String> needRemoveFormDatakeys;
    /**
     * 是否可以修改请求body
     */
    private boolean canChangeBody = false;
    /**
     * 请求的url
     */
    private String requestUrl;
    /**
     * 请求方法
     */
    private String requestMethod;
    private boolean changed;

    public HttpRequestInfo(String requestUrl, String requestMethod) {
        headers = new HashMap<>();
        formData = new HashMap<>();
        needRemoveHeaders = new ArrayList<>();
        this.requestUrl = requestUrl;
        this.requestMethod = requestMethod;
        changed = true;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        changed = true;
    }

    public HashMap<String, String> getFormData() {
        return formData;
    }

    public void setFormData(HashMap<String, String> formData) {
        this.formData = formData;
        changed = true;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        if (canChangeBody) {
            if (requestBody == null) {
                requestBody = "";
            }
            this.requestBody = requestBody;
            changed = true;
        }
    }

    /**
     * 删除某个header
     *
     * @param headerKey
     */
    public void removeHeader(String headerKey) {
        needRemoveHeaders.add(headerKey);
        changed = true;
    }

    /**
     * 删除某个formData
     *
     * @param formDataKey
     */
    public void removeFormData(String formDataKey) {
        needRemoveFormDatakeys.add(formDataKey);
        changed = true;
    }

    public void addHeader(String key, String value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return;
        }
        headers.put(key, value);
        changed = true;
    }

    public void addFormData(String key, String value) {
        if (TextUtils.isEmpty(key) || value == null) {
            return;
        }
        formData.put(key, value);
        changed = true;
    }

    public boolean isCanChangeBody() {
        return canChangeBody;
    }

    public void setCanChangeBody(boolean canChangeBody) {
        this.canChangeBody = canChangeBody;
        changed = true;
    }

    public ArrayList<String> getNeedRemoveHeaders() {
        return needRemoveHeaders;
    }

    public void setNeedRemoveHeaders(ArrayList<String> needRemoveHeaders) {
        this.needRemoveHeaders = needRemoveHeaders;
        changed = true;
    }

    public ArrayList<String> getNeedRemoveFormDatakeys() {
        return needRemoveFormDatakeys;
    }

    public void setNeedRemoveFormDatakeys(ArrayList<String> needRemoveFormDatakeys) {
        this.needRemoveFormDatakeys = needRemoveFormDatakeys;
        changed = true;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void initChanged() {
        changed = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HttpRequestInfo) || obj == null) {
            return false;
        }
        if (changed) {
            return false;
        }
        //header
        HashMap<String, String> otherHeaders = ((HttpRequestInfo) obj).headers;
        boolean headerEquals = equalsHashMap(headers, otherHeaders);
        if (!headerEquals) {
            return false;
        }
        //formData
        HashMap<String, String> otherFormData = ((HttpRequestInfo) obj).formData;
        boolean formDataEquals = equalsHashMap(formData, otherFormData);
        if (!formDataEquals) {
            return false;
        }
        //body
        if (!TextUtils.equals(requestBody, ((HttpRequestInfo) obj).requestBody)) {
            return false;
        }
        //需要删除的header
        ArrayList<String> otherNeedRemovedHeaders = ((HttpRequestInfo) obj).needRemoveHeaders;
        boolean removeHeaderEquals = equalsArrayList(needRemoveHeaders, otherNeedRemovedHeaders);
        if (!removeHeaderEquals) {
            return false;
        }
        //需要删除的formData key
        ArrayList<String> otherNeedRemoveFormDatas = ((HttpRequestInfo) obj).needRemoveFormDatakeys;
        boolean removeFormDataEquals = equalsArrayList(needRemoveFormDatakeys, otherNeedRemoveFormDatas);
        if (!removeFormDataEquals) {
            return false;
        }
        return true;
    }

    private static <K, V> boolean equalsHashMap(HashMap<K, V> map1, HashMap<K, V> map2) {
        if (map1 != map2) {
            return false;
        }
        if (map1 != null && map2 != null && (map1.size() != map2.size())) {
            return false;
        }
        if (map1 != null && map1.size() > 0) {
            for (Map.Entry<K, V> entry : map1.entrySet()) {
                V value1 = entry.getValue();
                V value2 = map2.get(entry.getKey());
                if (value1 == null && value2 == null) {
                    continue;
                }
                if (value1 != null) {
                    if (value2 == null) {
                        return false;
                    } else {
                        if (!value1.equals(value2)) {
                            return false;
                        }
                        continue;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private static <T> boolean equalsArrayList(ArrayList<T> array1, ArrayList<T> array2) {
        if (array1 != array2) {
            return false;
        }
        if (array1 != null && array2 != null && (array1.size() != array2.size())) {
            return false;
        }
        if (array1 != null && array1.size() > 0) {
            for (int i = 0; i < array1.size(); i++) {
                T value1 = array1.get(i);
                T value2 = array2.get(i);
                if (value1 == null && value2 == null) {
                    continue;
                }
                if (value1 != null) {
                    if (value1.equals(value2)) {
                        continue;
                    }
                }
                return false;
            }
        }
        return true;
    }

}
