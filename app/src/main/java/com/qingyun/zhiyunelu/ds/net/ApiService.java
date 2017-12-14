package com.qingyun.zhiyunelu.ds.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.LoginInfo;

import io.reactivex.Observable;
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
                .build();
        return client;
    }

    private IApiService createJsonApi(String url) {
        return new Retrofit.Builder().client(initClient()).baseUrl(url).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(IApiService.class);
    }

    public Observable<LoginInfo> login(LoginInfo.LoginRequest body) {
            return createJsonApi(Constants.BASE_URL).login(body);
    }

}
