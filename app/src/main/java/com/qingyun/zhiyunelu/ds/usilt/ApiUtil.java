package com.qingyun.zhiyunelu.ds.usilt;

import com.orhanobut.logger.Logger;
import com.qingyun.zhiyunelu.ds.op.AsyncApiService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtil {
    private static final String HOST = "http://zhijian.13.zyelu.com/";//换成你上传用的服务器地址
    private static Retrofit retrofit;
    private static final int DEFAULT_TIMEOUT = 10;//超时时长，单位：秒

    /**
     * 获取根服务地址
     */
    public static String getHOST() {
        return HOST;
    }



    private static Retrofit getApiRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor.Logger customLogger = message -> Logger.i( message);
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(customLogger);
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                    .addNetworkInterceptor(logInterceptor);
            okHttpBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .client(okHttpBuilder.build())
                    .baseUrl(HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }




}
