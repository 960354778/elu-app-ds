package com.qingyun.zhiyunelu.ds.op;


import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.LoginDto;
import com.qingyun.zhiyunelu.ds.data.PendingSoundRecordInfo;
import com.qingyun.zhiyunelu.ds.data.RecordCalledOutDto;
import com.qingyun.zhiyunelu.ds.data.RecordInfo;
import com.qingyun.zhiyunelu.ds.data.SyncSmsContactsDto;
import com.qingyun.zhiyunelu.ds.data.SyncSmsContactsResult;
import com.qingyun.zhiyunelu.ds.data.SyncSmsMessagesDto;
import com.qingyun.zhiyunelu.ds.data.SyncWechatFriendsDto;
import com.qingyun.zhiyunelu.ds.data.SyncWechatFriendsResult;
import com.qingyun.zhiyunelu.ds.data.SyncWechatMessagesDto;
import com.qingyun.zhiyunelu.ds.data.TaskMessage;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface SyncApiService {

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
    ApiResult<RecordInfo> uploadSoundRecord(@Query("taskRecordId") String taskRecordId, @Query("hash") String hash, @Query("duration") String duration, @Part MultipartBody.Part file);

    @POST("MobileTask/CheckAudioToRecords")
    ApiResult<PendingSoundRecordInfo[]> checkAudioToRecords(@Body String[] files);

    @POST("MobileSync/UploadSmsContacts")
    ApiResult<SyncSmsContactsResult> syncSmsContacts(@Body SyncSmsContactsDto contacts);

    @POST("MobileSync/UploadSmsChats")
    ApiResult syncSmsMessages(@Body SyncSmsMessagesDto messages);
}
