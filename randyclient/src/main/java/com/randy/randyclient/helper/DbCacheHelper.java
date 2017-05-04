package com.randy.randyclient.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.randy.randyclient.cache.DbCacheInfo;
import com.randy.randyclient.greendao.gen.DaoMaster;
import com.randy.randyclient.greendao.gen.DaoSession;
import com.randy.randyclient.greendao.gen.DbCacheInfoDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 数据库缓存帮助类
 * 主要是GreenDao的使用
 * Created by RandyZhang on 2017/4/13.
 */

public class DbCacheHelper {
    private static DbCacheHelper mInstance;
    private final static String dbName = "db_cache_db";
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    private DbCacheHelper() {
        context = RxRetrofitApp.getApplication();
        if (null == openHelper)
            openHelper = new DaoMaster.DevOpenHelper(context, dbName);
    }

    public static DbCacheHelper getInstance() {
        if (mInstance == null) {
            synchronized (DbCacheHelper.class) {
                if (mInstance == null) {
                    mInstance = new DbCacheHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    /**
     * add
     *
     * @param dbCacheInfo DbCacheInfo object
     */
    public void saveDbCacheInfo(DbCacheInfo dbCacheInfo) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DbCacheInfoDao dbCacheInfoDao = daoSession.getDbCacheInfoDao();
        dbCacheInfoDao.insert(dbCacheInfo);
    }

    /**
     * delete
     *
     * @param dbCacheInfo DbCacheInfo object
     */
    public void deleteCookie(DbCacheInfo dbCacheInfo) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DbCacheInfoDao dbCacheInfoDao = daoSession.getDbCacheInfoDao();
        dbCacheInfoDao.delete(dbCacheInfo);
    }

    /**
     * update
     *
     * @param dbCacheInfo DbCacheInfo object
     */
    public void updateDbCacheInfo(DbCacheInfo dbCacheInfo) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DbCacheInfoDao dbCacheInfoDao = daoSession.getDbCacheInfoDao();
        dbCacheInfoDao.update(dbCacheInfo);
    }

    /**
     * query special one
     *
     * @param url he url of DbCacheInfo object
     * @return DbCacheInfo object
     */
    public DbCacheInfo queryDbCacheByUrl(String url) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DbCacheInfoDao dbCacheInfoDao = daoSession.getDbCacheInfoDao();
        QueryBuilder<DbCacheInfo> qb = dbCacheInfoDao.queryBuilder();
        qb.where(DbCacheInfoDao.Properties.CacheUrl.eq(url));
        List<DbCacheInfo> dbCacheInfoList = qb.list();
        if (dbCacheInfoList.isEmpty()) {
            return null;
        } else {
            return dbCacheInfoList.get(0);
        }
    }

    /**
     * query all
     *
     * @return List of DbCacheInfo object
     */
    public List<DbCacheInfo> queryDbCacheAll() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DbCacheInfoDao dbCacheInfoDao = daoSession.getDbCacheInfoDao();
        QueryBuilder<DbCacheInfo> qb = dbCacheInfoDao.queryBuilder();
        return qb.list();
    }

}
