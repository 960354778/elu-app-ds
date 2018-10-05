package com.qingyun.zhiyunelu.ds.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.Calendar;

import velites.android.utility.db.Converters;

@Dao
@TypeConverters({Converters.CalendarAsLong.class, RecordEntity.Status.class})
public interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void save(RecordEntity... records);
    @Query("SELECT * FROM records WHERE id = :id")
    public RecordEntity fetchById(String id);
    @Query("SELECT * FROM records WHERE phone_number = :number AND status IN (:statuses) AND execution_time >= :from LIMIT 1")
    public RecordEntity fetchLatestByNumberAndStatus(String number, RecordEntity.Status[] statuses, Calendar from);
}
