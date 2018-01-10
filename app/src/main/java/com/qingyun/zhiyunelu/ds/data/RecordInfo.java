package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by luohongzhen on 10/01/2018.
 */

public class RecordInfo implements Parcelable {
    private long timestamp;
    private RecordInfo data;
    private String callStatus;
    private String operationUser;
    private String taskRecordId;
    private String taskId;
    private String execType;
    private String execDate;
    private String cooperationType;
    private String feedback;
    private String materialId;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestamp);
        dest.writeParcelable(this.data, flags);
        dest.writeString(this.callStatus);
        dest.writeString(this.operationUser);
        dest.writeString(this.taskRecordId);
        dest.writeString(this.taskId);
        dest.writeString(this.execType);
        dest.writeString(this.execDate);
        dest.writeString(this.cooperationType);
        dest.writeString(this.feedback);
        dest.writeString(this.materialId);
    }

    public RecordInfo() {
    }

    protected RecordInfo(Parcel in) {
        this.timestamp = in.readLong();
        this.data = in.readParcelable(RecordInfo.class.getClassLoader());
        this.callStatus = in.readString();
        this.operationUser = in.readString();
        this.taskRecordId = in.readString();
        this.taskId = in.readString();
        this.execType = in.readString();
        this.execDate = in.readString();
        this.cooperationType = in.readString();
        this.feedback = in.readString();
        this.materialId = in.readString();
    }

    public static final Parcelable.Creator<RecordInfo> CREATOR = new Parcelable.Creator<RecordInfo>() {
        @Override
        public RecordInfo createFromParcel(Parcel source) {
            return new RecordInfo(source);
        }

        @Override
        public RecordInfo[] newArray(int size) {
            return new RecordInfo[size];
        }
    };

    public long getTimestamp() {
        return timestamp;
    }

    public RecordInfo getData() {
        return data;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public String getOperationUser() {
        return operationUser;
    }

    public String getTaskRecordId() {
        return taskRecordId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getExecType() {
        return execType;
    }

    public String getExecDate() {
        return execDate;
    }

    public String getCooperationType() {
        return cooperationType;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getMaterialId() {
        return materialId;
    }

    public static class RecordRequestBody implements Parcelable {
        private String taskId;
        private String execDate;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.taskId);
            dest.writeString(this.execDate);
        }

        public RecordRequestBody() {
        }

        public RecordRequestBody(String taskId) {
            this.taskId = taskId;
            SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.execDate = dateformat1.format(new Date());
            this.execDate = execDate.replace(" ", "T");
        }

        protected RecordRequestBody(Parcel in) {
            this.taskId = in.readString();
            this.execDate = in.readString();
        }

        public static final Creator<RecordRequestBody> CREATOR = new Creator<RecordRequestBody>() {
            @Override
            public RecordRequestBody createFromParcel(Parcel source) {
                return new RecordRequestBody(source);
            }

            @Override
            public RecordRequestBody[] newArray(int size) {
                return new RecordRequestBody[size];
            }
        };
    }
}
