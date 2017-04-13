package com.randy.randyclient.Interceptor;

import com.randy.randyclient.cache.DbCacheInfo;
import com.randy.randyclient.helper.DbCacheHelper;

import java.io.IOException;
import java.nio.charset.Charset;

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
    private String dbCacheUrl;
    private DbCacheHelper dbCacheHelper;

    public DbCacheInterceptor(boolean isDbCache, String dbCacheUrl, DbCacheHelper dbCacheHelper) {
        this.isDbCache = isDbCache;
        this.dbCacheUrl = dbCacheUrl;
        this.dbCacheHelper = dbCacheHelper;
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
            DbCacheInfo dbCacheInfo = dbCacheHelper.queryDbCacheByUrl(dbCacheUrl);
            long time = System.currentTimeMillis();
            if (dbCacheInfo == null) {
                dbCacheInfo = new DbCacheInfo(dbCacheUrl, time, bodyString);
                dbCacheHelper.saveDbCacheInfo(dbCacheInfo);
            } else {
                dbCacheInfo.setCacheTime(time);
                dbCacheInfo.setCacheContent(bodyString);
                dbCacheHelper.updateDbCacheInfo(dbCacheUrl);
            }
        }

        return response;
    }
}
