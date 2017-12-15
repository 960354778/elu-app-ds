package com.qingyun.zhiyunelu.ds.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luohongzhen on 15/12/2017.
 */

public class OrderInfo implements Parcelable {
    private long timestamp;
    private OrderInfo  data;
    private List<OrderInfo> taskList;
    private PageInfo page;

    private String doctorName;
    private String hospitalName;
    private String departmentName;
    private String brandName;
    private String endDate;
    private String provinceName;
    private String cityName;
    private String districtName;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestamp);
        dest.writeParcelable(this.data, flags);
        dest.writeList(this.taskList);
        dest.writeParcelable(this.page, flags);
        dest.writeString(this.doctorName);
        dest.writeString(this.hospitalName);
        dest.writeString(this.departmentName);
        dest.writeString(this.brandName);
        dest.writeString(this.endDate);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeString(this.districtName);
    }

    public OrderInfo() {
    }

    protected OrderInfo(Parcel in) {
        this.timestamp = in.readLong();
        this.data = in.readParcelable(OrderInfo.class.getClassLoader());
        this.taskList = new ArrayList<OrderInfo>();
        in.readList(this.taskList, OrderInfo.class.getClassLoader());
        this.page = in.readParcelable(PageInfo.class.getClassLoader());
        this.doctorName = in.readString();
        this.hospitalName = in.readString();
        this.departmentName = in.readString();
        this.brandName = in.readString();
        this.endDate = in.readString();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.districtName = in.readString();
    }

    public static final Parcelable.Creator<OrderInfo> CREATOR = new Parcelable.Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel source) {
            return new OrderInfo(source);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

    public long getTimestamp() {
        return timestamp;
    }

    public OrderInfo getData() {
        return data;
    }

    public List<OrderInfo> getTaskList() {
        return taskList;
    }

    public PageInfo getPage() {
        return page;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "timestamp=" + timestamp +
                ", data=" + data +
                ", taskList=" + taskList +
                ", page=" + page +
                ", doctorName='" + doctorName + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", endDate='" + endDate + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", districtName='" + districtName + '\'' +
                '}';
    }
}