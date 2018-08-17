package com.qingyun.zhiyunelu.ds.op;

import com.google.gson.Gson;
import com.qingyun.zhiyunelu.ds.data.Setting;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import velites.android.utility.utils.NetUtil;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.ExceptionUtil;

public class ApiService {
    private final Gson gson;
    private final Setting.Network setting;
    private final IApiService defaultIApi;

    public ApiService(Gson gson, Setting.Network setting) {
        ExceptionUtil.assertArgumentNotNull(gson, "gson");
        ExceptionUtil.assertArgumentNotNull(setting, "setting");
        this.gson = gson;
        this.setting = setting;
        this.defaultIApi = this.createApi(this.setting.apiRootUrl);
    }

    public OkHttpClient initClient() {
        HttpLoggingInterceptor.Logger customLogger = new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_DEBUG, ApiService.class, message));
            }
        };
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(customLogger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okb = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .sslSocketFactory(NetUtil.createTrustAllSSLSocketFactory(), new NetUtil.TrustAllCerts())
                .hostnameVerifier(new NetUtil.TrustAllHostnameVerifier());
        if (this.setting != null && this.setting.connectTimeoutMs != null) {
            okb = okb.connectTimeout(this.setting.connectTimeoutMs, TimeUnit.MILLISECONDS);
        }
        if (this.setting != null && this.setting.readTimeoutMs != null) {
            okb = okb.connectTimeout(this.setting.readTimeoutMs, TimeUnit.MILLISECONDS);
        }
        if (this.setting != null && this.setting.writeTimeoutMs != null) {
            okb = okb.connectTimeout(this.setting.writeTimeoutMs, TimeUnit.MILLISECONDS);
        }
        OkHttpClient client = okb.build();
        return client;
    }

    public IApiService createApi(String url) {
        return new Retrofit.Builder().client(initClient()).baseUrl(url).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(IApiService.class);
    }

    public abstract static class ApiObserver<T> implements Observer<T> {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public final void onNext(T t) {
            onSuccess(t);
        }

        @Override
        public final void onError(Throwable e) {
            onFail(e);
        }

        @Override
        public void onComplete() {

        }

        public abstract void onSuccess(T t);

        public abstract void onFail(Throwable e);
    }
}
