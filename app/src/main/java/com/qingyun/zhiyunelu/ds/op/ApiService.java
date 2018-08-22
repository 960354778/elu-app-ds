package com.qingyun.zhiyunelu.ds.op;

import android.os.HandlerThread;

import com.google.gson.Gson;
import com.qingyun.zhiyunelu.ds.data.Setting;
import com.qingyun.zhiyunelu.ds.data.TokenInfo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import velites.android.utility.helpers.NetHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.ObjectAccessor;
import velites.java.utility.misc.StringUtil;

public class ApiService {
    private final Gson gson;
    private final Setting.Network setting;
    private final ObjectAccessor<String> serializedTokenAccessor;
    private final HandlerThread miscThread;

    private final IApiService defaultApi;

    public IApiService getDefaultApi() {
        return defaultApi;
    }
    private TokenInfo token;
    public TokenInfo getToken() {
        return token;
    }
    private Subject<Boolean> tokenChanged;
    public Observable<Boolean> getTokenChanged() {
        return tokenChanged;
    }

    public ApiService(Gson gson, Setting.Network setting, HandlerThread miscThread, ObjectAccessor<String> serializedTokenAccessor) {
        ExceptionUtil.assertArgumentNotNull(gson, "gson");
        ExceptionUtil.assertArgumentNotNull(setting, "setting");
        ExceptionUtil.assertArgumentNotNull(miscThread, "miscThread");
        this.gson = gson;
        this.setting = setting;
        this.miscThread = miscThread;
        this.serializedTokenAccessor = serializedTokenAccessor;
        this.defaultApi = this.createApi(this.setting.apiRootUrl);
        this.initToken();
    }

    private void initToken() {
        if (this.serializedTokenAccessor != null) {
            String str = this.serializedTokenAccessor.get();
            if (!StringUtil.isNullOrEmpty(str)) {
                this.token = gson.fromJson(str, TokenInfo.class);
            }
        }
        tokenChanged = PublishSubject.create();
    }

    private void updateToken(TokenInfo token) {
        this.token = token;
        if (this.serializedTokenAccessor != null) {
            this.serializedTokenAccessor.Set(token == null ? null : gson.toJson(token));
        }
        tokenChanged.onNext(token != null);
    }

    public OkHttpClient createClient() {
        HttpLoggingInterceptor.Logger customLogger = new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, ApiService.class, message));
            }
        };
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(customLogger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okb = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .sslSocketFactory(NetHelper.createTrustAllSSLSocketFactory(), new NetHelper.TrustAllCerts())
                .hostnameVerifier(new NetHelper.TrustAllHostnameVerifier());
        if (this.setting != null && this.setting.connectTimeoutMs != null) {
            okb = okb.connectTimeout(this.setting.connectTimeoutMs, TimeUnit.MILLISECONDS);
        }
        if (this.setting != null && this.setting.readTimeoutMs != null) {
            okb = okb.readTimeout(this.setting.readTimeoutMs, TimeUnit.MILLISECONDS);
        }
        if (this.setting != null && this.setting.writeTimeoutMs != null) {
            okb = okb.writeTimeout(this.setting.writeTimeoutMs, TimeUnit.MILLISECONDS);
        }
        OkHttpClient client = okb.build();
        return client;
    }

    public IApiService createApi(String url) {
        return new Retrofit.Builder().client(createClient()).baseUrl(url).addConverterFactory(GsonConverterFactory.create(gson)).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(IApiService.class);
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
