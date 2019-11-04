package com.qingyun.zhiyunelu.ds.db.Phonedb;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.qingyun.zhiyunelu.ds.App;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * ================================================
 * 创建日期：2019/11/4 14:14
 * 描    述：执行语句
 * 修订历史：
 * ================================================
 */
public class StudentDao {

    private final App.Assistant assistant;


    public StudentDao(App.Assistant assistant) {
        this.assistant = assistant;
    }

    public static StudentDao getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final StudentDao sInstance = new StudentDao(getInstance().assistant);
    }

    public Flowable<List<PhocneEntity>> getPhocne(){
        return this.assistant.getData().getPhocneDatabase().phocneDao().getPhocne();
    }


    /*public void insert(PhocneEntity... phocneEntity) {
        getInstance().assistant.getData().getPhocneDatabase().phocneDao().insert(phocneEntity);
    }*/

    public Observable<Boolean> insert(PhocneEntity... phocneEntity) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Logger.e("StudentDao-------------------------");
                getInstance().assistant.getData().getPhocneDatabase().phocneDao().insert(phocneEntity);
                return true;
            }

        });
    }

}
