package com.randy.randyclient.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.randy.randyclient.cache.DbCacheInfo;
import com.randy.randyclient.config.Global;
import com.randy.randyclient.exception.BadFormatException;
import com.randy.randyclient.exception.ExceptionHandle;
import com.randy.randyclient.exception.SelfDefineThrowable;
import com.randy.randyclient.exception.ServerException;
import com.randy.randyclient.helper.DbCacheHelper;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

/**
 * 最终处理请求的订阅者
 * Created by RandyZhang on 2017/4/11.
 */

public class FinalSubscriber<T> extends BaseSubscriber<ResponseBody> {
    private IBaseResponse<T> callback;
    private Type finalDataType;
    private boolean isDbCache = false;
    private boolean isNetworkAvailable = false;
    private String url;

    private String TAG = FinalSubscriber.class.getSimpleName();

    public FinalSubscriber(Context context, Type finalDataType, IBaseResponse<T> callback) {
        super(context);
        this.callback = callback;
        this.finalDataType = finalDataType;
    }

    /**
     * 需要数据库缓存时调用的构造函数
     */
    public FinalSubscriber(Context context, Type finalDataType, boolean isDbCache, String pathUrl,
                           Map<String, Object> paramMap, IBaseResponse<T> callback) {
        super(context);
        this.isDbCache = isDbCache;
        url = getKeyUrl(pathUrl, paramMap);
        this.callback = callback;
        this.finalDataType = finalDataType;
    }

