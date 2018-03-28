package com.qingyun.zhiyunelu.ds.net;


import com.qingyun.zhiyunelu.ds.data.LoginInfo;
import com.qingyun.zhiyunelu.ds.data.OrderInfo;
import com.qingyun.zhiyunelu.ds.data.PendingSoundRecordInfo;
import com.qingyun.zhiyunelu.ds.data.RecordInfo;
import com.qingyun.zhiyunelu.ds.data.SmsMsgInfo;
import com.qingyun.zhiyunelu.ds.data.WxFriends;
import com.qingyun.zhiyunelu.ds.data.WxLocalMsg;

import java.util.List;
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

    @POST("MobileUser/Login")
    Observable<LoginInfo> login(@Body LoginInfo.LoginRequest body);

    @POST("MobileTask/DoctorTasksAllReps")
    Observable<OrderInfo> getMyDoctersList(@Header("token") String token, @Body Map<String, Object> params );

    @POST("MobileTask/RecordCalledOut")
    Observable<RecordInfo> recordCalledOut(@Header("token") String token, @Body RecordInfo.RecordRequestBody params);

    @Multipart
    @POST("MobileTask/UploadAudioToRecord")
    Observable<RecordInfo> uploadRecord(@Header("token") String token, @Query("taskRecordId") String taskRecordId, @Query("hash") String hash, @Part MultipartBody.Part file, @Query("duration") String time);

    @POST("MobileTask/CheckAudioToRecords")
    Observable<List<PendingSoundRecordInfo>> checkAudioToRecords(@Header("token") String token, @Body String[] files);

    @POST("MobileSync/UploadWechatFriends")
    Observable<WxFriends> uploadWxFriedns(@Header("token") String token, @Body WxFriends friends);

    @POST("MobileSync/UploadWechatChats")
    Observable<WxLocalMsg> upLoadWxMsg(@Header("token") String token, @Body WxLocalMsg msg);

    @POST("MobileSync/UploadSmsContacts")
    Observable<SmsMsgInfo> upLoadSmsContacts(@Header("token") String token, @Body SmsMsgInfo contacts);

    @POST("MobileSync/UploadSmsChats")
    Observable<SmsMsgInfo> upLoadSmsChat(@Header("token") String token, @Body SmsMsgInfo msg);


}
