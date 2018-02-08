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
    private OrderInfo doctorTasks;
    private List<OrderInfo> list;
    private List<String> phones;
    private PageInfo page;
    private String doctorName;
    private String hospitalName;
    private String departmentName;
    private String representativeName;
    private String brandName;
    private String endDate;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String taskId;
    private String taskCode;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timestamp);
        dest.writeParcelable(this.data, flags);
        dest.writeParcelable(this.doctorTasks, flags);
        dest.writeList(this.list);
        dest.writeList(this.phones);
        dest.writeParcelable(this.page, flags);
        dest.writeString(this.doctorName);
        dest.writeString(this.hospitalName);
        dest.writeString(this.departmentName);
        dest.writeString(this.brandName);
        dest.writeString(this.representativeName);
        dest.writeString(this.endDate);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeString(this.districtName);
        dest.writeString(this.taskId);
        dest.writeString(this.taskCode);
    }

    public OrderInfo() {
    }

    protected OrderInfo(Parcel in) {
        this.timestamp = in.readLong();
        this.data = in.readParcelable(OrderInfo.class.getClassLoader());
        this.doctorTasks = in.readParcelable(OrderInfo.class.getClassLoader());
        this.list = new ArrayList<OrderInfo>();
        in.readList(this.list, OrderInfo.class.getClassLoader());
        this.phones = new ArrayList<>();
        in.readList(this.phones, String.class.getClassLoader());
        this.page = in.readParcelable(PageInfo.class.getClassLoader());
        this.doctorName = in.readString();
        this.hospitalName = in.readString();
        this.departmentName = in.readString();
        this.brandName = in.readString();
        this.representativeName = in.readString();
        this.endDate = in.readString();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.districtName = in.readString();
        this.taskId = in.readString();
        this.taskCode = in.readString();
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

    public OrderInfo getDoctorTasks() {
        return doctorTasks;
    }

    public List<OrderInfo> getList() {
        return list;
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

    public List<String> getPhones() {
        return phones;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "timestamp=" + timestamp +
                ", taskId=" + taskId +
                ", taskCode=" + taskCode +
                ", data=" + data +
                ", list=" + list +
                ", phones=" + phones +
                ", page=" + page +
                ", doctorName='" + doctorName + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", representativeName='" + representativeName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", endDate='" + endDate + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", districtName='" + districtName + '\'' +
                '}';
    }
}