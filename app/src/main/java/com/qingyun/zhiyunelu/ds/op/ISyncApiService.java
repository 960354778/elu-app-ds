package com.qingyun.zhiyunelu.ds.op;


import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.LoginDto;
import com.qingyun.zhiyunelu.ds.data.RecordCalledOutDto;
import com.qingyun.zhiyunelu.ds.data.RecordInfo;
import com.qingyun.zhiyunelu.ds.data.SyncWechatFriendsDto;
import com.qingyun.zhiyunelu.ds.data.SyncWechatFriendsResult;
import com.qingyun.zhiyunelu.ds.data.SyncWechatMessagesDto;
import com.qingyun.zhiyunelu.ds.data.TaskMessage;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ISyncApiService {

    @POST("MobileUser/Login")
    ApiResult login(@Body LoginDto body);

    @GET("MobileUser/Logout")
    ApiResult logout();

    @POST("MobileSync/UploadWechatFriends")
    ApiResult<SyncWechatFriendsResult> syncWechatFriends(@Body SyncWechatFriendsDto friends);

    @POST("MobileSync/UploadWechatChats")
    ApiResult syncWechatMessages(@Body SyncWechatMessagesDto messages);

    @GET("MobileSync/ObtainDialingInformation")
    ApiResult<TaskMessage> obtainDialingInformation();

    @POST("MobileTask/RecordCalledOut")
    ApiResult<RecordInfo> recordCalledOut(@Body RecordCalledOutDto params);

    @Multipart
    @POST("MobileTask/UploadAudioToRecord")
    ApiResult<RecordInfo> uploadRecord(@Query("taskRecordId") String taskRecordId, @Query("hash") String hash, @Query("duration") String duration, @Part MultipartBody.Part file);

//    @POST("MobileTask/CheckAudioToRecords")
//    Observable<ResultWrapper<PendingSoundRecordInfo[]>> checkAudioToRecords(@Header("token") String token, @Body String[] files);
//
//    @POST("MobileSync/UploadSmsContacts")
//    Observable<SmsMsgInfo> upLoadSmsContacts(@Header("token") String token, @Body SmsMsgInfo contacts);
//
//    @POST("MobileSync/UploadSmsChats")
//    Observable<SmsMsgInfo> upLoadSmsChat(@Header("token") String token, @Body SmsMsgInfo msg);
}
