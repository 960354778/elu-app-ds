package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luohongzhen on 09/02/2018.
 */

public class WxFriends implements Parcelable {
    private String userName;
    private String userAlias;
    private String nickName;
    private String conRemark;
    private String conRemarkPy;
    private long type;
    private String lastChatTime;
    private String lastUpdateTime;
    private List<WxFriends> friends;
    private WxFriends data;
    private String phone;


    public String getUserName() {
        return userName;
    }

    public WxFriends getData() {
        return data;
    }

    public void setData(WxFriends data) {
        this.data = data;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getComRemark() {
        return conRemark;
    }

    public void setComRemark(String conRemark) {
        this.conRemark = conRemark;
    }

    public String getComRemarkPy() {
        return conRemarkPy;
    }

    public void setComRemarkPy(String conRemarkPy) {
        this.conRemarkPy = conRemarkPy;
    }

    public List<WxFriends> getFriends() {
        return friends;
    }

    public void setFriends(List<WxFriends> friends) {
        this.friends = friends;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getLastChatTime() {
        return lastChatTime;
    }

    public void setLastChatTime(String lastChatTime) {
        this.lastChatTime = lastChatTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.userAlias);
        dest.writeString(this.nickName);
        dest.writeString(this.conRemark);
        dest.writeString(this.conRemarkPy);
        dest.writeString(this.phone);
        dest.writeLong(this.type);
        dest.writeList(this.friends);
        dest.writeParcelable(this.data, flags);
        dest.writeString(this.lastChatTime);
        dest.writeString(this.lastUpdateTime);
    }

    public WxFriends() {
    }

    protected WxFriends(Parcel in) {
        this.userName = in.readString();
        this.userAlias = in.readString();
        this.nickName = in.readString();
        this.conRemark = in.readString();
        this.conRemarkPy = in.readString();
        this.phone = in.readString();
        this.type = in.readInt();
        this.friends = new ArrayList<WxFriends>();
        in.readList(this.friends, WxFriends.class.getClassLoader());
        this.data = in.readParcelable(WxFriends.class.getClassLoader());
        this.lastChatTime = in.readString();
        this.lastUpdateTime = in.readString();
    }

    public static final Parcelable.Creator<WxFriends> CREATOR = new Parcelable.Creator<WxFriends>() {
        @Override
        public WxFriends createFromParcel(Parcel source) {
            return new WxFriends(source);
        }

        @Override
        public WxFriends[] newArray(int size) {
            return new WxFriends[size];
        }
    };

    public static WxFriends buildFriendsFromDatabase(net.sqlcipher.Cursor cursor) {
        if (cursor == null)
            return null;

        String name = cursor.getString(cursor.getColumnIndex("username"));
        String alias = cursor.getString(cursor.getColumnIndex("alias"));
        String conRemark = cursor.getString(cursor.getColumnIndex("conRemark"));
        String nickName = cursor.getString(cursor.getColumnIndex("nickname"));
        String conRemarkPYFull = cursor.getString(cursor.getColumnIndex("conRemarkPYFull"));
        String lastChatTime = cursor.getString(cursor.getColumnIndex("lastChatTime"));
        int type = cursor.getInt(cursor.getColumnIndex("type"));
        WxFriends friend = new WxFriends();
        friend.setComRemark(conRemark);
        friend.setNickName(nickName);
        friend.setUserName(name);
        friend.setUserAlias(alias);
        friend.setType(type);
        friend.setComRemarkPy(conRemarkPYFull);
        friend.setLastChatTime(lastChatTime);
        return friend;
    }

    @Override
    public String toString() {
        return "WxFriends{" +
                "userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", comRemark='" + conRemark + '\'' +
                ", comRemarkPy='" + conRemarkPy + '\'' +
                ", type=" + type +
                ", friends=" + friends +
                ", data=" + data +
                '}';
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
