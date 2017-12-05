package com.qingyun.zhiyunelu.ds.wechat;


import android.content.Context;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.WxLocalMsg;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import velites.android.support.wx.WechatHelper;
import velites.java.utility.generic.Action3;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;

/**
 * Created by luohongzhen on 03/12/2017.
 */

public class WxManager {

    public static void test(Context context) {

        WechatHelper.checkWechatAndRun(context, AppAssistant.getDefaultContext().getCacheDir().getPath() + "/%s/" + Constants.FilePaths.WX_MS_DB_NAME, Constants.FilePaths.WX_SHARE_PREFS_PATH, Constants.FilePaths.WX_MICROMS_PATH, Constants.FilePaths.WX_MS_DB_NAME, new Action3<Boolean, String, net.sqlcipher.database.SQLiteDatabase>() {
            @Override
            public void a(Boolean isOk, String msg, SQLiteDatabase sql) {
                if (isOk && sql != null) {
                    List<WxLocalMsg> list = new ArrayList();
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("SELECT m.msgId,m.msgSvrId,m.createTime,m.status,m.content,r.username,r.alias,r.conRemark,r.nickname").append(" FROM message m INNER JOIN rcontact r").append(" ON m.talker=r.username").append(" WHERE m.content IS NOT NULL").append(" AND r.verifyFlag=0").append(" AND r.nickname!=''").append(" AND r.type!=33").append(" AND r.type!=35").append(" ORDER BY").append(" m.createTime ASC").append(" LIMIT ?");
                    Cursor c1 = sql.rawQuery(buffer.toString(), new String[]{String.valueOf(100)});
                    while (c1.moveToNext()) {
                        list.add(WxLocalMsg.buildMsgFromWxDb(c1));
                    }
                    c1.close();
                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, WxManager.class, "list size:%d",list.size()));
                }
            }
        });


    }
}
