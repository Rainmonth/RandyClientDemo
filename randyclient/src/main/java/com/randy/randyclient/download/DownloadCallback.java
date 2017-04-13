package com.randy.randyclient.download;

import com.randy.randyclient.exception.SelfDefineThrowable;

/**
 * 下载文件回调
 * Created by RandyZhang on 2017/4/12.
 */

public abstract class DownloadCallback {
    public void onStart(String key) {
    }

    public void onCancel() {
    }

    public void onCompleted() {
    }


    /**
     * 错误处理
     * 需运行在UI线程
     *
     * @param e
     */
    abstract public void onError(SelfDefineThrowable e);

    public void onProgress(String key, long fileSizeDownloaded, long totalSize) {
    }

    public void onResponseProgress(long fileSizeDownloaded, long totalSize, boolean isAllRead) {

    }

    /**
     * 需运行在UI线程
     *
     * @param path
     * @param name
     * @param fileSize
     */
    abstract public void onSuccess(String key, String path, String name, long fileSize);

}
