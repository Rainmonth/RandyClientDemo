package com.randy.randyclient.exception;

/**
 * Created by RandyZhang on 2017/4/12.
 * 服务端异常，记录返回code和message
 */

public class ServerException extends RuntimeException {
    private int code;
    private String message;

    public ServerException(int code, String message) {
        super(message);
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
