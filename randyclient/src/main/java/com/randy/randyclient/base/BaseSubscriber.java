package com.randy.randyclient.base;

import android.content.Context;

import com.randy.randyclient.exception.ExceptionHandle;
import com.randy.randyclient.exception.SelfDefineThrowable;

import rx.Subscriber;

/**
 * Created by RandyZhang on 2017/4/11.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T> {
    private Context context;

    public BaseSubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(java.lang.Throwable e) {
        if (e instanceof SelfDefineThrowable) {
            onExceptionError((SelfDefineThrowable) e);
        } else {
            // 未知异常
            onExceptionError(new SelfDefineThrowable(ExceptionHandle.ERROR.UNKNOWN, e));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 将异常交给自定义的处理函数进行处理而不是有默认的onError函数处理
     *
     * @param e 自定义异常
     */
    public abstract void onExceptionError(SelfDefineThrowable e);
}
