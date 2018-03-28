package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by regis on 2018/3/27.
 */

public class PendingSoundRecordInfo implements Parcelable {

    private String fileName;
    private String taskRecordId;

    public String getFileName() {
        return fileName;
    }

    public String getTaskRecordId() {
        return taskRecordId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeString(this.taskRecordId);
    }

    protected PendingSoundRecordInfo(Parcel in) {
        this.fileName = in.readString();
        this.taskRecordId = in.readString();
    }


    public static final Creator<PendingSoundRecordInfo> CREATOR = new Creator<PendingSoundRecordInfo>() {
        @Override
        public PendingSoundRecordInfo createFromParcel(Parcel source) {
            return new PendingSoundRecordInfo(source);
        }

        @Override
        public PendingSoundRecordInfo[] newArray(int size) {
            return new PendingSoundRecordInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
