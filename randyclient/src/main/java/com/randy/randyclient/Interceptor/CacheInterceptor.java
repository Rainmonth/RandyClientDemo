package com.randy.randyclient.Interceptor;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.randy.randyclient.config.Global;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 缓存拦截器
 * <p>
 * 通过拦截器来修改缓存策略
 * </p>
 * Created by RandyZhang on 2017/4/12.
 */

public class CacheInterceptor implements Interceptor {
    protected Context context;
    // offline cache control value
    protected String cacheControlValue_Offline;
    // online cache control value
    protected String cacheControlValue_Online;
    //set cache times is 3 days
    protected static final int maxStale = 60 * 60 * 24 * 3;
    // read from cache for 60 s
    protected static final int maxStaleOnline = 60;

    public CacheInterceptor(Context context) {
        this(context, String.format(Global.LOCALE, "max-age=%d", maxStaleOnline));
    }

    public CacheInterceptor(Context context, String cacheControlValueOffline) {
        this(context, cacheControlValueOffline, String.format(Global.LOCALE, "max-age=%d", maxStale));
    }

    public CacheInterceptor(Context context, String cacheControlValueOffline,
                            String cacheControlValueOnline) {
        this.context = context;
        this.cacheControlValue_Offline = cacheControlValueOffline;
        this.cacheControlValue_Online = cacheControlValueOnline;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");
        //String cacheControl = request.cacheControl().toString();
        Log.e(Global.TAG, maxStaleOnline + "s load cache:" + cacheControl);
        if (TextUtils.isEmpty(cacheControl) || cacheControl.contains("no-store")
                || cacheControl.contains("no-cache") || cacheControl.contains("must-revalidate")
                || cacheControl.contains("max-age") || cacheControl.contains("max-stale")) {
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxStale)
                    .build();
        } else {
            return originalResponse;
        }
    }
}
