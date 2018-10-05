package com.qingyun.zhiyunelu.ds.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {RecordEntity.class}, version = 1)
public abstract class EluDatabase extends RoomDatabase {
    public abstract RecordDao records();
}
