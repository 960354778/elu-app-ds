package com.qingyun.zhiyunelu.ds.net;


import com.qingyun.zhiyunelu.ds.data.LoginInfo;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by luohongzhen on 14/12/2017.
 */

public interface IApiService {

    @POST("user/login")
    Observable<LoginInfo> login(@Body LoginInfo.LoginRequest body);

}
