package com.qingyun.zhiyunelu.ds.db.Phonedb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * ================================================
 * 创建日期：2019/11/4 11:51
 * 描    述：elu数据库 录音Phocne表的创建
 * 修订历史：
 * ================================================
 */
@Entity(tableName = "Phocne")
public class PhocneEntity {

    /**
     *  主键id 自增长
     */
    @PrimaryKey(autoGenerate = true)
    public long id;

    /**
     * 医生姓名
     * */
    @ColumnInfo(name = "compellation")
    public String compellation;

    /**
     * 医生电话号码 字符创建
     * */
    @ColumnInfo(name = "phone")
    public String phone;

    /**
    * 录音时长
    * */
    @ColumnInfo(name = "Duration")
    public String Duration;

    /**
     * 录音文件名
     * */
   /* @ColumnInfo(name = "name")
    public String name;*/

    /**
     * 录音日期时间
     * */
    @ColumnInfo(name = "data")
    public String data;

    /**
     * 判断录音是否上传
     * */
    @ColumnInfo(name = "code")
    public String code;
}
