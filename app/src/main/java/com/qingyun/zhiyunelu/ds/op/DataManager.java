package com.qingyun.zhiyunelu.ds.op;

import android.arch.persistence.room.Room;

import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.data.EluDatabase;

public class DataManager {
    private final App.Assistant assistant;
    private final EluDatabase db;

    public DataManager(App.Assistant assistant) {
        this.assistant = assistant;
        this.db = Room.databaseBuilder(this.assistant.getDefaultContext(), EluDatabase.class, "elu.db").fallbackToDestructiveMigration().build();
    }

    public EluDatabase getDb() {
        return db;
    }
}
