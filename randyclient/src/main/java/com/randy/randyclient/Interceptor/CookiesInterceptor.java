package com.randy.randyclient.Interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Cookies 拦截器
 * Created by RandyZhang on 2017/4/11.
 */

public class CookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        return null;
    }
}
