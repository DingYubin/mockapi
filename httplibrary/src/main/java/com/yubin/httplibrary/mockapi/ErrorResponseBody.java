package com.yubin.httplibrary.mockapi;

public class ErrorResponseBody {
    private int statusCode;
    private String error;
    private String message;
    private String errorCode;
    private String errState;
    private String errorMessage;

    public ErrorResponseBody(int statusCode, String error, String message, String errorCode, String errState, String errorMessage) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.errorCode = errorCode;
        this.errState = errState;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrState() {
        return errState;
    }

    public void setErrState(String errState) {
        this.errState = errState;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
