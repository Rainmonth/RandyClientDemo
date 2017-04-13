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

    private UploadCallback uploadCallback;
    private String key;

    public UploadSubscriber(Context context, UploadCallback uploadCallback, String key) {
        super(context);
        this.uploadCallback = uploadCallback;
        this.key = key;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != uploadCallback) {
            uploadCallback.onStart(key);
        }
    }

    @Override
    public void onNext(RequestBody requestBody) {
        ProgressRequestBody progressRequestBody = (ProgressRequestBody) requestBody;
        uploadCallback.onSuccess(key);
    }

    @Override
    public void onCompleted() {
        super.onCompleted();
        if (null != uploadCallback) {
            uploadCallback.onComplete(key);
        }
    }

    @Override
    public void onExceptionError(SelfDefineThrowable e) {
        if (null != uploadCallback) {
            uploadCallback.onError(key, e);
        }
    }
}
