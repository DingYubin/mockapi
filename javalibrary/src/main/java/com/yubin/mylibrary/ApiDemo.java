package com.yubin.mylibrary;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2024/07/17
 *     desc    : 会话
 *     version : 1.0
 * </pre>
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ApiDemo {

    // 编码
    private static final String CHARSET_UTF8 = "utf-8";

    // 沙箱环境api调用地址
    private static final String domain = "https://sandbox-developer-api.vivo.com.cn/router/rest";
    // 正式环境api调用地址
    // private static final String domain = "https://developer-api.vivo.com.cn/router/rest";
    // 申请API服务后平台分配的accessKey
    private static final String accessKey = "202310177j5out1k";
    // 申请API服务后平台分配的accessSecret
    private static final String accessSecret = "0172b69f05e711eabae6d0946672c4fb";
    // 签名算法(当前支持HMAC-SHA256)
    private static final String signMethod = "HMAC-SHA256";
    // api版本
    private static final String apiVersion = "1.0";
    // 返回数据类型(当前支持json)
    private static final String format = "json";
    // 接口目标类型, 接口传包必须使用developer
    private static final String targetAppKey = "developer";
    // 文件边界
    private static final String boundary = UUID.randomUUID().toString();


    public static void main(String[] args) throws Exception {
        // 1. 组装参数
        Map<String, Object> paramsMap = assembleParams();
        paramsMap.put("method", "app.upload.apk.app");
        paramsMap.put("packageName", "com.yubin.ec");
        paramsMap.put("fileMd5", "8115b90f00c9d8cecb9108c313c71b14");

        // 2. 计算签名
        String sign = calSign(paramsMap);
        System.out.printf("cal sign = %s%n", sign);
        paramsMap.put("sign", sign);
        // 3.1 普通文本请求api服务
//        String response = sendRequest(new URL(domain), paramsMap);
//        System.out.printf("Response body:%s%n", response);
        // 3.2 文件上传请求api服务
        // 文件上传是本地文件路径
         String filePath = "downloadedFile.apk";
         String response = sendRequest(new URL(domain), paramsMap, filePath);
         System.out.printf("Response body:%s%n", response);
    }

    /**
     * 组装请求参数：公共参数+业务参数
     */
    private static Map<String, Object> assembleParams() {
        Map<String, Object> paramsMap = new HashMap<>();
        // 公共参数
        paramsMap.put("access_key", accessKey);
        paramsMap.put("timestamp", System.currentTimeMillis());
        paramsMap.put("format", format);
        paramsMap.put("v", apiVersion);
        paramsMap.put("target_app_key", targetAppKey);
        paramsMap.put("sign_method", signMethod);
        // method是调用的具体业务方法,可以查看某个详细的业务接口获取,eg：https://dev.vivo.com.cn/documentCenter/doc/342
//        paramsMap.put("method", "xxxxxx");
        // 业务参数: 可以查看某个详细业务接口文档获得,eg:https://dev.vivo.com.cn/documentCenter/doc/342
//        paramsMap.put("xxx", "xxxxxx");
        return paramsMap;
    }

    /**
     * 计算签名
     */
    private static String calSign(Map<String, Object> paramsMap) {
        // 参数排序:按ascii码排序的参数键值对拼接结果
        List<String> keysList = new ArrayList<>(paramsMap.keySet());
        Collections.sort(keysList);
        List<String> paramList = new ArrayList<>();
        for (String key : keysList) {
            Object object = paramsMap.get(key);
            if (object == null) {
                continue;
            }
            String value = key + "=" + object;
            paramList.add(value);
        }
        String params = String.join("&", paramList);

        // 使用HmacSHA256进行加密
        try {
            byte[] secretByte = accessSecret.getBytes(Charset.forName("UTF-8"));
            SecretKeySpec signingKey = new SecretKeySpec(secretByte, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] dataByte = params.getBytes(Charset.forName("UTF-8"));
            byte[] by = mac.doFinal(dataByte);
            return byteArr2HexStr(by);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HMAC_SHA256 加密后的数组进行16进制转换
     *  @return String 返回加密后字符串
     */
    private static String byteArr2HexStr(byte[] bytes) {
        int length= bytes.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuilder sb = new StringBuilder(length* 2);
        for (int i = 0; i < length; i++) {
            // 将得到的字节转16进制
            String strHex = Integer.toHexString(bytes[i] & 0xFF);
            // 每个字节由两个字符表示，位数不够，高位补0
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return sb.toString();
    }

    /**
     * 普通文本提交
     */
    private static String sendRequest(URL url, Map<String, Object> params) throws IOException {
        String query = buildQuery(params);
        byte[] content = {};
        if (query != null) {
            content = query.getBytes(CHARSET_UTF8);
        }

        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Host", url.getHost());
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET_UTF8);
            out = conn.getOutputStream();
            out.write(content);
            rsp = getResponseToString(conn);
        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rsp;
    }

    /**
     * 文件上传
     */
    public static String sendRequest(URL url, Map<String, Object> textMap, String filePath) throws IOException {
        String rsp = "";
        HttpURLConnection conn = null;
        DataInputStream in = null;
        InputStream inStream = null;
        OutputStream out = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator<Map.Entry<String, Object>> iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Object> entry = iter.next();
                    String inputName = entry.getKey();
                    Object object = entry.getValue();
                    if(object == null){
                        continue;
                    }
                    String inputValue = String.valueOf(entry.getValue());
                    strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }

            // file
            File file = new File(filePath);
            String filename = file.getName();
            Path path = Paths.get(filePath);
            String contentType = Files.probeContentType(path);

            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=file; filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            inStream = new FileInputStream(file);
            in = new DataInputStream(inStream);
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }

            byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 读取返回数据
            rsp = getResponseToString(conn);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null) {
                in.close();
            }
            if(inStream != null) {
                inStream.close();
            }
            if(out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rsp;
    }

    /**
     * 构建请求参数，eg：key1=value1&key2=value2
     */
    private static String buildQuery(Map<String, Object> params) throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder query = new StringBuilder();
        Set<Entry<String, Object>> entries = params.entrySet();
        boolean hasParam = false;

        for (Entry<String, Object> entry : entries) {
            String name = entry.getKey();
            Object value = entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (isNotEmpty(name) && value != null) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }
                query.append(name).append("=").append(URLEncoder.encode(String.valueOf(value), CHARSET_UTF8));
            }
        }
        return query.toString();
    }

    /**
     * 上传文件方式获取返回结果
     */
    private static String getResponseToString(HttpURLConnection conn) throws IOException {
        String charset = getResponseCharset(conn.getContentType());
        if (conn.getResponseCode() < 400) {
            return getStreamToString(conn.getInputStream(), charset);
        } else {// Client Error 4xx and Server Error 5xx
            throw new IOException(conn.getResponseCode() + " " + conn.getResponseMessage());
        }
    }

    /**
     * 上传文件方式获取返回结果
     */
    private static String getStreamToString(InputStream stream, String charset) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }
            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * 获取返回结果编码类型
     */
    private static String getResponseCharset(String contentType) {
        String charset = CHARSET_UTF8;
        if (isNotEmpty(contentType)) {
            String[] params = contentType.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (isNotEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }
        return charset;
    }

    /**
     * 字符串为空判断
     */
    private static boolean isNotEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return true;
            }
        }
        return false;
    }
}