    @Override
    public void onCompleted() {
        super.onCompleted();
        if (null != callback)
            callback.onCompleted();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onNext(ResponseBody body) {
        try {
            byte[] bytes = body.bytes();
            String jsonStr = new String(bytes);
            Log.d(TAG, "onNext():" + jsonStr);
            if (null != callback) {
//                /*
//                  if need parse baseRespone<T> use ParentType, if parse T use childType.
//                   defult parse baseRespone<T>
//                  callBack.onSuccess((T) JSON.parseArray(jsStr, (Class<Object>) finalNeedType));
//                  Type finalNeedType = needChildType;
//                 */
                BaseResponse<T> baseResponse = getBaseResponse(jsonStr);
                if (baseResponse.isOk()) {
                    callback.onSuccess((T) new Gson().fromJson(jsonStr, finalDataType));
                } else {
                    ServerException serverException = getServerException(baseResponse);
                    callback.onExceptionError(ExceptionHandle.handleException(serverException));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onExceptionError(ExceptionHandle.handleException(e));
            }
        }
    }

    /**
     * 根据返回结果获取异常信息
     *
     * @param baseResponse 服务器返回结果
     */
    @NonNull
    private ServerException getServerException(BaseResponse<T> baseResponse) {
        String finalMsg, msg, error, message;
        msg = baseResponse.getMsg();
        error = baseResponse.getError();
        message = baseResponse.getMessage();
        finalMsg = msg != null ? msg : error != null
                ? error : message != null ? message : "api未知错误信息";
        return new ServerException(baseResponse.getCode(), finalMsg);
    }

    @NonNull
    private BaseResponse<T> getBaseResponse(String jsonStr) {
        BaseResponse<T> baseResponse = null;
        if (new Gson().fromJson(jsonStr, finalDataType) == null) {
            throw new NullPointerException();
        }
        baseResponse = new Gson().fromJson(jsonStr, finalDataType);
//                    if (ConfigLoader.isFormat(mContext) && baseResponse.getData() == null
//                          & baseResponse.getResult() == null)
        if (baseResponse.getData() == null & baseResponse.getResult() == null) {
            throw new BadFormatException();
        }
        return baseResponse;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 开启了数据库缓存，网络不可用时，
        if (isDbCache && isNetworkAvailable) {
            // 获取缓存数据，只处理正确的情况，因为错误的情况在onExceptionError中会处理
            dealWithDbCacheWhenStart(url);
        }
        if (null != callback) {
            callback.onStart();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onExceptionError(SelfDefineThrowable e) {
        if (isDbCache) {
            dealWithDbCacheWhenError(url);
        } else {
            if (null != callback) {
                callback.onExceptionError(e);
            }
        }
    }

    /**
     * 获取缓存对应的key值（该值由接口路径和接口请求参数得到）
     *
     * @param pathUrl 接口路径
     * @param paraMap 接口请求参数
     * @return key值
     */
    private String getKeyUrl(String pathUrl, Map<String, Object> paraMap) {
        if (null == pathUrl) {
            return "";
        }
        if (null == paraMap) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(pathUrl);
        sb.append("?");
        for (Map.Entry entry : paraMap.entrySet()) {
            sb.append(entry.getKey()).append("=")
                    .append(String.valueOf(entry.getValue())).append("&");
        }
        sb.delete(sb.length() - 1, sb.length());
        Log.i(TAG, "getKeyUrl " + sb.toString());
        return sb.toString();
    }

    /**
     * 刚开始的情况下处理缓存
     *
     * @param urlKey urlKey
     */
    @SuppressWarnings("unchecked")
    private void dealWithDbCacheWhenStart(String urlKey) {
        Observable.just(urlKey).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                if (null != callback) {
                    callback.onCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (null != callback) {
                    callback.onExceptionError(new SelfDefineThrowable(-1000, e));
                }
            }

            @Override
            public void onNext(String s) {
                    /*获取缓存数据*/
                DbCacheInfo dbCacheInfo = DbCacheHelper.getInstance().queryDbCacheByUrl(s);
                if (dbCacheInfo != null) {
                    long time = (System.currentTimeMillis() - dbCacheInfo.getCacheTime()) / 1000;
                    if (time < Global.DB_CACHE_VALID_TIME) {
                        if (callback != null) {
                            Log.i(TAG, "dealWithDbCacheWhenStart" + dbCacheInfo.getCacheContent());
                            callback.onReadCacheSuccess((T) getBaseResponse(dbCacheInfo.getCacheContent()));
                            onCompleted();
                            unsubscribe();
                        }
                    }
                }
            }
        });
    }

    /**
     * 错误的情况下处理缓存
     *
     * @param urlKey urlKey
     */
    @SuppressWarnings("unchecked")
    private void dealWithDbCacheWhenError(String urlKey) {
        Observable.just(urlKey).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (null != callback) {
                    callback.onExceptionError(new SelfDefineThrowable(-1000, e));
                }
            }

            @Override
            public void onNext(String s) {
                    /*获取缓存数据*/
                DbCacheInfo dbCacheInfo = DbCacheHelper.getInstance().queryDbCacheByUrl(s);
                if (dbCacheInfo == null) {
                    if (null != callback) {
                        new NullPointerException("缓存为空");
                        SelfDefineThrowable defineThrowable = new SelfDefineThrowable();
                        defineThrowable.setCode(ExceptionHandle.ERROR.READ_DB_CACHE_ERROR);
                        defineThrowable.setMessage("对应缓存为空");
                        callback.onExceptionError(defineThrowable);
//                        callback.onExceptionError(new SelfDefineThrowable
//                                (ExceptionHandle.ERROR.READ_DB_CACHE_ERROR,
//                                        new Exception("读取数据库缓存错误")));
//                        onCompleted();
//                        unsubscribe();
                    }
                } else {
                    long time = (System.currentTimeMillis() - dbCacheInfo.getCacheTime()) / 1000;
                    if (time < Global.DB_CACHE_VALID_TIME) {
                        if (callback != null) {
                            Log.i(TAG, "dealWithDbCacheWhenError" + dbCacheInfo.getCacheContent());
                            callback.onReadCacheSuccess((T) getBaseResponse(dbCacheInfo
                                    .getCacheContent()));
                        }
                    } else {
                        DbCacheHelper.getInstance().deleteCookie(dbCacheInfo);
                        if (null != callback) {
                            callback.onExceptionError(new SelfDefineThrowable(
                                    ExceptionHandle.ERROR.READ_DB_CACHE_ERROR,
                                    new Exception("读取数据库缓存错误")));
                        }
                    }
                    onCompleted();
                    unsubscribe();
                }
            }
        });
    }
}
