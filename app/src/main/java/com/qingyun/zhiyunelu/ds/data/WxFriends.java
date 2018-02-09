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
    private String nickName;
    private String conRemark;
    private String conRemarkPy;
    private boolean isDeleted;
    private String lastUpdateTime;


    private List<WxFriends> friends;

    private WxFriends data;
    private long timestamp;


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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.nickName);
        dest.writeString(this.conRemark);
        dest.writeString(this.conRemarkPy);
        dest.writeByte(this.isDeleted ? (byte) 1 : (byte) 0);
        dest.writeList(this.friends);
        dest.writeParcelable(this.data, flags);
        dest.writeLong(this.timestamp);
        dest.writeString(this.lastUpdateTime);
    }

    public WxFriends() {
    }

    protected WxFriends(Parcel in) {
        this.userName = in.readString();
        this.nickName = in.readString();
        this.conRemark = in.readString();
        this.conRemarkPy = in.readString();
        this.isDeleted = in.readByte() != 0;
        this.friends = new ArrayList<WxFriends>();
        in.readList(this.friends, WxFriends.class.getClassLoader());
        this.data = in.readParcelable(WxFriends.class.getClassLoader());
        this.timestamp = in.readLong();
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

        String alias = cursor.getString(cursor.getColumnIndex("username"));
        String conRemark = cursor.getString(cursor.getColumnIndex("conRemark"));
        String nickName = cursor.getString(cursor.getColumnIndex("nickname"));
        String conRemarkPYFull = cursor.getString(cursor.getColumnIndex("conRemarkPYFull"));
        WxFriends friend = new WxFriends();
        friend.setComRemark(conRemark);
        friend.setNickName(nickName);
        friend.setUserName(alias);
        friend.setComRemarkPy(conRemarkPYFull);
        return friend;
    }

    @Override
    public String toString() {
        return "WxFriends{" +
                "userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", comRemark='" + conRemark + '\'' +
                ", comRemarkPy='" + conRemarkPy + '\'' +
                ", isDeleted=" + isDeleted +
                ", friends=" + friends +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}
