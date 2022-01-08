package com.yubin.httplibrary.mockapi;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable {
    private int errorCode;
    private String message;
    private T data;
    private int teamCode;

    public BaseResponse(int errorCode, String message, T data, int teamCode) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
        this.teamCode = teamCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(int teamCode) {
        this.teamCode = teamCode;
    }

    public boolean isSuccessful(int errorCode){

        return errorCode == this.errorCode;
    }
}
