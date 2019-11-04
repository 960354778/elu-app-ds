package com.qingyun.zhiyunelu.ds.usilt;

import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* 照片文件信息类
* */
public class Photo {


    public Photo(){}

    /**
    * 照片类型
    * */
    public String getPhotoType(String images){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//在解码范围
        BitmapFactory.decodeFile(images, options);
        //照片类型
        String photoType = options.outMimeType;
        return  photoType;
    }

    /**
     * 照片大小
     * */
    public String getPhotoSize(String images) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//在解码范围
        BitmapFactory.decodeFile(images, options);//破解文件
        File f = new File(images);
        FileInputStream fis = new FileInputStream(f);
        //照片大小
        float size = fis.available()/1000;
        String photoSize = size+"KB";
        return  photoSize;
    }

    /**
     * 照片名
     * */
    public String getName(String images)  {
        File f = new File(images);
        //照片类型
        return  f.getName();
    }

    /**
     * 照片时间
     * */
    public String getData(String images)  {
        File f = new File(images);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(f.lastModified());
        String timeMillis =  simpleDateFormat.format(date);
        //照片类型
        return  timeMillis;
    }


}
