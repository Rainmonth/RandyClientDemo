package com.randy.randyclient.Interceptor;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求头拦截
 * <p>
 * 通过拦截器想请求中添加指定header
 * </p>
 * Created by RandyZhang on 2017/4/11.
 */

public class HeaderInterceptor<T> implements Interceptor {
    private Map<String, T> headers;

    public HeaderInterceptor(Map<String, T> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request()
                .newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey) == null ? "" :
                        (String) headers.get(headerKey)).build();
            }
        }
        return chain.proceed(builder.build());

    }
}
