package com.randy.randyclient.Interceptor;

import android.util.Log;

import com.randy.randyclient.config.Global;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 日志打印拦截器
 * Created by RandyZhang on 2017/4/11.
 */

public class LogInterceptor implements Interceptor {
    private LEVEL level = LEVEL.All;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        ResponseBody responseBody = response.body();
        BufferedSource bufferedSource = responseBody.source();
        bufferedSource.request(Long.MAX_VALUE);
        Buffer buffer = bufferedSource.buffer();
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset();
            } catch (UnsupportedCharsetException e) {
                Log.e(Global.TAG, "Couldn't decode the response body; charset is likely malformed.");
                return response;
            }
        }
        StringBuilder sb = new StringBuilder();// 用于构建url+parameter
        sb.append(request.url());//  添加url
        if ("POST".equals(request.method())) {
            if (request.body() instanceof FormBody) {// 添加post参数
                FormBody formBody = (FormBody) request.body();
                sb.append("?");
                for (int i = 0; i < formBody.size(); i++) {
                    sb.append(formBody.encodedName(i)).append("=").
                            append(formBody.encodedValue(i)).append("&");
                }
                sb.delete(sb.length() - 1, sb.length());
            }
        }

        long contentLength = responseBody.contentLength();
        if (contentLength != 0) {
            long t1 = System.nanoTime();
            if (level == LEVEL.URL) {
                Log.i("OkHttp", request.method() + sb.toString());
            } else if (level == LEVEL.HEADERS) {
                Log.i("OkHttp", request.method() + sb.append(request.headers()));
            } else {
                long t2 = System.nanoTime();
                if (level == LEVEL.All) {
                    Log.i("OkHttp", request.method() + sb.append(request.headers()));
                    Log.i("OkHttp", String.format("Received response for %s in %s%s",
                            sb.toString(), (t2 - t1) / 1e6d, response.headers()));
                    Log.i("OkHttp", "-------------------开始打印返回数据---------------------");
                    Log.i("json", buffer.clone().readString(charset));
                    Log.i("OkHttp", "-------------------结束打印返回数据---------------------");
                }
            }
        }

        return response;
    }

    private enum LEVEL {
        URL,        // 打印带参数的URL
        HEADERS,    // 打印URL + Headers
        All,        // 打印URL + Headers + Json
    }
}
