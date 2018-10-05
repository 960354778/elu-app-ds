package com.qingyun.zhiyunelu.ds.op;


import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.data.SyncWechatFriendsDto;
import com.qingyun.zhiyunelu.ds.data.SyncWechatMessagesDto;
import com.qingyun.zhiyunelu.ds.data.WechatFriendResult;
import com.qingyun.zhiyunelu.ds.data.WechatMessagesInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import velites.android.support.wx.WechatMeInfo;
import velites.android.support.wx.WechatOperator;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.PathUtil;
import velites.java.utility.misc.SerializationUtil;

public class WechatCenter {
    private final App.Assistant assistant;
    private final WechatOperator op;

    public WechatCenter(App.Assistant assistant) {
        this.assistant = assistant;
        this.op = new WechatOperator(this.assistant.getDefaultContext());
        this.op.fixPermission();
    }

    public void syncWxDatabase() {
        ExceptionUtil.executeWithRetry(() -> {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Starting sync wechat database..."));
            op.checkWechatAndRun(assistant.createWxTempDirPath(), sql -> {
                WechatMeInfo me = op.obtainMe(sql);
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Processing wechat friends, me is: %s", SerializationUtil.describe(me)));
                SyncWechatFriendsDto dtoFriends = new SyncWechatFriendsDto();
                dtoFriends.userName = me.userName;
                dtoFriends.nickName = me.nickName;
                dtoFriends.phone = me.phone;
                dtoFriends.friends = op.fetchFriends(sql);
                WechatFriendResult[] friends = assistant.getApi().createSyncApi().syncWechatFriends(dtoFriends).data.friends;
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Processing wechat messages, me is: %s", SerializationUtil.describe(me)));
                int count = 0;
                List<WechatMessagesInfo> friendMessages = new ArrayList<>();
                for (int i = 0; i < friends.length; i++) {
                    WechatFriendResult friend = friends[i];
                    WechatMessagesInfo messages = new WechatMessagesInfo();
                    messages.chats = op.fetchMessages(sql, friend.userName, friend.lastUpdateTime, DateTimeUtil.nowTimestamp() - assistant.getSetting().logic.wxChatSyncReserveMs);
                    if (messages.chats.length > 0) {
                        messages.userName = friend.userName;
                        friendMessages.add(messages);
                        count += messages.chats.length;
                    }
                    if (count >= assistant.getSetting().logic.wxChatSyncCountThreshold || i == friends.length - 1 && count > 0) {
                        SyncWechatMessagesDto dto = new SyncWechatMessagesDto();
                        dto.repUserName = me.userName;
                        dto.userChats = friendMessages.toArray(new WechatMessagesInfo[0]);
                        assistant.getApi().createSyncApi().syncWechatMessages(dto);
                        count = 0;
                        friendMessages.clear();
                    }
                }
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Finished sync wechat database."));
            });
        }, 1, null);
    }

    public void exportWxDatabase() {
        ExceptionUtil.executeWithRetry(() -> op.checkWechatAndRun(this.assistant.createWxTempDirPath(), sql -> WechatOperator.exportDecodedDB(sql, PathUtil.concat(assistant.getMiscDir().getPath(), this.assistant.getSetting().path.decryptedWxDbFileName))), 1, null);
    }
}
