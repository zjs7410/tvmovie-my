package com.pj567.movie.cache;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * @author pj567
 * @date :2021/1/7
 * @description:
 */
@Dao
public interface VodRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(VodRecord record);

    @Query("select * from vodRecord where `apiUrl`=:apiUrl order by updateTime desc")
    List<VodRecord> getAll(String apiUrl);

    @Query("select *from vodRecord where `apiUrl`=:apiUrl and `vodId`=:vodId")
    VodRecord getVodRecord(String apiUrl, int vodId);

    @Delete
    int delete(VodRecord record);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(VodRecord record);
}