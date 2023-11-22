package com.getech.android.httphelper.httpinterface;

import com.getech.android.httphelper.constant.Constants;

import java.io.Serializable;
import java.util.HashMap;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BaseHttpResult<T> implements Serializable {
    private static final long serialVersionUID = 5213230387195987834L;

    public int code;
    public String msg;
    public T data;
    public T result;
    public HashMap<String, Object> extra;


    public boolean isSuccess() {
        return Constants.HTTP_SUCCESS_CODE == code;
    }

    @Override
    public String toString() {
        return "BaseHttpResult:{\n" +
                "\tcode=" + code + "\n" +
                "\tmsg='" + msg + "\'\n" +
                "\tdata=" + data + "\n" +
                '}';
    }
}
