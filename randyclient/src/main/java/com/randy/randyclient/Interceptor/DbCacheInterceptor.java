package com.randy.randyclient.Interceptor;

import com.randy.randyclient.cache.DbCacheInfo;
import com.randy.randyclient.helper.DbCacheHelper;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 数据库缓存拦截器
 * Created by RandyZhang on 2017/4/13.
 */

public class DbCacheInterceptor implements Interceptor {
    private boolean isDbCache = false;
    private String baseUrl;

    public DbCacheInterceptor(boolean isDbCache, String baseUrl) {
        this.isDbCache = isDbCache;
        this.baseUrl = baseUrl;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (isDbCache) {
            ResponseBody responseBody = response.body();
            BufferedSource bufferedSource = responseBody.source();
            bufferedSource.request(Long.MAX_VALUE);
            Buffer buffer = bufferedSource.buffer();
            Charset charset = Charset.defaultCharset();
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset();
            }
            String bodyString = buffer.clone().readString(charset);
            String dbCacheUrl = getPathUrl(request);

            DbCacheInfo dbCacheInfo = DbCacheHelper.getInstance().queryDbCacheByUrl(dbCacheUrl);
            long time = System.currentTimeMillis();
            if (dbCacheInfo == null) {
                dbCacheInfo = new DbCacheInfo(dbCacheUrl, time, bodyString);
                DbCacheHelper.getInstance().saveDbCacheInfo(dbCacheInfo);
            } else {
                dbCacheInfo.setCacheTime(time);
                dbCacheInfo.setCacheContent(bodyString);
                DbCacheHelper.getInstance().updateDbCacheInfo(dbCacheInfo);
            }
        }

        return response;
    }

    /**
     * 获取带参数url
     *
     * @param request Request object
     */
    private String getPathUrl(Request request) {
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
                sb.delete(0, baseUrl.length());
            }
        }
        return sb.toString();
    }
}
