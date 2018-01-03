package com.qingyun.zhiyunelu.ds.net;


import com.qingyun.zhiyunelu.ds.data.LoginInfo;
import com.qingyun.zhiyunelu.ds.data.OrderInfo;
import com.qingyun.zhiyunelu.ds.data.PageInfo;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by luohongzhen on 14/12/2017.
 */

public interface IApiService {

    @POST("user/login")
    Observable<LoginInfo> login(@Body LoginInfo.LoginRequest body);

    @POST("MyTask/DoctorTasks")
    Observable<OrderInfo> getMyDoctersList(@Header("token") String token, @Body PageInfo params );

    @POST("MyTask/HospitalTasks")
    Observable<OrderInfo> getMyHospitalList(@Header("token") String token,@Body PageInfo params);

    @POST("Task/GetDoctorTaskList")
    Observable<OrderInfo> getDoctersList(@Header("token") String token, @Body PageInfo params);

    @POST("Task/GetHospitalTaskList")
    Observable<OrderInfo> getHospitalList(@Header("token") String token, @Body PageInfo params);

}
