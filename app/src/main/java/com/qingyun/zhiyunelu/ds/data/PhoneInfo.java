package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import velites.java.utility.misc.StringUtil;

/**
 * Created by regis on 2018/3/27.
 */

public class PhoneInfo implements Parcelable {

    private String phoneID;
    private String phoneSource;
    private String areaCode;
    private String number;
    private String extension;

    public String getPhoneID() {
        return phoneID;
    }

    public String getPhoneSource() {
        return phoneSource;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getNumber() {
        return number;
    }

    public String getExtension() {
        return extension;
    }

    public String getCallableNumber() {
        return StringUtil.emptyIfNull(areaCode) + number;
    }

    public String getDisplayNumber() {
        return (StringUtil.isNullOrSpace(areaCode) ? "" : areaCode + "-") + number + (StringUtil.isNullOrSpace(extension) ? "" : "*" + extension);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phoneID);
        dest.writeString(this.phoneSource);
        dest.writeString(this.areaCode);
        dest.writeString(this.number);
        dest.writeString(this.extension);
    }

    protected PhoneInfo(Parcel in) {
        this.phoneID = in.readString();
        this.phoneSource = in.readString();
        this.areaCode = in.readString();
        this.number = in.readString();
        this.extension = in.readString();
    }


    public static final Parcelable.Creator<PhoneInfo> CREATOR = new Parcelable.Creator<PhoneInfo>() {
        @Override
        public PhoneInfo createFromParcel(Parcel source) {
            return new PhoneInfo(source);
        }

        @Override
        public PhoneInfo[] newArray(int size) {
            return new PhoneInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
