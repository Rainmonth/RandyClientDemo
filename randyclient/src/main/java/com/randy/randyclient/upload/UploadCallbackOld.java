package com.randy.randyclient.upload;

import com.randy.randyclient.exception.SelfDefineThrowable;

/**
 * 上传文件回调
 * Created by RandyZhang on 2017/4/12.
 */

@Deprecated
public abstract class UploadCallbackOld {
    /**
     * 上传开始
     *
     * @param key 上传任务对应的key
     */
    public void onStart(String key) {

    }

    /**
     * 上传进度
     *
     * @param key           上传任务对应的key
     * @param bytesUploaded 已上传bytes
     * @param totalBytes    总共bytes
     */
    public void onProgress(String key, int bytesUploaded, int totalBytes) {

    }

    /**
     * 上传完成
     *
     * @param key 上传任务对应的key
     */
    public void onComplete(String key) {

    }

    /**
     * 上传成功
     *
     * @param key 上传任务对应的key
     */
    public void onSuccess(String key) {

    }

    /**
     * 上传出错
     *
     * @param key                 上传任务对应的key
     * @param selfDefineThrowable 自定义异常
     */
    public void onError(String key, SelfDefineThrowable selfDefineThrowable) {

    }
}
