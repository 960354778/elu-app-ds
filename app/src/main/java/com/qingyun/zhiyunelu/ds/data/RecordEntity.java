package com.qingyun.zhiyunelu.ds.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.Expose;

import java.util.Calendar;

import velites.android.utility.db.Converters;
import velites.java.utility.misc.DateTimeUtil;

@Entity(tableName = "records")
@TypeConverters({Converters.CalendarAsLong.class, RecordEntity.Status.class})
public class RecordEntity {
    public enum Status {
        Initial, Connecting, Connected, Finished, Uploaded;

        @TypeConverter
        public static Status fromStatus(String value) {
            return value == null ? null : Status.valueOf(value);
        }

        @TypeConverter
        public static String toStatus(Status s) {
            return s == null ? null : s.name();
        }
    }

    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "task_record_id", index = true)
    public String taskRecordId;
    @ColumnInfo(name = "execution_time", index = true)
    public Calendar executionTime;
    @ColumnInfo(name = "phone_id")
    public String phoneId;
    @ColumnInfo(name = "phone_number", index = true)
    public String phoneNumber;
    @ColumnInfo(name = "is_incoming", index = true)
    public boolean isIncoming;
    @ColumnInfo(name = "status", index = true)
    public Status status;
    @ColumnInfo(name = "file_name", index = true)
    public String fileName;
    @ColumnInfo(name = "error", typeAffinity = ColumnInfo.TEXT)
    @Expose(serialize = false)
    public String error;
}
