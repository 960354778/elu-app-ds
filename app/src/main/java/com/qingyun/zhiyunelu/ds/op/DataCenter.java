package com.qingyun.zhiyunelu.ds.op;

import android.arch.persistence.room.Room;

import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.data.EluDatabase;
import com.qingyun.zhiyunelu.ds.db.Phonedb.PhocneDatabase;

public class DataCenter {
    private final App.Assistant assistant;
    private final EluDatabase db;

    private final PhocneDatabase phocneDatabase;

    public DataCenter(App.Assistant assistant) {
        this.assistant = assistant;

        this.db = Room.databaseBuilder(this.assistant.getDefaultContext(), EluDatabase.class, "elu.db").build();

        this.phocneDatabase = Room.databaseBuilder(this.assistant.getDefaultContext(), PhocneDatabase.class, "elu.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
    }

    public EluDatabase getDb() {
        return db;
    }

    public PhocneDatabase getPhocneDatabase() {
        return phocneDatabase;
    }
}
