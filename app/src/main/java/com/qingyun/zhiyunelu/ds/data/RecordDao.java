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
    long[] saveRaw(RecordEntity... records);
    @Query("SELECT * FROM records WHERE id = :id")
    RecordEntity fetchById(String id);
    @Query("SELECT * FROM records WHERE phone_number = :number AND status IN (:statuses) AND execution_time >= :from ORDER BY execution_time DESC LIMIT 1")
    RecordEntity fetchLatestByNumberAndStatus(String number, RecordEntity.Status[] statuses, Calendar from);
    @Query("SELECT * FROM records WHERE error IS NOT NULL AND status NOT IN (:statusesExclusion) ORDER BY execution_time DESC")
    RecordEntity[] fetchErrors(RecordEntity.Status[] statusesExclusion);
    @Query("SELECT * FROM records WHERE file_name = :fileName ORDER BY execution_time DESC LIMIT 1")
    RecordEntity fetchByFileName(String fileName);

    default void save(RecordEntity... records) {
        long[] ids = saveRaw(records);
        for (int i = 0; i < ids.length; i++) {
            records[i].id = ids[i];
        }
    }
}
