package com.qingyun.zhiyunelu.ds.db.Phonedb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

/**
 * ================================================
 * 创建日期：2019/11/4 14:07
 * 描    述：数据库执行语句
 * 修订历史：
 * ================================================
 */
@Dao
public interface PhocneDao {

    /**
     * 增
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PhocneEntity... phocneEntities);

    /**
     * 查询录音文件
     * */
    @Query("SELECT * FROM Phocne")
    Flowable<List<PhocneEntity>> getPhocne();

    /**
     * 查询录音文件
     * */
    /*@Query("SELECT * FROM Phocne")
    List<PhocneEntity> getPhocne();*/


    /**
    * 删全部
    * */
    @Query("DELETE FROM Phocne")
    void deleteAll();

}

