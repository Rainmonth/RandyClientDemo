package com.randy.randyclient.cache;

import java.io.Serializable;

/**
 * 数据库缓存信息类
 * Created by RandyZhang on 2017/4/13.
 */

public class DbCacheInfo implements Serializable {
    // 缓存地址
    private String cacheUrl;
    // 缓存的时间
    private long cacheTime;
    // 缓存的内容
    private String cacheContent;

    public DbCacheInfo(String cacheUrl, long cacheTime, String cacheContent) {
        this.cacheUrl = cacheUrl;
        this.cacheTime = cacheTime;
        this.cacheContent = cacheContent;
    }

    public String getCacheUrl() {
        return cacheUrl;
    }

    public void setCacheUrl(String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public String getCacheContent() {
        return cacheContent;
    }

    public void setCacheContent(String cacheContent) {
        this.cacheContent = cacheContent;
    }
}
