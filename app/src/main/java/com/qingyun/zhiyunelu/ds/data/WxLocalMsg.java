package com.qingyun.zhiyunelu.ds.data;

import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 05/12/2017.
 */

public class WxLocalMsg {
    private String alias;
    private String conRemark;
    private String content;
    private long createTime;
    private int id;
    private int isSend;
    private long modifyTime;
    private long msgId;
    private long msgsvrid;
    private String nickname;
    private int status;
    private String username;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getConRemark() {
        return conRemark;
    }

    public void setConRemark(String conRemark) {
        this.conRemark = conRemark;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsSend() {
        return isSend;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getMsgsvrid() {
        return msgsvrid;
    }

    public void setMsgsvrid(long msgsvrid) {
        this.msgsvrid = msgsvrid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static WxLocalMsg buildMsgFromWxDb(net.sqlcipher.Cursor cursor) {
        int msgId = cursor.getInt(cursor.getColumnIndex("msgId"));
        long msgsvrid = cursor.getLong(cursor.getColumnIndex("msgSvrId"));
        long createTime = cursor.getLong(cursor.getColumnIndex("createTime"));
        int status = cursor.getInt(cursor.getColumnIndex("status"));
        String content = cursor.getString(cursor.getColumnIndex("content"));
        if (StringUtil.isNullOrEmpty(content)) {
            content = "";
        }
        String username = cursor.getString(cursor.getColumnIndex("username"));
        if (StringUtil.isNullOrEmpty(username)) {
            username = "";
        }
        String alias = cursor.getString(cursor.getColumnIndex("alias"));
        if (StringUtil.isNullOrEmpty(alias)) {
            alias = "";
        }
        String conRemark = cursor.getString(cursor.getColumnIndex("conRemark"));
        if (StringUtil.isNullOrEmpty(conRemark)) {
            conRemark = "";
        }
        String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
        if (StringUtil.isNullOrEmpty(nickname)) {
            nickname = "";
        }
        WxLocalMsg message = new WxLocalMsg();
        message.setAlias(alias);
        message.setUsername(username);
        message.setConRemark(conRemark);
        message.setNickname(nickname);
        message.setMsgId((long) msgId);
        message.setContent(content);
        message.setCreateTime(createTime);
        message.setStatus(status);
        message.setMsgsvrid(msgsvrid);
        return message;
    }

    @Override
    public String toString() {
        return "WxLocalMsg{" +
                "alias='" + alias + '\'' +
                ", conRemark='" + conRemark + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", id=" + id +
                ", isSend=" + isSend +
                ", modifyTime=" + modifyTime +
                ", msgId=" + msgId +
                ", msgsvrid=" + msgsvrid +
                ", nickname='" + nickname + '\'' +
                ", status=" + status +
                ", username='" + username + '\'' +
                '}';
    }
}
