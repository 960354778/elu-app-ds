package com.qingyun.zhiyunelu.ds.usilt.PhoneUsilt;

import android.media.MediaPlayer;

import com.orhanobut.logger.Logger;
import com.qingyun.zhiyunelu.ds.ui.MainActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ================================================

 * 创建日期：2019/11/4 16:37
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class PhoneUtil {

    /**
    * 音频时长
    * */
    public String getAudioFileVoiceTime(String filePath){
        long mediaPlayerDuration = 0L;
        String  Duration = String.valueOf(mediaPlayerDuration);
        if (filePath == null || filePath.isEmpty()) {
            return Duration;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayerDuration = mediaPlayer.getDuration();
        }catch (IOException e){
            Logger.e( e.getMessage());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        int musicTime = (int) (mediaPlayerDuration / 1000);
        if( musicTime / 60 == 0){
            Duration =  musicTime % 60+"秒";
        }else {
            Duration = musicTime / 60 + "分" + musicTime % 60;
        }
        return Duration;
    }


    /**
     * 文件日期
     * */
    public String getData(String images)  {
        String timeMillis = "";
        if (images == null || images.isEmpty()) {
            return "";
        }
        try {
            File f = new File(images);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(f.lastModified());
            timeMillis =  simpleDateFormat.format(date);
        }catch (Exception e){
            Logger.e( e.getMessage());
        }
        //照片类型
        return  timeMillis;
    }
}
