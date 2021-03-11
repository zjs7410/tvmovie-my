package com.pj567.movie.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.pj567.movie.cache.Cache;
import com.pj567.movie.cache.CacheDao;
import com.pj567.movie.cache.VodRecord;
import com.pj567.movie.cache.VodRecordDao;


/**
 * 类描述:
 *
 * @author pj567
 * @since 2020/5/15
 */
@Database(entities = {Cache.class, VodRecord.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract CacheDao getCacheDao();

    public abstract VodRecordDao getVodRecordDao();
}
