package com.qingyun.zhiyunelu.ds.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.LoginInfo;
import com.qingyun.zhiyunelu.ds.data.OrderInfo;
import com.qingyun.zhiyunelu.ds.data.RecordInfo;
import com.qingyun.zhiyunelu.ds.data.WxFriends;
import com.qingyun.zhiyunelu.ds.data.WxLocalMsg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;

/**
 * Created by luohongzhen on 14/12/2017.
 */

public class ApiService {

    private Gson gson;
    public ApiService() {
        if(gson == null){
            gson = new GsonBuilder().create();
        }
    }

    private OkHttpClient initClient() {
        HttpLoggingInterceptor.Logger customLogger = new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, "", "api msg: %s", message));
            }
        };
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(customLogger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .connectTimeout(Constants.Network.TIMEOUT_AMOUNT, Constants.Network.TIMEOUT_UNIT)
                .readTimeout(Constants.Network.TIMEOUT_AMOUNT, Constants.Network.TIMEOUT_UNIT)
                .writeTimeout(Constants.Network.TIMEOUT_AMOUNT, Constants.Network.TIMEOUT_UNIT)
                .build();
        return client;
    }

    private IApiService createJsonApi(String url) {
        return new Retrofit.Builder().client(initClient()).baseUrl(url).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(IApiService.class);
    }

    public Observable<LoginInfo> login(LoginInfo.LoginRequest body) {
            return createJsonApi(Constants.BASE_URL).login(body);
    }

    public Observable<OrderInfo> getOrderList(int request, Map<String, Object> params){
        params.put("pageSize", Constants.PAGE_SIZE);
        String token = AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY);
        try{
            switch (request){
                case Constants.Codes.REQUEST_NET_MY_DOCTER_LIST_TAG:
                    return createJsonApi(Constants.BASE_URL).getMyDoctersList(token, params);
                case Constants.Codes.REQUEST_NET_MY_HOSPITAL_LIST_TAG:
                    return createJsonApi(Constants.BASE_URL).getMyHospitalList(token, params);
                case Constants.Codes.REQUEST_NET_DOCTER_LIST_TAG:
                    return createJsonApi(Constants.BASE_URL).getDoctersList(token, params);
                case Constants.Codes.REQUEST_NET_HOSPITAL_LIST_TAG:
                    return createJsonApi(Constants.BASE_URL).getHospitalList(token, params);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public Observable<RecordInfo> recordCalledOut(RecordInfo.RecordRequestBody body){
        String token = AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY);
        return createJsonApi(Constants.BASE_URL).recordCalledOut(token,body);
    }

    public Observable<RecordInfo> upLoadRecord(String params,String sha1, MultipartBody.Part file , String time){
        String token = AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY);
        return createJsonApi(Constants.BASE_URL).upLoadRecord(token, params,sha1, file, time);
    }

    public Observable<WxFriends> uploadFriends(List<WxFriends> friends, String usrName, String nickName, String phone){
        String token = AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY);
        WxFriends tmpFriend = new WxFriends();
        tmpFriend.setFriends(friends);
        tmpFriend.setUserName(usrName);
        tmpFriend.setNickName(nickName);
        tmpFriend.setPhone(phone);
        return createJsonApi(Constants.BASE_URL).upLoadWxFriedns(token, tmpFriend);
    }

    public Observable<WxLocalMsg> uploadWxMsg(WxLocalMsg msg){
        String token = AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY);
        return createJsonApi(Constants.BASE_URL).upLoadMsg(token, msg);
    }

}
