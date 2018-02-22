package com.qingyun.zhiyunelu.ds.sms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;
import android.util.Log;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;

import velites.android.utility.utils.HandlerUtil;
import velites.android.utility.utils.SystemUtil;
import velites.java.utility.generic.Action1;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 22/02/2018.
 */

public class SmsManager {

    private final static String TAG = SmsManager.class.getSimpleName();

    private static HandlerUtil handlerUtil;
    private static final int MSG_TAG_UPLOAD_SMS = 3001;

    private static Uri uri = Uri.parse("content://sms/");
    private static String[] projection = new String[]{"_id", "thread_id", "address", "body", "date"};


    public static void initSms(){
        if(handlerUtil != null){
            handlerUtil.releaseData();
            handlerUtil = null;
        }
        handlerUtil = HandlerUtil.create(false, TAG, new Action1<Message>() {
            @Override
            public void a(Message arg1) {
                handlerUtil.removeMessages(MSG_TAG_UPLOAD_SMS);
                handlerUtil.sendEmptyMessageDelayed(MSG_TAG_UPLOAD_SMS, Constants.UPLOAD_SMS_CYCLE);
            }
        });

        handlerUtil.sendEmptyMessageDelayed(MSG_TAG_UPLOAD_SMS, Constants.UPLOAD_SMS_CYCLE);
        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, TAG, "init sms manager"));
    }

    public static boolean isGetMySelfPhoneNum(Context ctx){
        String curPhoneNum = AppAssistant.getPrefs().getStr(Constants.PrefsKey.MYSELF_PHONE_NUM);
        String myPhoneNum = SystemUtil.getMyselfPhone(ctx);
        if(!StringUtil.isNullOrEmpty(myPhoneNum)){
            AppAssistant.getPrefs().setStr(Constants.PrefsKey.MYSELF_PHONE_NUM, myPhoneNum);
            return true;
        }else{
            return !StringUtil.isNullOrEmpty(curPhoneNum);
        }
    }

    public static void uploadSmsList() {
        ContentResolver contentResolver = AppAssistant.getDefaultContext().getContentResolver();
        Cursor c = contentResolver.query(uri, projection,"0=0) GROUP BY (address",null,"date asc");
        if(c != null){
            while (c.moveToNext()){
                String _id = c.getString(c.getColumnIndex("_id"));
//                String thread_id = c.getString(c.getColumnIndex("thread_id"));
                String address = c.getString(c.getColumnIndex("address"));
//                String body = c.getString(c.getColumnIndex("body"));
                String date = c.getString(c.getColumnIndex("date"));

            }
            c.close();
        }
        uploadSmsMsg("15312160057", "0");
    }

    private static void uploadSmsMsg(String addressTmp, String dateTmp){
        ContentResolver contentResolver = AppAssistant.getDefaultContext().getContentResolver();
        String[] selectionArgs = new String[]{addressTmp, dateTmp};
        Cursor c = contentResolver.query(uri, projection, "address=? AND date >=?", selectionArgs, "date asc");
        if(c != null){
            while (c.moveToNext()){
                String _id = c.getString(c.getColumnIndex("_id"));
                String thread_id = c.getString(c.getColumnIndex("thread_id"));
                String phone = c.getString(c.getColumnIndex("address"));
                String body = c.getString(c.getColumnIndex("body"));
                String date = c.getString(c.getColumnIndex("date"));
                Log.e("YYD", "phone:"+phone+" body:"+body+" date:"+date);
            }
            c.close();
        }
    }
}
