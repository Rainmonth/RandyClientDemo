package com.randy.randyclient.cache;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 数据库缓存信息类
 * Created by RandyZhang on 2017/4/13.
 */

@Entity
public class DbCacheInfo {
    @Id
    private Long id;
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

    @Generated(hash = 1906140102)
    public DbCacheInfo(Long id, String cacheUrl, long cacheTime,
            String cacheContent) {
        this.id = id;
        this.cacheUrl = cacheUrl;
        this.cacheTime = cacheTime;
        this.cacheContent = cacheContent;
    }
    @Generated(hash = 1103566521)
    public DbCacheInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCacheUrl() {
        return this.cacheUrl;
    }
    public void setCacheUrl(String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }
    public long getCacheTime() {
        return this.cacheTime;
    }
    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }
    public String getCacheContent() {
        return this.cacheContent;
    }
    public void setCacheContent(String cacheContent) {
        this.cacheContent = cacheContent;
    }

}
