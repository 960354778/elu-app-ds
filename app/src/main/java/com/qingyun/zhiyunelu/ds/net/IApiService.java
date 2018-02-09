package com.qingyun.zhiyunelu.ds.net;


import com.qingyun.zhiyunelu.ds.data.LoginInfo;
import com.qingyun.zhiyunelu.ds.data.OrderInfo;
import com.qingyun.zhiyunelu.ds.data.RecordInfo;
import com.qingyun.zhiyunelu.ds.data.WxFriends;
import com.qingyun.zhiyunelu.ds.data.WxLocalMsg;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by luohongzhen on 14/12/2017.
 */

public interface IApiService {

    @POST("user/login")
    Observable<LoginInfo> login(@Body LoginInfo.LoginRequest body);

    @GET("MyTask/Load")
    Observable<OrderInfo> getMyTasks(@Header("token") String token);

    @POST("MyTask/DoctorTasksAllReps")
    Observable<OrderInfo> getMyDoctersList(@Header("token") String token, @Body Map<String, Object> params );

    @POST("MyTask/HospitalTasks")
    Observable<OrderInfo> getMyHospitalList(@Header("token") String token,@Body Map<String, Object> params);

    @POST("Task/GetDoctorTaskList")
    Observable<OrderInfo> getDoctersList(@Header("token") String token, @Body Map<String, Object> params);

    @POST("Task/GetHospitalTaskList")
    Observable<OrderInfo> getHospitalList(@Header("token") String token, @Body Map<String, Object> params);

    @POST("TaskDetail/RecordCalledOut")
    Observable<RecordInfo> recordCalledOut(@Header("token") String token, @Body RecordInfo.RecordRequestBody params);

    @Multipart
    @POST("TaskDetail/UploadAudioToRecord")
    Observable<RecordInfo> upLoadRecord(@Header("token") String token, @Query("taskRecordId") String taskRecordId,@Query("hash") String hash, @Part MultipartBody.Part file, @Query("duration") String time);

    @POST("WeChatChat/UploadFriends")
    Observable<WxFriends> upLoadWxFriedns(@Header("token") String token, @Body WxFriends friends);

    @POST("WeChatChat/UploadChat")
    Observable<WxLocalMsg> upLoadMsg(@Header("token") String token, @Body WxLocalMsg msg);


}
