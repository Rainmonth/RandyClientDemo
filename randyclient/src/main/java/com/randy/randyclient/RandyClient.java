package com.randy.randyclient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.randy.randyclient.Interceptor.CacheInterceptor;
import com.randy.randyclient.Interceptor.HeaderInterceptor;
import com.randy.randyclient.Interceptor.OfflineCacheInterceptor;
import com.randy.randyclient.base.BaseApiService;
import com.randy.randyclient.base.BaseResponse;
import com.randy.randyclient.base.BaseResponseCallback;
import com.randy.randyclient.base.BaseSubscriber;
import com.randy.randyclient.base.FinalSubscriber;
import com.randy.randyclient.cache.CookieCacheImpl;
import com.randy.randyclient.cookie.CookieManager;
import com.randy.randyclient.cookie.SharedPrefsCookiePersistor;
import com.randy.randyclient.download.DownloadSubscriber;
import com.randy.randyclient.download.DownloadCallback;
import com.randy.randyclient.exception.ExceptionHandle;
import com.randy.randyclient.helper.ClientHttpsFactory;
import com.randy.randyclient.upload.ProgressRequestBody;
import com.randy.randyclient.upload.UploadCallback;
import com.randy.randyclient.upload.UploadSubscriber;
import com.randy.randyclient.util.FileUtil;
import com.randy.randyclient.util.Utils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Call;
import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.Part;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 网络请求客户端封装
 * Created by RandyZhang on 2017/4/12.
 */
@SuppressWarnings({"unchecked"})
public final class RandyClient {
    /**
     * 普通请求线程切换
     */
    final Observable.Transformer normalSchedulersTransformer
            = new Observable.Transformer() {
        @Override
        public Object call(Object o) {
            return ((Observable) o).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * 下载请求线程切换
     */
    final Observable.Transformer downloadSchedulersTransformer
            = new Observable.Transformer() {
        @Override
        public Object call(Object o) {
            return ((Observable) o).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io());
        }
    };

    /**
     * 上传请求线程切换
     */
    final Observable.Transformer uploadSchedulersTransformer
            = new Observable.Transformer() {
        @Override
        public Object call(Object o) {
            return ((Observable) o).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io());
        }
    };

    /**
     * 异常处理切换
     */
    private Observable.Transformer exceptionSchedulersTransformer = null;

