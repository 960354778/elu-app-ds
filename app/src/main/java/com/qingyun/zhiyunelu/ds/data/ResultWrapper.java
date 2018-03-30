package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import velites.java.utility.misc.ExceptionUtil;

/**
 * Created by luohongzhen on 15/12/2017.
 */

public class ResultWrapper<TData> {
    private long timestamp;
    private TData  data;
    private ErrorInfo  error;
    private TokenInfo token;

    public long getTimestamp() {
        return timestamp;
    }

    public TData getData() {
        return data;
    }

    public ErrorInfo getError() {
        return error;
    }

    public TokenInfo getToken() {
        return token;
    }

    public ResultWrapper() {
    }

    protected ResultWrapper(Parcel in) {
        this.timestamp = in.readLong();
        this.token = in.readParcelable(TokenInfo.class.getClassLoader());
        this.error = in.readParcelable(ErrorInfo.class.getClassLoader());
        String dataClassName = in.readString();
        try {
            this.data = in.readParcelable(Class.forName(dataClassName).getClassLoader());
        } catch (ClassNotFoundException e) {
            ExceptionUtil.rethrowAsRuntime(e);
        }
    }
}