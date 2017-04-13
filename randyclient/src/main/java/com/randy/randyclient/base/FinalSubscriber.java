package com.randy.randyclient.base;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.randy.randyclient.exception.BadFormatException;
import com.randy.randyclient.exception.ExceptionHandle;
import com.randy.randyclient.exception.SelfDefineThrowable;
import com.randy.randyclient.exception.ServerException;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * 最终处理请求的订阅者
 * Created by RandyZhang on 2017/4/11.
 */

public class FinalSubscriber<T> extends BaseSubscriber<ResponseBody> {
    private BaseResponseCallback<T> callback;
    private Type finalDataType;

    public FinalSubscriber(Context context, Type finalDataType, BaseResponseCallback<T> callback) {
        super(context);
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
            Log.d("OkHttp", "ResponseBody:" + jsonStr);
            if (null != callback) {
                /*
                  if need parse baseRespone<T> use ParentType, if parse T use childType.
                   defult parse baseRespone<T>
                  callBack.onSuccess((T) JSON.parseArray(jsStr, (Class<Object>) finalNeedType));
                  Type finalNeedType = needChildType;
                 */
                BaseResponse<T> baseResponse = null;
                if (new Gson().fromJson(jsonStr, finalDataType) == null) {
                    throw new NullPointerException();
                }
                baseResponse = new Gson().fromJson(jsonStr, finalDataType);
//                    if (ConfigLoader.isFormat(mContext) && baseResponse.getData() == null & baseResponse.getResult() == null)
                if (baseResponse.getData() == null & baseResponse.getResult() == null) {
                    throw new BadFormatException();
                }

                if (baseResponse.isOk()) {
                    callback.onSuccess((T) new Gson().fromJson(jsonStr, finalDataType));
                } else {
                    String finalMsg, msg, error, message;
                    msg = baseResponse.getMsg();
                    error = baseResponse.getError();
                    message = baseResponse.getMessage();
                    finalMsg = msg != null ? msg : error != null
                            ? error : message != null ? message : "api未知错误信息";
                    ServerException serverException = new ServerException(baseResponse.getCode(),
                            finalMsg);
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onExceptionError(SelfDefineThrowable e) {
        if (null != callback) {
            callback.onExceptionError(e);
        }
    }
}
