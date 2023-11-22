package com.getech.android.httphelper.exception;


public class ApiException extends Exception {
    private int code;
    private String rawMessage;
    private String displayMessage;
    @ExceptionHandler.ExceptionType
    private int type;
    private int httpCode;
    private String tag;

    public ApiException(@ExceptionHandler.ExceptionType int type, int code, String rawMessage, String displayMessage, String tag) {
        super(rawMessage);
        this.type = type;
        this.code = code;
        this.displayMessage = displayMessage;
        this.tag = tag;
    }

    public ApiException(@ExceptionHandler.ExceptionType int type, int code, String rawMessage, String displayMessage, int httpCode, String tag) {
        super(rawMessage);
        this.type = type;
        this.code = code;
        this.displayMessage = displayMessage;
        this.httpCode = httpCode;
        this.tag = tag;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    @ExceptionHandler.ExceptionType
    public int getType() {
        return type;
    }

    public void setType(@ExceptionHandler.ExceptionType int type) {
        this.type = type;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}