package com.qingyun.zhiyunelu.ds.op;


import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.LoginDto;
import com.qingyun.zhiyunelu.ds.data.SyncWechatFriendsDto;
import com.qingyun.zhiyunelu.ds.data.SyncWechatFriendsResult;
import com.qingyun.zhiyunelu.ds.data.SyncWechatMessagesDto;
import com.qingyun.zhiyunelu.ds.data.TaskMessage;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IAsyncApiService {

    @POST("MobileUser/Login")
    Observable<ApiResult> login(@Body LoginDto body);

    @GET("MobileUser/Logout")
    Observable<ApiResult> logout();

    @POST("MobileSync/UploadWechatFriends")
    Observable<ApiResult<SyncWechatFriendsResult>> syncWechatFriends(@Body SyncWechatFriendsDto friends);

    @POST("MobileSync/UploadWechatChats")
    Observable<ApiResult> syncWechatMessages(@Body SyncWechatMessagesDto messages);

    @GET("MobileSync/ObtainLatestMessage")
    Observable<ApiResult<TaskMessage>> obtainLatestMessage(@Body SyncWechatMessagesDto messages);

//    @POST("MobileTask/DoctorTasksAllReps")
//    Observable<OrderInfo> getMyDoctersList(@Header("token") String token, @Body Map<String, Object> params );
//
//    @POST("MobileTask/RecordCalledOut")
//    Observable<RecordInfo> recordCalledOut(@Header("token") String token, @Body RecordInfo.RecordRequestBody params);
//
//    @Multipart
//    @POST("MobileTask/UploadAudioToRecord")
//    Observable<RecordInfo> uploadRecord(@Header("token") String token, @Query("taskRecordId") String taskRecordId, @Query("hash") String hash, @Part MultipartBody.Part file, @Query("duration") String time);
//
//    @POST("MobileTask/CheckAudioToRecords")
//    Observable<ResultWrapper<PendingSoundRecordInfo[]>> checkAudioToRecords(@Header("token") String token, @Body String[] files);
//
//    @POST("MobileSync/UploadSmsContacts")
//    Observable<SmsMsgInfo> upLoadSmsContacts(@Header("token") String token, @Body SmsMsgInfo contacts);
//
//    @POST("MobileSync/UploadSmsChats")
//    Observable<SmsMsgInfo> upLoadSmsChat(@Header("token") String token, @Body SmsMsgInfo msg);
}
