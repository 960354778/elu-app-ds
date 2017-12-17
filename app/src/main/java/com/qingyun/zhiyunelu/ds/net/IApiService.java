package com.qingyun.zhiyunelu.ds.net;


import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.LoginInfo;
import com.qingyun.zhiyunelu.ds.data.OrderInfo;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by luohongzhen on 14/12/2017.
 */

public interface IApiService {

    @POST("user/login")
    Observable<LoginInfo> login(@Body LoginInfo.LoginRequest body);

    @GET("Task/GetDoctorTaskList?pageSize="+ Constants.PAGE_SIZE)
    Observable<OrderInfo> getMyDoctersList(@Header("token") String token, @QueryMap Map<String, String> params );

    @GET("MyTask/HospitalTasks?pageSize="+ Constants.PAGE_SIZE)
    Observable<OrderInfo> getMyHospitalList(@Header("token") String token,@QueryMap Map<String, String> params);

    @GET("Task/GetDoctorTaskList?pageSize="+ Constants.PAGE_SIZE)
    Observable<OrderInfo> getDoctersList(@Header("token") String token, @QueryMap Map<String, String> params);

    @GET("Task/GetHospitalTaskList?pageSize="+ Constants.PAGE_SIZE)
    Observable<OrderInfo> getHospitalList(@Header("token") String token, @QueryMap Map<String, String> params);

}
