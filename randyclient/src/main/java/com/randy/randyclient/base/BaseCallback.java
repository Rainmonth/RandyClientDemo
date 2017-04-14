package com.randy.randyclient.base;

import com.randy.randyclient.exception.SelfDefineThrowable;

/**
 * Created by RandyZhang on 2017/4/14.
 *
 * @param <T> 最终得到的数据类型
 */

public abstract class BaseCallback<T> implements IBaseResponse<T> {
    @Override
    public void onStart() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onExceptionError(SelfDefineThrowable e) {

    }

    @Override
    public void onReadCacheSuccess(T response) {

    }

    @Override
    abstract public void onSuccess(T response);
}
