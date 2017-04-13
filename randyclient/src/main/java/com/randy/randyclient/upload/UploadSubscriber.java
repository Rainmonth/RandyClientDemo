package com.randy.randyclient.upload;

import android.content.Context;

import com.randy.randyclient.base.BaseSubscriber;
import com.randy.randyclient.exception.SelfDefineThrowable;

/**
 * 上传订阅
 * Created by RandyZhang on 2017/4/13.
 */

public class UploadSubscriber<RequestBody extends okhttp3.RequestBody>
        extends BaseSubscriber<RequestBody> {

    private UploadCallback callback;
    private String key;

    public UploadSubscriber(Context context, UploadCallback callback, String key) {
        super(context);
        this.callback = callback;
        this.key = key;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != callback) {
            callback.onStart(key);
        }
    }

    @Override
    public void onNext(RequestBody requestBody) {
        ProgressRequestBody progressRequestBody = (ProgressRequestBody) requestBody;

    }

    @Override
    public void onCompleted() {
        super.onCompleted();
        if (null != callback) {
            callback.onComplete(key);
        }
    }

    @Override
    public void onExceptionError(SelfDefineThrowable e) {
        if (null != callback) {
            callback.onError(key, e);
        }
    }
}
