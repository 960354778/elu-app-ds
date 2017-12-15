package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luohongzhen on 15/12/2017.
 */

public class PageInfo implements Parcelable {
    private int pageCounts;
    private int pageIndex;
    private int pageSize;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pageCounts);
        dest.writeInt(this.pageIndex);
        dest.writeInt(this.pageSize);
    }

    public PageInfo() {
    }

    protected PageInfo(Parcel in) {
        this.pageCounts = in.readInt();
        this.pageIndex = in.readInt();
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

    public int getPageCounts() {
        return pageCounts;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }
}
