package com.randy.randyclient.Interceptor;

import android.content.Context;
import android.util.Log;

import com.randy.randyclient.config.Global;
import com.randy.randyclient.util.NetworkUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 离线缓存拦截器
 * <p>
 * 通过拦截器来修改缓存策略
 * </p>
 * Created by RandyZhang on 2017/4/12.
 */

public class OfflineCacheInterceptor extends CacheInterceptor {
    public OfflineCacheInterceptor(Context context) {
        super(context);
    }

    public OfflineCacheInterceptor(Context context, String cacheControlValue) {
        super(context, cacheControlValue);
    }

    public OfflineCacheInterceptor(Context context, String cacheControlValue,
                                   String cacheOnlineControlValue) {
        super(context, cacheControlValue, cacheOnlineControlValue);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetworkUtil.isNetworkAvailable(context)) {
            Log.e(Global.TAG, " no network load cache:" + request.cacheControl().toString());

            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, " + cacheControlValue_Offline)
                    .build();
        }
        return chain.proceed(request);
    }
}
