package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luohongzhen on 23/02/2018.
 */

public class SmsMsgInfo implements Parcelable {

    private List<SmsMsgInfo> contacts;

    private String phoneTo;
    private String lastChatTime;
    private String phoneFrom;
    private String lastUpdateTime;

    private long timestamp;

    private SmsMsgInfo data;

    private List<SmsMsgInfo> smsChats;
    private List<SmsMsgInfo> chats;

    private String msgId;
    private boolean isSend;
    private String createTime;
    private String content;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.contacts);
        dest.writeString(this.phoneTo);
        dest.writeString(this.lastChatTime);
        dest.writeString(this.phoneFrom);
        dest.writeString(this.lastUpdateTime);
        dest.writeLong(this.timestamp);
        dest.writeParcelable(this.data, flags);
        dest.writeList(this.smsChats);
        dest.writeList(this.chats);
        dest.writeString(this.msgId);
        dest.writeByte(this.isSend ? (byte) 1 : (byte) 0);
        dest.writeString(this.createTime);
        dest.writeString(this.content);
    }

    public List<SmsMsgInfo> getContacts() {
        return contacts;
    }

    public void setContacts(List<SmsMsgInfo> contacts) {
        this.contacts = contacts;
    }

    public String getPhoneTo() {
        return phoneTo;
    }

    public void setPhoneTo(String phoneTo) {
        this.phoneTo = phoneTo;
    }

    public String getLastChatTime() {
        return lastChatTime;
    }

    public void setLastChatTime(String lastChatTime) {
        this.lastChatTime = lastChatTime;
    }

    public String getPhoneFrom() {
        return phoneFrom;
    }

    public void setPhoneFrom(String phoneFrom) {
        this.phoneFrom = phoneFrom;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public SmsMsgInfo getData() {
        return data;
    }

    public void setData(SmsMsgInfo data) {
        this.data = data;
    }

    public List<SmsMsgInfo> getSmsChats() {
        return smsChats;
    }

    public void setSmsChats(List<SmsMsgInfo> smsChats) {
        this.smsChats = smsChats;
    }

    public List<SmsMsgInfo> getChats() {
        return chats;
    }

    public void setChats(List<SmsMsgInfo> chats) {
        this.chats = chats;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SmsMsgInfo() {
    }

    protected SmsMsgInfo(Parcel in) {
        this.contacts = new ArrayList<SmsMsgInfo>();
        in.readList(this.contacts, SmsMsgInfo.class.getClassLoader());
        this.phoneTo = in.readString();
        this.lastChatTime = in.readString();
        this.phoneFrom = in.readString();
        this.lastUpdateTime = in.readString();
        this.timestamp = in.readLong();
        this.data = in.readParcelable(SmsMsgInfo.class.getClassLoader());
        this.smsChats = new ArrayList<SmsMsgInfo>();
        in.readList(this.smsChats, SmsMsgInfo.class.getClassLoader());
        this.chats = new ArrayList<SmsMsgInfo>();
        in.readList(this.chats, SmsMsgInfo.class.getClassLoader());
        this.msgId = in.readString();
        this.isSend = in.readByte() != 0;
        this.createTime = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<SmsMsgInfo> CREATOR = new Parcelable.Creator<SmsMsgInfo>() {
        @Override
        public SmsMsgInfo createFromParcel(Parcel source) {
            return new SmsMsgInfo(source);
        }

        @Override
        public SmsMsgInfo[] newArray(int size) {
            return new SmsMsgInfo[size];
        }
    };

    @Override
    public String toString() {
        return "SmsMsgInfo{" +
                "contacts=" + contacts +
                ", phoneTo='" + phoneTo + '\'' +
                ", lastChatTime='" + lastChatTime + '\'' +
                ", phoneFrom='" + phoneFrom + '\'' +
                ", lastUpdateTime=" + lastUpdateTime +
                ", timestamp=" + timestamp +
                ", data=" + data +
                ", smsChats=" + smsChats +
                ", chats=" + chats +
                ", msgId='" + msgId + '\'' +
                ", isSend=" + isSend +
                ", createTime='" + createTime + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
