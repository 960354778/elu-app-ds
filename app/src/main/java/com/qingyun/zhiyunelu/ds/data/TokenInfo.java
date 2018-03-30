package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by luohongzhen on 14/12/2017.
 */

public class TokenInfo implements Parcelable {
    private String value;
    private long expire;
    private String version;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
        dest.writeString(this.version);
        dest.writeLong(this.expire);
    }

    public TokenInfo() {
    }

    protected TokenInfo(Parcel in) {
        this.value = in.readString();
        this.version = in.readString();
        this.expire = in.readLong();

    }

    public static final Creator<TokenInfo> CREATOR = new Creator<TokenInfo>() {
        @Override
        public TokenInfo createFromParcel(Parcel source) {
            return new TokenInfo(source);
        }

        @Override
        public TokenInfo[] newArray(int size) {
            return new TokenInfo[size];
        }
    };

    public String getValue() {
        return value;
    }

    public String getVersion() {
        return version;
    }

    public long getExpire() {
        return expire;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                ", value='" + value + '\'' +
                ", version='" + version + '\'' +
                ", expire=" + expire +
                '}';
    }
}