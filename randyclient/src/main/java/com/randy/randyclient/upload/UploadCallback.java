package com.randy.randyclient.upload;

import com.randy.randyclient.exception.SelfDefineThrowable;

/**
 * 上传回调
 * Created by RandyZhang on 2017/4/13.
 */

public abstract class UploadCallback {
    /**
     * @param key 上传文件对应的key
     */
    void onStart(String key) {

    }

    /**
     * @param key 上传文件对应的key
     */
    void onComplete(String key) {

    }

    /**
     * @param key 上传文件对应的key
     * @param e   error message
     */
    void onError(String key, SelfDefineThrowable e) {

    }

    /**
     * @param currentUploadedSize 已上传大小
     * @param totalSize           总大小
     */
    abstract void onUpdateProgress(long currentUploadedSize, long totalSize);

    /**
     * @param key 上传文件对应的key
     */
    void onSuccess(String key) {

    }
}
