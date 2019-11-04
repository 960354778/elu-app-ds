package com.qingyun.zhiyunelu.ds.op;


import android.support.annotation.Nullable;

import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.CheckRecordAudiosDto;
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
import com.qingyun.zhiyunelu.ds.data.picture;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface AsyncApiService {

    @POST("MobileUser/Login")
    Observable<ApiResult> login(@Body LoginDto body);

    @GET("MobileUser/Logout")
    Observable<ApiResult> logout();

    @POST("MobileSync/UploadWechatFriends")
    Observable<ApiResult<SyncWechatFriendsResult>> syncWechatFriends(@Body SyncWechatFriendsDto friends);

    @POST("MobileSync/UploadWechatChats")
    Observable<ApiResult> syncWechatMessages(@Body SyncWechatMessagesDto messages);

    @GET("MobileSync/ObtainDialingInformation")
    Observable<ApiResult<TaskMessage>> obtainDialingInformation();

    @POST("MobileTask/RecordCalledOut")
    Observable<ApiResult<RecordInfo>> recordCalledOut(@Body RecordCalledOutDto params);

    @Multipart
    @POST("MobileTask/UploadAudioToRecord")
    Observable<ApiResult<RecordInfo>> uploadSoundRecord(@Query("taskRecordId") String taskRecordId, @Query("hash") String hash, @Query("duration") String duration, @Part MultipartBody.Part file);

    @POST("MobileTask/CheckRecordAudios")
    Observable<ApiResult<PendingSoundRecordInfo[]>> checkRecordAudios(@Body CheckRecordAudiosDto files);

    @POST("MobileSync/UploadSmsContacts")
    Observable<ApiResult<SyncSmsContactsResult>> syncSmsContacts(@Body SyncSmsContactsDto contacts);

    @POST("MobileSync/UploadSmsChats")
    Observable<ApiResult> syncSmsMessages(@Body SyncSmsMessagesDto messages);

    /**
     * 图片上传封装
     * */
    @Multipart
    @POST("api/index/upload")
    Observable<ApiResult<picture>> uploadImages(@Query("account") String account, @Query("userName") String userName,@Query("mobile") String mobile, @Part List<MultipartBody.Part> image);

}
