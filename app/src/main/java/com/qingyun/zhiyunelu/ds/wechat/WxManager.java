package com.qingyun.zhiyunelu.ds.wechat;


import android.content.Context;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import velites.android.support.wx.WechatHelper;
import velites.java.utility.generic.Action3;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;

/**
 * Created by luohongzhen on 03/12/2017.
 */

public class WxManager {

    public static void test(Context context){

        WechatHelper.checkWechatAndRun(context, AppAssistant.getDefaultContext().getCacheDir().getPath() + "/%s/" + Constants.FilePaths.WX_MS_DB_NAME, Constants.FilePaths.WX_SHARE_PREFS_PATH, Constants.FilePaths.WX_MICROMS_PATH, Constants.FilePaths.WX_MS_DB_NAME, new Action3<Boolean, String, net.sqlcipher.database.SQLiteDatabase>() {
            @Override
            public void a(Boolean isOk, String msg, SQLiteDatabase sql) {
                if(isOk && sql != null){
                    Cursor c1 = sql.rawQuery("select * from rcontact where verifyFlag = 0 and type != 4 and type != 2 and nickname != '' limit 20, 9999", null);
                    while (c1.moveToNext()) {
                        String userName = c1.getString(c1.getColumnIndex("username"));
                        String alias = c1.getString(c1.getColumnIndex("alias"));
                        String nickName = c1.getString(c1.getColumnIndex("nickname"));
                        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, WxManager.class, "userName:%s alias:%s nickName:%s", userName, alias, nickName));
                    }
                    c1.close();
                    sql.close();
                }
            }
        });


    }
}
