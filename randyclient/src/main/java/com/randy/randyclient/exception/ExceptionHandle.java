package com.randy.randyclient.exception;

import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.text.ParseException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by RandyZhang on 2017/4/12.
 * 异常处理
 */

public class ExceptionHandle {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    private static final int ACCESS_DENIED = 302;
    private static final int HANDEL_ERROR = 417;

    /**
     * 约定异常
     */
    public class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;

        /**
         * 证书未找到
         */
        public static final int SSL_NOT_FOUND = 1007;

        /**
         * 出现空值
         */
        public static final int NULL = -100;

        /**
         * 格式错误
         */
        public static final int FORMAT_ERROR = 1008;
    }

    public static SelfDefineThrowable handleException(Throwable throwable) {
        SelfDefineThrowable selfDefineThrowable = null;
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            selfDefineThrowable = new SelfDefineThrowable(ERROR.HTTP_ERROR, throwable);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                    selfDefineThrowable.setMessage("未授权的请求");
                    break;
                case FORBIDDEN:
                    selfDefineThrowable.setMessage("禁止访问");
                    break;
                case NOT_FOUND:
                    selfDefineThrowable.setMessage("服务器地址未找到");
                    break;
                case REQUEST_TIMEOUT:
                    selfDefineThrowable.setMessage("请求超时");
                    break;
                case INTERNAL_SERVER_ERROR:
                    selfDefineThrowable.setMessage("服务器出错");
                    break;
                case BAD_GATEWAY:
                    selfDefineThrowable.setMessage("无效的请求");
                    break;
                case GATEWAY_TIMEOUT:
                    selfDefineThrowable.setMessage("网关响应超市");
                    break;
                case SERVICE_UNAVAILABLE:
                    selfDefineThrowable.setMessage("服务器不可用");
                case ACCESS_DENIED:
                    selfDefineThrowable.setMessage("网络错误");
                    break;
                case HANDEL_ERROR:
                    selfDefineThrowable.setMessage("接口处理失败");
                    break;
                default:
                    selfDefineThrowable.setMessage(throwable.getMessage());
                    break;
            }
            selfDefineThrowable.setCode(httpException.code());
            return selfDefineThrowable;
        } else if (throwable instanceof ServerException) {
            ServerException se = (ServerException) throwable;
            selfDefineThrowable = new SelfDefineThrowable(se.getCode(), se);
            selfDefineThrowable.setMessage(se.getMessage());
            return selfDefineThrowable;
        } else if (throwable instanceof JsonParseException
                || throwable instanceof JSONException
                || throwable instanceof ParseException) {
            selfDefineThrowable = new SelfDefineThrowable(ERROR.PARSE_ERROR, throwable);
            selfDefineThrowable.setMessage("解析错误");
            return selfDefineThrowable;
        } else if (throwable instanceof ConnectException) {
            selfDefineThrowable = new SelfDefineThrowable(ERROR.NETWORD_ERROR, throwable);
            selfDefineThrowable.setMessage("连接失败");
            return selfDefineThrowable;
        } else if (throwable instanceof javax.net.ssl.SSLHandshakeException) {
            selfDefineThrowable = new SelfDefineThrowable(ERROR.SSL_ERROR, throwable);
            selfDefineThrowable.setMessage("证书验证失败");
            return selfDefineThrowable;
        } else if (throwable instanceof java.security.cert.CertPathValidatorException) {
            selfDefineThrowable = new SelfDefineThrowable(ERROR.SSL_NOT_FOUND, throwable);
            selfDefineThrowable.setMessage("证书路径没找到");
            return selfDefineThrowable;
        } else if (throwable instanceof ConnectTimeoutException) {
            selfDefineThrowable = new SelfDefineThrowable(ERROR.TIMEOUT_ERROR, throwable);
            selfDefineThrowable.setMessage("连接超时");
            return selfDefineThrowable;
        } else if (throwable instanceof java.net.SocketTimeoutException) {
            selfDefineThrowable = new SelfDefineThrowable(ERROR.TIMEOUT_ERROR, throwable);
            selfDefineThrowable.setMessage("连接超时");
            return selfDefineThrowable;
        } else if (throwable instanceof ClassCastException) {
            selfDefineThrowable = new SelfDefineThrowable(ERROR.FORMAT_ERROR, throwable);
            selfDefineThrowable.setMessage("类型转换出错");
            return selfDefineThrowable;
        } else if (throwable instanceof NullPointerException) {
            selfDefineThrowable = new SelfDefineThrowable(ERROR.NULL, throwable);
            selfDefineThrowable.setMessage("数据有空");
            return selfDefineThrowable;
        } else if (throwable instanceof BadFormatException) {
            BadFormatException resultException = (BadFormatException) throwable;
            selfDefineThrowable = new SelfDefineThrowable(resultException.getCode(), throwable);
            selfDefineThrowable.setMessage(resultException.getMessage());
            return selfDefineThrowable;
        } else {
            selfDefineThrowable = new SelfDefineThrowable(ERROR.UNKNOWN, throwable);
            selfDefineThrowable.setMessage(throwable.getLocalizedMessage());
            return selfDefineThrowable;
        }

    }
}
