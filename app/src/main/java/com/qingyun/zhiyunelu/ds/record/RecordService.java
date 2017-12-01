package com.qingyun.zhiyunelu.ds.record;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.qingyun.zhiyunelu.ds.AppAssistant;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;

/**
 * Created by luohongzhen on 01/12/2017.
 */

public class RecordService extends Service{
    private static final String RECORD_SERVICE_ACTION = "com.qingyun.zhiyunelu.ds.record.RecordService";

    public static void startRS(int state, int type, String phoneNum){
        Intent intent = new Intent();
        intent.setClassName(AppAssistant.getDefaultContext().getPackageName(), RECORD_SERVICE_ACTION);
        intent.putExtra("state",state);
        intent.putExtra("type",type);
        intent.putExtra("phoneNum",phoneNum);
        AppAssistant.getDefaultContext().startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_VERBOSE, this, "init service"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            int state = intent.getIntExtra("state", -1);
            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_VERBOSE, this, "onStartCommand state:%d type:%d phonenum:%s", intent.getIntExtra("state", -1), intent.getIntExtra("type", -1), intent.getStringExtra("phoneNum")));
            if(state == TelephonyManager.CALL_STATE_OFFHOOK){
                RecordTest.startRecord(intent.getStringExtra("phoneNum"));
                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_VERBOSE, this, "start record audio for  phonenum:%s", intent.getStringExtra("phoneNum")));
            }else if(state == TelephonyManager.CALL_STATE_IDLE){
                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_VERBOSE, this, "stop record audio for  phonenum:%s", intent.getStringExtra("phoneNum")));
                RecordTest.stopRecord();
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
