package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luohongzhen on 09/02/2018.
 */

public class WxMyInfo implements Parcelable {
    private String nickName;
    private String phone;
    private String userName;


    @Override
    public int describeContents() {
        return 0;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickName);
        dest.writeString(this.phone);
        dest.writeString(this.userName);
    }

    public WxMyInfo() {
    }

    protected WxMyInfo(Parcel in) {
        this.nickName = in.readString();
        this.phone = in.readString();
        this.userName = in.readString();
    }

    public static final Parcelable.Creator<WxMyInfo> CREATOR = new Parcelable.Creator<WxMyInfo>() {
        @Override
        public WxMyInfo createFromParcel(Parcel source) {
            return new WxMyInfo(source);
        }

        @Override
        public WxMyInfo[] newArray(int size) {
            return new WxMyInfo[size];
        }
    };

    @Override
    public String toString() {
        return "WxMyInfo{" +
                "nickName='" + nickName + '\'' +
                ", phone='" + phone + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