    private <T> Observable.Transformer<BaseResponse<T>, T> getExceptionTransformer() {
        if (exceptionSchedulersTransformer != null) return exceptionSchedulersTransformer;
        else return exceptionSchedulersTransformer = new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable) observable)/*.map(new HandleFuc<T>())*/
                        .onErrorResumeNext(new HttpResponseFunc<T>());
            }
        };
    }

    private static class HttpResponseFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable call(java.lang.Throwable t) {
            return Observable.error(ExceptionHandle.handleException(t));
        }
    }

    // headers
    private static Map<String, String> headers;
    // parameters
    private static Map<String, String> parameters;
    // retrofit builder
    private static Retrofit.Builder retrofitBuilder;
    // retrofit
    private static Retrofit retrofit;
    // baseApiService
    private static BaseApiService apiManager;
    // baseUrl
    private String baseUrl;
    // OkHttpClient builder
    private static OkHttpClient.Builder okHttpClientBuilder;
    // OkHttpClient
    private static OkHttpClient okHttpClient;
    // context
    private static Context mContext;
    // 是否开启数据库缓存
    private static boolean isDbCache = false;
    private okhttp3.Call.Factory callFactory;
    // download observable
    private Observable<ResponseBody> downloadObservable;
    // download observable map
    private Map<String, Observable<ResponseBody>> downloadObservableMap = new HashMap<>();
    // upload observable
    private Observable<ResponseBody> uploadObservable;
    // upload observable map
    private Map<String, Observable<ResponseBody>> uploadObservableMap = new HashMap<>();
    // callback executor
    private Executor callbackExecutor;

    private boolean validateEagerly;
    // converter factory list
    private final List<Converter.Factory> converterFactories;
    // call adapter factory list;
    private final List<CallAdapter.Factory> adapterFactories;
    // TAG for log
    public final String TAG = RandyClient.class.getSimpleName();

    /**
     * package constructor
     */
    RandyClient(okhttp3.Call.Factory callFactory, String baseUrl, Map<String, String> headers,
                Map<String, String> parameters, BaseApiService apiManager,
                List<Converter.Factory> converterFactories,
                List<CallAdapter.Factory> adapterFactories,
                Executor callbackExecutor, boolean validateEagerly) {
        RandyClient.headers = headers;
        RandyClient.parameters = parameters;
        RandyClient.apiManager = apiManager;
        this.baseUrl = baseUrl;
        this.callFactory = callFactory;
        this.callbackExecutor = callbackExecutor;
        this.validateEagerly = validateEagerly;
        this.converterFactories = converterFactories;
        this.adapterFactories = adapterFactories;
    }

    /**
     * 创建service
     *
     * @param serviceClazz service接口类
     * @param <T>          泛型参数
     * @return T
     */
    public <T> T createService(Class<T> serviceClazz) {
        return retrofit.create(serviceClazz);
    }

    /**
     * retrofit get
     *
     * @param url        请求地址
     * @param paramMap   请求参数
     * @param subscriber 订阅对象
     * @param <T>        泛型参数
     * @return T
     */
    public <T> T get(String url, Map<String, Object> paramMap,
                     BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executeGet(url, paramMap)
                .compose(normalSchedulersTransformer)
                .compose(getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * client get
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @param callBack 订阅对象
     * @param <T>      泛型参数
     * @return T
     */
    public <T> T executeGet(String url, Map<String, Object> paramMap,
                            BaseResponseCallback<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();
        if (genericTypeHandle(types) == null || genericTypeHandle(types).size() == 0) {
            return null;
        }
        final Type finalNeedType = genericTypeHandle(types).get(0);
        Log.d(TAG, "-->:" + "Type:" + types[0]);
        return (T) apiManager.executeGet(url, paramMap)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(new FinalSubscriber(mContext, finalNeedType, callBack));
    }

    /**
     * retrofit post
     *
     * @param url        请求地址
     * @param paramMap   请求参数
     * @param subscriber 订阅对象
     * @param <T>        泛型参数
     * @return T
     */
    public <T> T post(String url, @FieldMap(encoded = true) Map<String, Object> paramMap,
                      BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executePost(url, paramMap)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * client post
     * <p>
     * <b>no need to parse ResponseBody</b>
     * </p>
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @param callBack 订阅对象
     * @param <T>      泛型参数
     * @return T
     */
    public <T> T executePost(String url, @FieldMap(encoded = true) Map<String, Object> paramMap,
                             BaseResponseCallback<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();
        if (genericTypeHandle(types) == null || genericTypeHandle(types).size() == 0) {
            return null;
        }
        final Type finalNeedType = genericTypeHandle(types).get(0);
        Log.d(TAG, "-->:" + "Type:" + types[0]);
        return (T) apiManager.executePost(url, paramMap)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(new FinalSubscriber(mContext, finalNeedType, callBack));
    }

    /**
     * retrofit post
     *
     * @param url        请求地址
     * @param body       请求参数
     * @param subscriber 订阅对象
     * @param <T>        泛型参数
     * @return T
     */
    public <T> T body(String url, @Body RequestBody body,
                      BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executePostBody(url, body)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * client post
     * <p>
     * <b>no need to parse ResponseBody</b>
     * </p>
     *
     * @param url      请求地址
     * @param body     请求参数
     * @param callBack 订阅对象
     * @param <T>      泛型参数
     * @return T
     */
    public <T> T executeBody(String url, @Body RequestBody body,
                             BaseResponseCallback<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();
        if (genericTypeHandle(types) == null || genericTypeHandle(types).size() == 0) {
            return null;
        }
        final Type finalNeedType = genericTypeHandle(types).get(0);
        Log.d(TAG, "-->:" + "Type:" + types[0]);
        return (T) apiManager.executePostBody(url, body)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(new FinalSubscriber(mContext, finalNeedType, callBack));
    }


    /**
     * retrofit form
     *
     * @param url        请求地址
     * @param paramMap   请求参数
     * @param subscriber 订阅对象
     * @param <T>        泛型参数
     * @return T
     */
    public <T> T form(String url, final @FieldMap(encoded = true) Map<String, Object> paramMap,
                      BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.postForm(url, paramMap)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * client form
     * <p>
     * <b>no need to parse ResponseBody</b>
     * </p>
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @param callBack 订阅对象
     * @param <T>      泛型参数
     * @return T
     */
    public <T> T executeForm(String url, @FieldMap(encoded = true) Map<String, Object> paramMap,
                             BaseResponseCallback<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();
        if (genericTypeHandle(types) == null || genericTypeHandle(types).size() == 0) {
            return null;
        }
        final Type finalNeedType = genericTypeHandle(types).get(0);
        Log.d(TAG, "-->:" + "Type:" + types[0]);
        return (T) apiManager.postForm(url, paramMap)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(new FinalSubscriber(mContext, finalNeedType, callBack));
    }

    /**
     * retrofit post json
     *
     * @param url        请求地址
     * @param jsonStr    Json String
     * @param subscriber 订阅对象
     */
    public <T> T json(String url, String jsonStr, BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.postRequestBody(url, Utils.createJson(jsonStr))
                .compose(normalSchedulersTransformer)
                .compose(getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * client post json
     * <p>
     * <b>no need to parse ResponseBody</b>
     * </p>
     *
     * @param url     request json
     * @param jsonStr Json String
     * @return parsed data T
     */
    public <T> T executeJson(final String url, final String jsonStr, final BaseResponseCallback<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();
        if (genericTypeHandle(types) == null || genericTypeHandle(types).size() == 0) {
            return null;
        }
        final Type finalNeedType = genericTypeHandle(types).get(0);
        Log.d(TAG, "-->:" + "Type:" + types[0]);
        return (T) apiManager.postRequestBody(url, Utils.createJson(jsonStr))
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(new FinalSubscriber(mContext, finalNeedType, callBack));
    }

    /**
     * retrofit put
     *
     * @param url        请求地址
     * @param paramMap   请求参数
     * @param subscriber 订阅对象
     * @param <T>        泛型参数
     * @return T
     */
    public <T> T put(String url, final @FieldMap(encoded = true) Map<String, Object> paramMap,
                     BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executePut(url, paramMap)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * client put
     * <p>
     * <b>no need to parse ResponseBody</b>
     * </p>
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @param callBack 订阅对象
     * @param <T>      泛型参数
     * @return T
     */
    public <T> T executePut(String url, @FieldMap(encoded = true) Map<String, Object> paramMap,
                            BaseResponseCallback<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();
        if (genericTypeHandle(types) == null || genericTypeHandle(types).size() == 0) {
            return null;
        }
        final Type finalNeedType = genericTypeHandle(types).get(0);
        Log.d(TAG, "-->:" + "Type:" + types[0]);
        return (T) apiManager.executePut(url, paramMap)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(new FinalSubscriber(mContext, finalNeedType, callBack));
    }

    /**
     * retrofit delete
     *
     * @param url        请求地址
     * @param paramMap   请求参数
     * @param subscriber 订阅对象
     * @param <T>        泛型参数
     * @return T
     */
    public <T> T delete(String url, Map<String, Object> paramMap,
                        BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executeDelete(url, paramMap)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * client post
     * <p>
     * <b>no need to parse ResponseBody</b>
     * </p>
     *
     * @param url      请求地址
     * @param paramMap 请求参数
     * @param callBack 订阅对象
     * @param <T>      泛型参数
     * @return T
     */
    public <T> T executeDelete(String url, Map<String, Object> paramMap,
                               BaseResponseCallback<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();
        if (genericTypeHandle(types) == null || genericTypeHandle(types).size() == 0) {
            return null;
        }
        final Type finalNeedType = genericTypeHandle(types).get(0);
        Log.d(TAG, "-->:" + "Type:" + types[0]);
        return (T) apiManager.executeDelete(url, paramMap)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(new FinalSubscriber(mContext, finalNeedType, callBack));
    }

    /**
     * retrofit upload
     *
     * @param url         upload url
     * @param requestBody requestBody
     * @param subscriber  subscriber
     * @param <T>         T
     * @return T
     */
    public <T> T upload(String url, RequestBody requestBody,
                        BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.postRequestBody(url, requestBody)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * uploadImage
     * <p>
     * <b>no need to parse ResponseBody</b>
     * </p>
     *
     * @param url        url
     * @param file       file
     * @param subscriber subscriber
     * @param <T>        T
     * @return T
     */
    public <T> T uploadImage(String url, File file, BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadImage(url, Utils.createImage(file))
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * upload File
     *
     * @param url        upload url
     * @param file       file
     * @param subscriber subscriber
     * @param <T>        T
     * @return T
     */
    public <T> T uploadFlie(String url, RequestBody file,
                            BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.postRequestBody(url, file)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(subscriber);
    }


    /**
     * upload File
     *
     * @param url        upload url
     * @param file       file
     * @param subscriber subscriber
     * @param <T>        T
     * @return T
     */
    public <T> T uploadFlie(String url, RequestBody description, MultipartBody.Part file,
                            BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadFlie(url, description, file)
                .compose(normalSchedulersTransformer)
                .compose(getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * upload Flies
     *
     * @param url        upload url
     * @param subscriber subscriber
     * @param <T>        T
     * @return T
     */
    public <T> T uploadFlies(String url, Map<String, RequestBody> files,
                             BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadFiles(url, files)
                .compose(normalSchedulersTransformer)
                .compose(getExceptionTransformer())
                .subscribe(subscriber);
    }


    /**
     * upload Flies WithPartMap
     *
     * @param url        upload url
     * @param partMap    map contains upload parameters
     * @param file       file to be upload
     * @param subscriber subscriber to deal with upload file request
     * @param <T>        generic type T
     * @return generic type T
     */
    public <T> T uploadFileWithPartMap(String url, Map<String, RequestBody> partMap,
                                       @Part("file") MultipartBody.Part file,
                                       BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadFileWithPartMap(url, partMap, file)
                .compose(normalSchedulersTransformer)
                .compose(this.getExceptionTransformer())
                .subscribe(subscriber);
    }

    /**
     * download
     *
     * @param url      download url
     * @param callBack the download callback to be called after request
     */
    public void download(String url, DownloadCallback callBack) {
        download(url, FileUtil.getFileNameWithURL(url), callBack);
    }

    /**
     * @param url      download url
     * @param name     the download file saved name
     * @param callBack the download callback to be called after request
     */
    public void download(String url, String name, DownloadCallback callBack) {
        download(FileUtil.generateFileKey(url, name), url, null, name, callBack);
    }

    /**
     * downloadMin
     *
     * @param url      download url
     * @param callBack the download callback to be called after request
     */
    public void downloadMin(String url, DownloadCallback callBack) {
        downloadMin(FileUtil.generateFileKey(url, FileUtil.getFileNameWithURL(url)), url, callBack);
    }

    /**
     * downloadMin
     *
     * @param key      observable key
     * @param url      download url
     * @param callBack the download callback to be called after request
     */
    public void downloadMin(String key, String url, DownloadCallback callBack) {
        downloadMin(key, url, FileUtil.getFileNameWithURL(url), callBack);
    }

    /**
     * downloadMin
     *
     * @param key      observable key
     * @param url      download url
     * @param name     the download file saved name
     * @param callBack the download callback to be called after request
     */
    public void downloadMin(String key, String url, String name, DownloadCallback callBack) {
        downloadMin(key, url, null, name, callBack);
    }

    /**
     * download small file
     *
     * @param key      observable key
     * @param url      download url
     * @param savePath where to save the download file
     * @param name     the download file saved name
     * @param callBack the download callback to be called after request
     */
    public void downloadMin(String key, String url, String savePath, String name,
                            DownloadCallback callBack) {

        if (downloadObservableMap.get(key) == null) {
            downloadObservable = apiManager.downloadSmallFile(url);
        } else {
            downloadObservable = downloadObservableMap.get(key);
        }
        downloadObservableMap.put(key, downloadObservable);
        executeDownload(key, savePath, name, callBack);
    }

    /**
     * download
     *
     * @param key      observable key
     * @param url      download url
     * @param savePath where to save the download file
     * @param name     the download file saved name
     * @param callBack the download callback to be called after request
     */
    public void download(String key, String url, String savePath, String name,
                         DownloadCallback callBack) {
        if (downloadObservableMap.get(key) == null) {
            downloadObservable = apiManager.downloadFile(url);
        } else {
            downloadObservable = downloadObservableMap.get(url);
        }
        downloadObservableMap.put(key, downloadObservable);
        executeDownload(key, savePath, name, callBack);
    }

    public void upload(String key, String url, ProgressRequestBody requestBody) {
        UploadCallback uploadCallback = requestBody.getUploadCallback();
        if (uploadObservableMap.get(key) == null) {
            uploadObservable = apiManager.uploadFile(url, requestBody);
        } else {
            uploadObservable = uploadObservableMap.get(key);
        }
        executeUpload(key, uploadCallback);
    }

    private void executeUpload(String key, UploadCallback uploadCallback) {
        if (uploadObservableMap.get(key) != null) {
            uploadObservableMap.get(key)
                    .compose(uploadSchedulersTransformer)
                    .compose(this.getExceptionTransformer())
                    .subscribe(new UploadSubscriber(mContext, uploadCallback, key));
        }
    }

    /**
     * executeDownload
     *
     * @param key      observable key
     * @param savePath where to save the download file
     * @param name     the download file saved name
     * @param callBack the download callback to be called after request
     */
    private void executeDownload(String key, String savePath, String name,
                                 DownloadCallback callBack) {
        if (downloadObservableMap.get(key) != null) {
            // 根据observable key获取到对应的observable
            downloadObservableMap.get(key).compose(downloadSchedulersTransformer)
                    .compose(getExceptionTransformer())
                    .subscribe(new DownloadSubscriber<>(key, savePath, name, callBack, mContext));
        }

    }

    /**
     * 处理泛型参数
     *
     * @return generic type list
     */
    private List<Type> genericTypeHandle(Type[] types) {
        Log.d(TAG, "types size: " + types.length);
        List<Type> needTypes = new ArrayList<>();
        Type needParentType = null;
        for (Type paramType : types) {
            System.out.println("  " + paramType);
            // if Type is T
            if (paramType instanceof ParameterizedType) {
                Type[] parentTypes = ((ParameterizedType) paramType).getActualTypeArguments();
                Log.d(TAG, "TypeArgument: ");
                for (Type childType : parentTypes) {
                    Log.d(TAG, "childType:" + childType);
                    needTypes.add(childType);
                    //needParentType = childType;
                    if (childType instanceof ParameterizedType) {
                        Type[] childTypes = ((ParameterizedType) childType).getActualTypeArguments();
                        for (Type type : childTypes) {
                            needTypes.add(type);
                            //needChildType = type;
                            Log.d(TAG, "type:" + childType);
                        }
                    }
                }
            }
        }
        return needTypes;
    }

    public static final class Builder {
        private static final int DEFAULT_TIMEOUT = 5;
        private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 5;
        private static final long DEFAULT_KEEP_ALIVE_DURATION = 8;
        private static final long MAX_CACHE_SIZE = 10 * 1024 * 1024;

        private okhttp3.Call.Factory callFactory;
        private String baseUrl;
        private Boolean isLog = false;
        private Boolean isCookie = false;
        private Boolean isCache = true;
        private List<InputStream> certificateList;
        private HostnameVerifier hostnameVerifier;
        private CertificatePinner certificatePinner;
        private List<Converter.Factory> converterFactories = new ArrayList<>();
        private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        private Executor callbackExecutor;
        private boolean validateEagerly;
        private Context context;
        private CookieManager cookieManager;
        private Cache cache = null;
        private Proxy proxy;
        private File httpCacheDirectory;
        private SSLSocketFactory sslSocketFactory;
        private ConnectionPool connectionPool;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;
        private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR;
        private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE;

        public Builder(Context context) {
            okHttpClientBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            this.context = context;
        }

        @NonNull
        public Builder client(OkHttpClient client) {
            retrofitBuilder.client(Utils.checkNotNull(client, "client == null"));
            return this;
        }

        /**
         * Add ApiManager for serialization and deserialization of objects.
         *//*
        public Builder addApiManager(final Class<ApiManager> service) {

            apiManager = retrofit.create((Utils.checkNotNull(service, "apiManager == null")));
            //return retrofit.create(service);
            return this;
        }*/

        /**
         * Specify a custom call factory for creating {@link } instances.
         * <p/>
         * Note: Calling {@link #client} automatically sets this value.
         */
        public Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = Utils.checkNotNull(factory, "factory == null");
            return this;
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder connectTimeout(int timeout) {
            return connectTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder writeTimeout(int timeout) {
            return writeTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * open default logcat
         *
         * @param isLog whether to show request log
         */
        public Builder addLog(boolean isLog) {
            this.isLog = isLog;
            return this;
        }

        /**
         * open sync default Cookie
         *
         * @param isCookie whether to save cookie
         */
        public Builder addCookie(boolean isCookie) {
            this.isCookie = isCookie;
            return this;
        }

        /**
         * open default Cache
         *
         * @param isCache where to cache
         * @return
         */
        public Builder addCache(boolean isCache) {
            this.isCache = isCache;
            return this;
        }

        /**
         * open the proxy
         *
         * @param proxy where to proxy
         */
        public Builder proxy(Proxy proxy) {
            okHttpClientBuilder.proxy(Utils.checkNotNull(proxy, "proxy == null"));
            return this;
        }

        /**
         * Sets the default write timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link TimeUnit #MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder writeTimeout(int timeout, TimeUnit unit) {
            if (timeout != -1) {
                okHttpClientBuilder.writeTimeout(timeout, unit);
            } else {
                okHttpClientBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * Sets the connection pool used to recycle HTTP and HTTPS connections.
         * <p>
         * <p>If unset, a new connection pool will be used.
         */
        public Builder connectionPool(ConnectionPool connectionPool) {
            if (connectionPool == null) throw new NullPointerException("connectionPool == null");
            this.connectionPool = connectionPool;
            return this;
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link TimeUnit #MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder connectTimeout(int timeout, TimeUnit unit) {
            if (timeout != -1) {
                okHttpClientBuilder.connectTimeout(timeout, unit);
            } else {
                okHttpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }


        /**
         * Set an API base URL which can change over time.
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = Utils.checkNotNull(baseUrl, "baseUrl == null");
            return this;
        }

        /**
         * Add converter factory for serialization and deserialization of objects.
         */
        public Builder addConverterFactory(Converter.Factory factory) {
            this.converterFactory = factory;
            return this;
        }

        /**
         * Add a call adapter factory for supporting service method return types other than
         * {@link CallAdapter}
         */
        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            this.callAdapterFactory = factory;
            return this;
        }

        /**
         * Add Header for serialization and deserialization of objects.
         */
        public <T> Builder addHeader(Map<String, T> headers) {
            okHttpClientBuilder.addInterceptor(new HeaderInterceptor(Utils.checkNotNull(headers,
                    "header == null")));
            return this;
        }

        /**
         * Add parameters for serialization and deserialization of objects.
         */
        public <T> Builder addParameters(Map<String, T> parameters) {
            okHttpClientBuilder.addInterceptor(new HeaderInterceptor(Utils.checkNotNull(parameters,
                    "parameters == null")));
            return this;
        }

        /**
         * Returns a modifiable list of interceptors that observe a single network request and
         * response.These interceptors must call {@link Interceptor.Chain#proceed} exactly once:
         * it is an error for a network interceptor to short-circuit or repeat a network request.
         */
        public Builder addInterceptor(Interceptor interceptor) {
            okHttpClientBuilder.addInterceptor(Utils.checkNotNull(interceptor,
                    "interceptor == null"));
            return this;
        }

        /**
         * The executor on which {@link Call} methods are invoked when returning {@link Call} from
         * your service method.
         * <p/>
         * Note: {@code executor} is not used for {@linkplain #addCallAdapterFactory custom method
         * return types}.
         */
        public Builder callbackExecutor(Executor executor) {
            this.callbackExecutor = Utils.checkNotNull(executor, "executor == null");
            return this;
        }

        /**
         * When calling {@link Retrofit#create} on the resulting {@link Retrofit} instance, eagerly
         * validate the configuration of all methods in the supplied interface.
         */
        public Builder validateEagerly(boolean validateEagerly) {
            this.validateEagerly = validateEagerly;
            return this;
        }

        /**
         * Sets the handler that can accept cookies from incoming HTTP responses and provides
         * cookies to outgoing HTTP requests.
         * <p/>
         * <p>If unset, {@linkplain CookieManager#NO_COOKIES no cookies} will be accepted
         * nor provided.
         */
        public Builder cookieManager(CookieManager cookie) {
            if (cookie == null) throw new NullPointerException("cookieManager == null");
            this.cookieManager = cookie;
            return this;
        }

        /**
         *
         */
        public Builder addSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        public Builder addHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        public Builder addCertificatePinner(CertificatePinner certificatePinner) {
            this.certificatePinner = certificatePinner;
            return this;
        }


        /**
         * Sets the handler that can accept cookies from incoming HTTP responses and provides
         * cookies to outgoing HTTP requests.
         * <p/>
         * <p>If unset, {@linkplain CookieManager#NO_COOKIES no cookies} will be accepted
         * nor provided.
         */
        public Builder addSSL(String[] hosts, int[] certificates) {
            if (hosts == null) throw new NullPointerException("hosts == null");
            if (certificates == null) throw new NullPointerException("ids == null");

            addSSLSocketFactory(ClientHttpsFactory.getSSLSocketFactory(context, certificates));
            addHostnameVerifier(ClientHttpsFactory.getHostnameVerifier(hosts));
            return this;
        }

        /**
         * 添加networkInterceptor
         *
         * @param interceptor the interceptor to be added
         */
        public Builder addNetworkInterceptor(Interceptor interceptor) {
            okHttpClientBuilder.addNetworkInterceptor(interceptor);
            return this;
        }

        /**
         * setCache
         *
         * @param cache cahe
         * @return Builder
         */
        public Builder addCache(Cache cache) {
            int maxStale = 60 * 60 * 24 * 3;
            return addCache(cache, maxStale);
        }

        /**
         * @param cache
         * @param cacheTime ms
         */
        public Builder addCache(Cache cache, final int cacheTime) {
            addCache(cache, String.format("max-age=%d", cacheTime));
            return this;
        }

        /**
         * 添加OKHttp3 cache缓存
         *
         * @param cache             {@link okhttp3.Cache}
         * @param cacheControlValue Cache-Control
         */
        private Builder addCache(Cache cache, final String cacheControlValue) {
            REWRITE_CACHE_CONTROL_INTERCEPTOR = new CacheInterceptor(mContext, cacheControlValue);
            REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE = new OfflineCacheInterceptor(mContext,
                    cacheControlValue);
            addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
            addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE);
            addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE);
            this.cache = cache;
            return this;
        }

        /**
         * Create the {@link Retrofit} instance using the configured values.
         * <p>
         * Note: If neither {@link #client} nor {@link #callFactory} is called a default {@link
         * OkHttpClient} will be created and used.
         * </p>
         */
        public RandyClient build() {
            // set Context
            mContext = context;
            // okHttpClientBuilder check
            if (okHttpClientBuilder == null) {
                throw new IllegalStateException("okHttpBuilder should not be null.");
            }
            // retrofitBuilder check
            if (retrofitBuilder == null) {
                throw new IllegalStateException("retrofitBuilder should not be null.");
            }
            // baseUrl check
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL should not be null.");
            }
            // set the base url
            retrofitBuilder.baseUrl(baseUrl);
            // init converter factory if converter factory is null.
            if (converterFactory == null) {
                converterFactory = GsonConverterFactory.create();
            }
            // Add converter factory for serialization and deserialization of objects.
            retrofitBuilder.addConverterFactory(converterFactory);
            /*
             * Add a call adapter factory for supporting service method return types other than
             * {@link Call}.
             */
            if (callAdapterFactory == null) {
                callAdapterFactory = RxJavaCallAdapterFactory.create();
            }
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);
            if (isLog) {
                okHttpClientBuilder.addNetworkInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.HEADERS));
            }
            if (sslSocketFactory != null) {
                okHttpClientBuilder.sslSocketFactory(sslSocketFactory);
            }
            if (hostnameVerifier != null) {
                okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
            }
            if (httpCacheDirectory == null) {
                httpCacheDirectory = new File(mContext.getCacheDir(), String.format("%s_http_cache",
                        mContext.getPackageName()));
            }
            if (isCache) {
                try {
                    if (cache == null) {
                        cache = new Cache(httpCacheDirectory, MAX_CACHE_SIZE);
                    }
                    addCache(cache);
                } catch (Exception e) {
                    Log.e("OKHttp", "Could not create http cache", e);
                }
                if (cache == null) {
                    cache = new Cache(httpCacheDirectory, MAX_CACHE_SIZE);
                }
            }

            if (cache != null) {
                okHttpClientBuilder.cache(cache);
            }

            /*
              Sets the connection pool used to recycle HTTP and HTTPS connections.
              If unset, a new connection pool will be used.
             */
            if (connectionPool == null) {
                connectionPool = new ConnectionPool(DEFAULT_MAX_IDLE_CONNECTIONS,
                        DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS);
            }
            okHttpClientBuilder.connectionPool(connectionPool);

            /**
             * Sets the HTTP proxy that will be used by connections created by this client.
             * This takes precedence over {@link #proxySelector}, which is only honored when
             * this proxy is null (which it is by default).
             * To disable proxy use completely, call {@code setProxy(Proxy.NO_PROXY)}.
             */
            // set http proxy
            if (proxy != null) {
                okHttpClientBuilder.proxy(proxy);
            }

            /**
             * Sets the handler that can accept cookies from incoming HTTP responses and provides
             * cookies to outgoing HTTP requests.
             *
             * <p>If unset, {@link com.randy.retrofitclient.cookie.CookieManager#NO_COOKIES
             * no cookies} will be accepted nor provided.
             */
            if (isCookie && cookieManager == null) {
                okHttpClientBuilder.cookieJar(new CookieManager(new CookieCacheImpl(),
                        new SharedPrefsCookiePersistor(context)));

            }

            if (cookieManager != null) {
                okHttpClientBuilder.cookieJar(cookieManager);
            }
            // set callFactory
            if (callFactory != null) {
                retrofitBuilder.callFactory(callFactory);
            }
            // create okHttpClient
            okHttpClient = okHttpClientBuilder.build();
            // set Retrofit client
            retrofitBuilder.client(okHttpClient);
            // create Retrofit
            retrofit = retrofitBuilder.build();
            // create BaseApiService
            apiManager = retrofit.create(BaseApiService.class);
            return new RandyClient(callFactory, baseUrl, headers, parameters, apiManager,
                    converterFactories, adapterFactories, callbackExecutor, validateEagerly);


        }
    }
}
