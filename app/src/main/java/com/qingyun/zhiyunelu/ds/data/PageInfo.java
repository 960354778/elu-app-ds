package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luohongzhen on 15/12/2017.
 */

public class PageInfo implements Parcelable {
    private int totalCount;
    private int pageNumber;
    private int pageSize;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.totalCount);
        dest.writeInt(this.pageNumber);
        dest.writeInt(this.pageSize);
    }

    public PageInfo() {
    }

    public PageInfo(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    protected PageInfo(Parcel in) {
        this.totalCount = in.readInt();
        this.pageNumber = in.readInt();
        this.pageSize = in.readInt();
    }

    public static final Parcelable.Creator<PageInfo> CREATOR = new Parcelable.Creator<PageInfo>() {
        @Override
        public PageInfo createFromParcel(Parcel source) {
            return new PageInfo(source);
        }

        @Override
        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }
}
