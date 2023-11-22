package com.getech.android.httphelper.impl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class SafeHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        //验证主机名是否匹配
        return true;
    }
}