package com.randy.randyclient.exception;

/**
 * 数据格式错误异常
 * Created by RandyZhang on 2017/4/11.
 */

public class BadFormatException extends RuntimeException {
    private int code = -200;
    private String message = "服务端返回数据格式异常";

    public BadFormatException() {
    }

    public BadFormatException(int code, String message) {
        this.code = code;
        this.message = message;
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
