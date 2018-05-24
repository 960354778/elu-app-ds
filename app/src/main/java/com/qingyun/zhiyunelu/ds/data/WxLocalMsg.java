package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luohongzhen on 05/12/2017.
 */

public class WxLocalMsg implements Parcelable {
    private String content;
    private long createTime;
    private boolean isSend;
    private long msgId;
    private Long msgSvrId;
    private long type;
    private long status;
    private String userName;
    private List<WxLocalMsg> userChats;
    private List<WxLocalMsg> chats;

    private String repUserName;

    public WxLocalMsg() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<WxLocalMsg> getUserChats() {
        return userChats;
    }

    public void setUserChats(List<WxLocalMsg> userChats) {
        this.userChats = userChats;
    }

    public List<WxLocalMsg> getChats() {
        return chats;
    }

    public void setChats(List<WxLocalMsg> chats) {
        this.chats = chats;
    }

    public String getRepUserName() {
        return repUserName;
    }

    public void setRepUserName(String repUserName) {
        this.repUserName = repUserName;
    }

    public static WxLocalMsg buildMsgFromWxDb(net.sqlcipher.Cursor cursor) {
        int msgId = cursor.getInt(cursor.getColumnIndex("msgId"));
        int c_msgSvrId = cursor.getColumnIndex("msgSvrId");
        Long msgSvrId = cursor.isNull(c_msgSvrId) ? null :cursor.getLong(c_msgSvrId);
        int status = cursor.getInt(cursor.getColumnIndex("status"));
        int type = cursor.getInt(cursor.getColumnIndex("type"));
        long createTime = cursor.getLong(cursor.getColumnIndex("createTime"));
        String content = cursor.getString(cursor.getColumnIndex("content"));
        int isSend = cursor.getInt(cursor.getColumnIndex("isSend"));
        WxLocalMsg message = new WxLocalMsg();
        message.setContent(content);
        message.setCreateTime(createTime);
        message.setSend(isSend == 1);
        message.setMsgId(msgId);
        message.setMsgSvrId(msgSvrId);
        message.setStatus(status);
        message.setType(type);
        return message;
    }

    @Override
    public String toString() {
        return "WxLocalMsg{" +
                "content='" + content + '\'' +
                ", createTime=" + createTime +
                ", isSend=" + isSend +
                ", msgId=" + msgId +
                ", userName='" + userName + '\'' +
                ", userChats=" + userChats +
                ", chats=" + chats +
                ", repUserName='" + repUserName + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeLong(this.createTime);
        dest.writeByte(this.isSend ? (byte) 1 : (byte) 0);
        dest.writeLong(this.msgId);
        dest.writeByte(this.msgSvrId == null ? (byte)0 : 1);
        if (this.msgSvrId != null) {
            dest.writeLong(this.msgSvrId);
        }
        dest.writeLong(this.status);
        dest.writeLong(this.type);
        dest.writeString(this.userName);
        dest.writeList(this.userChats);
        dest.writeList(this.chats);
        dest.writeString(this.repUserName);
    }

    protected WxLocalMsg(Parcel in) {
        this.content = in.readString();
        this.createTime = in.readLong();
        this.isSend = in.readByte() != 0;
        this.msgId = in.readLong();
        if (in.readByte() > 0) {
            this.msgSvrId = in.readLong();
        }
        this.status = in.readLong();
        this.type = in.readLong();
        this.userName = in.readString();
        this.userChats = new ArrayList<WxLocalMsg>();
        in.readList(this.userChats, WxLocalMsg.class.getClassLoader());
        this.chats = new ArrayList<WxLocalMsg>();
        in.readList(this.chats, WxLocalMsg.class.getClassLoader());
        this.repUserName = in.readString();
    }

    public static final Parcelable.Creator<WxLocalMsg> CREATOR = new Parcelable.Creator<WxLocalMsg>() {
        @Override
        public WxLocalMsg createFromParcel(Parcel source) {
            return new WxLocalMsg(source);
        }

        @Override
        public WxLocalMsg[] newArray(int size) {
            return new WxLocalMsg[size];
        }
    };

    public Long getMsgSvrId() {
        return msgSvrId;
    }

    public void setMsgSvrId(Long msgSvrId) {
        this.msgSvrId = msgSvrId;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }
}
