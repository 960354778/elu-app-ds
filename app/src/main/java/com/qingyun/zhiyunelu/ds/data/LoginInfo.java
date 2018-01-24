package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by luohongzhen on 14/12/2017.
 */

public class LoginInfo implements Parcelable {
    private long timestamp;
    private String displayName;
    private List<String> permissions;
    private LoginInfo token;
    private LoginInfo account;
    private String code;
    private String message;
    private String value;
    private long expire;
    private String loginName;




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestamp);
        dest.writeString(this.displayName);
        dest.writeStringList(this.permissions);
        dest.writeParcelable(this.token, flags);
        dest.writeParcelable(this.account, flags);
        dest.writeString(this.code);
        dest.writeString(this.message);
        dest.writeString(this.value);
        dest.writeLong(this.expire);
        dest.writeString(this.loginName);
    }

    public LoginInfo() {
    }

    protected LoginInfo(Parcel in) {
        this.timestamp = in.readLong();
        this.displayName = in.readString();
        this.permissions = in.createStringArrayList();
        this.token = in.readParcelable(LoginInfo.class.getClassLoader());
        this.account = in.readParcelable(LoginInfo.class.getClassLoader());
        this.code = in.readString();
        this.message = in.readString();
        this.value = in.readString();
        this.expire = in.readLong();
        this.loginName = in.readString();

    }

    public static final Parcelable.Creator<LoginInfo> CREATOR = new Parcelable.Creator<LoginInfo>() {
        @Override
        public LoginInfo createFromParcel(Parcel source) {
            return new LoginInfo(source);
        }

        @Override
        public LoginInfo[] newArray(int size) {
            return new LoginInfo[size];
        }
    };

    public long getTimestamp() {
        return timestamp;
    }


    public String getDisplayName() {
        return displayName;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public LoginInfo getToken() {
        return token;
    }


    public String getValue() {
        return value;
    }

    public long getExpire() {
        return expire;
    }

    public LoginInfo getAccount() {
        return account;
    }

    public String getLoginName() {
        return loginName;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "timestamp=" + timestamp +
                ", displayName='" + displayName + '\'' +
                ", permissions=" + permissions +
                ", token=" + token +
                ", account=" + account +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", value='" + value + '\'' +
                ", expire=" + expire +
                ", loginName='" + loginName + '\'' +
                '}';
    }

    public static class LoginRequest{
        private String loginName;
        private String password;

        public LoginRequest(String loginName, String password) {
            this.loginName = loginName;
            this.password = password;
        }
    }
}