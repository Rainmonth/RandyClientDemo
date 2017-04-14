package com.randy.randyclient.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.randy.randyclient.cache.DbCacheInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DB_CACHE_INFO".
*/
public class DbCacheInfoDao extends AbstractDao<DbCacheInfo, Long> {

    public static final String TABLENAME = "DB_CACHE_INFO";

    /**
     * Properties of entity DbCacheInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CacheUrl = new Property(1, String.class, "cacheUrl", false, "CACHE_URL");
        public final static Property CacheTime = new Property(2, long.class, "cacheTime", false, "CACHE_TIME");
        public final static Property CacheContent = new Property(3, String.class, "cacheContent", false, "CACHE_CONTENT");
    }


    public DbCacheInfoDao(DaoConfig config) {
        super(config);
    }
    
    public DbCacheInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DB_CACHE_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"CACHE_URL\" TEXT," + // 1: cacheUrl
                "\"CACHE_TIME\" INTEGER NOT NULL ," + // 2: cacheTime
                "\"CACHE_CONTENT\" TEXT);"); // 3: cacheContent
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DB_CACHE_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DbCacheInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String cacheUrl = entity.getCacheUrl();
        if (cacheUrl != null) {
            stmt.bindString(2, cacheUrl);
        }
        stmt.bindLong(3, entity.getCacheTime());
 
        String cacheContent = entity.getCacheContent();
        if (cacheContent != null) {
            stmt.bindString(4, cacheContent);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DbCacheInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String cacheUrl = entity.getCacheUrl();
        if (cacheUrl != null) {
            stmt.bindString(2, cacheUrl);
        }
        stmt.bindLong(3, entity.getCacheTime());
 
        String cacheContent = entity.getCacheContent();
        if (cacheContent != null) {
            stmt.bindString(4, cacheContent);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DbCacheInfo readEntity(Cursor cursor, int offset) {
        DbCacheInfo entity = new DbCacheInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // cacheUrl
            cursor.getLong(offset + 2), // cacheTime
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // cacheContent
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DbCacheInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCacheUrl(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCacheTime(cursor.getLong(offset + 2));
        entity.setCacheContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DbCacheInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DbCacheInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DbCacheInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
