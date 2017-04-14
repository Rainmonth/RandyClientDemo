package com.randy.randyclient.base;

import com.randy.randyclient.exception.SelfDefineThrowable;

/**
 * Created by RandyZhang on 2017/4/11.
 *
 * @param <T> 最终得到的数据类型
 */
public interface IBaseResponse<T> {
    void onStart();

    void onCompleted();

    void onExceptionError(SelfDefineThrowable e);

    void onReadCacheSuccess(T response);

    void onSuccess(T response);
}
