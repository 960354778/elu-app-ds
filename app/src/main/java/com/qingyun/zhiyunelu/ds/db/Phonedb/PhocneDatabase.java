package com.qingyun.zhiyunelu.ds.db.Phonedb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * ================================================
 * 创建日期：2019/11/4 14:07
 * 描    述：数据库的创建
 * 修订历史：
 * ================================================
 */
@Database( entities = {PhocneEntity.class},version = 4)
public abstract class PhocneDatabase extends RoomDatabase {
/*

    private static volatile PhocneDatabase instance;

    public static PhocneDatabase getDatabase(Context context){
        //判断是否创建类数据库
        if (instance == null){
            synchronized (PhocneDatabase.class){
                if (instance == null)
                    instance = Room.databaseBuilder(context.getApplicationContext(), PhocneDatabase.class, "elu.db").fallbackToDestructiveMigration().allowMainThreadQueries().build();
            }
        }
        return instance;
    }

*/

    public abstract PhocneDao phocneDao();

}

