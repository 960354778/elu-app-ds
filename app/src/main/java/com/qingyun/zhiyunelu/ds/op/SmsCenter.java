package com.qingyun.zhiyunelu.ds.op;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.SmsContactResult;
import com.qingyun.zhiyunelu.ds.data.SmsMessagesInfo;
import com.qingyun.zhiyunelu.ds.data.SyncSmsContactsDto;
import com.qingyun.zhiyunelu.ds.data.SyncSmsContactsResult;
import com.qingyun.zhiyunelu.ds.data.SyncSmsMessagesDto;

import java.util.ArrayList;
import java.util.List;

import velites.android.support.sms.SmsOperator;
import velites.android.utility.misc.SystemHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.SerializationUtil;
import velites.java.utility.misc.StringUtil;

public class SmsCenter {
    private final App.Assistant assistant;
    private final SmsOperator op;

    public SmsCenter(App.Assistant assistant) {
        this.assistant = assistant;
        this.op = new SmsOperator(this.assistant.getDefaultContext());
    }

    private String ensureMySelfPhone() {
        String num = assistant.getPrefs().getSelfPhone();
        if (StringUtil.isNullOrEmpty(num)) {
            String p = SystemHelper.getMyselfPhone(assistant.getDefaultContext());
            if(!StringUtil.isNullOrEmpty(p)){
                assistant.getPrefs().setSelfPhone(p);
                return p;
            }
        }
        return num;
    }

    public boolean checkMySelfPhoneSet(){
        return !StringUtil.isNullOrEmpty(ensureMySelfPhone());
    }

    public void syncSmsData() {
        SyncSmsContactsDto dtoContacts = new SyncSmsContactsDto();
        dtoContacts.phoneFrom = ensureMySelfPhone();
        if (StringUtil.isNullOrEmpty(dtoContacts.phoneFrom)) {
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Given up sync sms data due to no self phone provided"));
            return;
        }
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Starting sync sms data(self phone: %s)...", dtoContacts.phoneFrom));
        dtoContacts.contacts = op.fetchContacts();
        SmsContactResult[] contacts = assistant.getApi().createSyncApi().syncSmsContacts(dtoContacts).data.contacts;
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Processing wechat messages, self phone is: %s", dtoContacts.phoneFrom));
        int count = 0;
        List<SmsMessagesInfo> contactMessages = new ArrayList<>();
        for (int i = 0; i < contacts.length; i++) {
            SmsContactResult contact = contacts[i];
            SmsMessagesInfo messages = new SmsMessagesInfo();
            messages.chats = op.fetchMessages(contact.phoneTo, contact.lastUpdateTime, DateTimeUtil.nowTimestamp() - assistant.getSetting().logic.smsChatSyncReserveMs);
            if (messages.chats.length > 0) {
                messages.phoneTo = contact.phoneTo;
                contactMessages.add(messages);
                count += messages.chats.length;
            }
            if (count >= assistant.getSetting().logic.smsChatSyncCountThreshold || i == contacts.length - 1 && count > 0) {
                SyncSmsMessagesDto dto = new SyncSmsMessagesDto();
                dto.phoneFrom = dtoContacts.phoneFrom;
                dto.smsChats = contactMessages.toArray(new SmsMessagesInfo[0]);
                assistant.getApi().createSyncApi().syncSmsMessages(dto);
                count = 0;
                contactMessages.clear();
            }
        }
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Finished sync sms data(self phone: %s).", dtoContacts.phoneFrom));
    }
}
