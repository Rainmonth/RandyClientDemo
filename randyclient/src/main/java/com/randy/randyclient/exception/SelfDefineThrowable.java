package com.randy.randyclient.exception;

/**
 * Created by RandyZhang on 2017/4/11.
 */

public class SelfDefineThrowable extends Exception {
    int code;
    String message;

    public SelfDefineThrowable() {
    }

    public SelfDefineThrowable(int code, Throwable throwable) {
        super(throwable);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
