package com.randy.randyclient.download;

import android.content.Context;
import android.util.Log;

import com.randy.randyclient.base.BaseSubscriber;
import com.randy.randyclient.exception.SelfDefineThrowable;

/**
 * 下载订阅
 */
public class DownloadSubscriber<ResponseBody extends okhttp3.ResponseBody>
        extends BaseSubscriber<ResponseBody> {
    private DownloadCallback callBack;
    private Context context;
    private String savePath;
    private String name;
    private String key;

    public DownloadSubscriber(String key, String savePath, String name,
                              DownloadCallback callBack, Context context) {
        super(context);
        this.key = key;
        this.savePath = savePath;
        this.name = name;
        this.callBack = callBack;
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != callBack) {
            callBack.onStart(key);
        }
    }

    @Override
    public void onCompleted() {
        if (null != callBack) {
            callBack.onCompleted();
        }
    }

    @Override
    public void onExceptionError(final SelfDefineThrowable e) {
        Log.e(DownloadManager.TAG, "DownloadSubscriber:>>>> onError:" + e.getMessage());
        if (null != callBack) {
            callBack.onError(e);
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        Log.d(DownloadManager.TAG, "DownloadSubscriber:>>>> onNext");
        new DownloadManager(callBack).writeResponseBodyToDisk(key, savePath, name, context,
                responseBody);

    }
}
