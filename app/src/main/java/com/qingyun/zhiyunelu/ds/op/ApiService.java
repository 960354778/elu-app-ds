package com.qingyun.zhiyunelu.ds.op;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.text.TextUtils;

import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.ErrorInfo;
import com.qingyun.zhiyunelu.ds.data.Setting;
import com.qingyun.zhiyunelu.ds.data.SimpleItem;
import com.qingyun.zhiyunelu.ds.data.TokenInfo;
import com.qingyun.zhiyunelu.ds.ui.Popups;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import velites.android.utility.misc.NetHelper;
import velites.android.utility.misc.RxHelper;
import velites.android.utility.misc.ThreadHelper;
import velites.java.utility.ex.BusinessException;
import velites.java.utility.ex.CodedException;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.RxUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.thread.RunnableKeepingScope;

public class ApiService {
    private static final String REQUEST_HEADER_KEY_TOKEN = "token";
    private static final String REQUEST_HEADER_KEY_TOKEN_VERSION = "token_version";

    private final App.Assistant assitant;

    private Map<String, List<SimpleItem>> pocket = new ConcurrentHashMap<>();
    private TokenInfo token;
    public TokenInfo getToken() {
        return token;
    }
    private Subject<Boolean> tokenChanged;
    public Observable<Boolean> getTokenChanged() {
        return tokenChanged;
    }
    private int serverTimeAdvanced;
    public Calendar inferServerTime() {
        Calendar ret = DateTimeUtil.now();
        ret.add(Calendar.MILLISECOND, serverTimeAdvanced);
        return ret;
    }
    private void updateServerTime(Calendar serverTime) {
        if (serverTime != null) {
            serverTimeAdvanced = (int) (serverTime.getTimeInMillis() - new Date().getTime());
        }
    }

    private Disposable expireChecker;

    public ApiService(App.Assistant assitant) {
        this.assitant = assitant;
        this.initToken();
    }

    private void initToken() {
        String str = this.assitant.getPrefs().getSerializedToken();
        if (!StringUtil.isNullOrEmpty(str)) {
            this.token = this.assitant.getGson().fromJson(str, TokenInfo.class);
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Restored token from cache: %s", str));
        }
        tokenChanged = BehaviorSubject.createDefault(token != null);
    }

    private void updateToken(TokenInfo token) {
        TokenInfo originToken = this.token;
        this.token = token;
        String str = this.assitant.getGson().toJson(token);
        this.assitant.getPrefs().setSerializedToken(token == null ? null : str);
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Replaces token from %s to %s", this.assitant.getGson().toJson(originToken), str));
        this.checkStartExpireCheck();
        tokenChanged.onNext(token != null);
    }

    private void checkStartExpireCheck() {
        Observable.just(0)
                .observeOn(RxHelper.createKeepingScopeSingleSchedule())
                .subscribe(integer -> this.startExpireCheck(), RxUtil.simpleErrorConsumer);
    }

    private void startExpireCheck() {
        if (this.expireChecker != null) {
            this.expireChecker.dispose();
            this.expireChecker = null;
        }
        if (this.token != null && this.token.expire != null) {
            Observable.just(0)
                    .delay(this.token.expire.getTimeInMillis() - inferServerTime().getTime().getTime(), TimeUnit.MILLISECONDS)
                    .observeOn(RxHelper.createKeepingScopeSingleSchedule())
                    .subscribe(integer -> this.clearToken(), RxUtil.simpleErrorConsumer);
        }
    }

    public void clearToken() {
        this.updateToken(null);
    }

    private void mergePocket(Map<String, List<SimpleItem>> p) {
        if (p != null) {
            for (Map.Entry<String, List<SimpleItem>> ety : p.entrySet()) {
                String k = ety.getKey();
                List<SimpleItem> v = ety.getValue();
                if (v == null) {
                    pocket.remove(k);
                } else {
                    pocket.put(k, v);
                }
            }
        }
    }

    public SimpleItem[] obtainPocketItems(String key) {
        List<SimpleItem> ret = key == null ? null : pocket.get(key);
        SimpleItem[] t = new SimpleItem[0];
        return ret == null ? t : ret.toArray(t);
    }

    public String translateByPocket(String key, String value) {
        SimpleItem[] p = obtainPocketItems(key);
        for (SimpleItem s : p) {
            if (TextUtils.equals(value, s.value)) {
                return s.label;
            }
        }
        return null;
    }

    public OkHttpClient createClient() {
        HttpLoggingInterceptor.Logger customLogger = message -> LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, ApiService.class, message));
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(customLogger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okb = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor).addInterceptor(new FulfillTokenInterceptor())
                .sslSocketFactory(NetHelper.createTrustAllSSLSocketFactory(), new NetHelper.TrustAllCerts())
                .hostnameVerifier(new NetHelper.TrustAllHostnameVerifier());
        Setting.Network setting = this.assitant.getSetting().network;
        if (setting.connectTimeoutMs != null) {
            okb = okb.connectTimeout(setting.connectTimeoutMs, TimeUnit.MILLISECONDS);
        }
        if (setting.readTimeoutMs != null) {
            okb = okb.readTimeout(setting.readTimeoutMs, TimeUnit.MILLISECONDS);
        }
        if (setting.writeTimeoutMs != null) {
            okb = okb.writeTimeout(setting.writeTimeoutMs, TimeUnit.MILLISECONDS);
        }
        return okb.build();
    }

    public IAsyncApiService createAsyncApi(String url) {
        return new Retrofit.Builder().client(createClient()).baseUrl(url).addConverterFactory(new ConverterFactory(GsonConverterFactory.create(this.assitant.getGson()))).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(IAsyncApiService.class);
    }

    public IAsyncApiService createAsyncApi() {
        return createAsyncApi(this.assitant.getSetting().network.apiRootUrl);
    }

    public ISyncApiService createSyncApi(String url) {
        return new Retrofit.Builder().client(createClient()).baseUrl(url).addConverterFactory(new ConverterFactory(GsonConverterFactory.create(this.assitant.getGson()))).addCallAdapterFactory(new SynchronousCallAdapterFactory()).build().create(ISyncApiService.class);
    }

    public ISyncApiService createSyncApi() {
        return createSyncApi(this.assitant.getSetting().network.apiRootUrl);
    }

    public abstract static class ApiNextConsumer<TData> implements Consumer<ApiResult<TData>> {
        private final Context context;

        protected ApiNextConsumer(Context context) {
            this.context = context;
        }
        protected ApiNextConsumer() {
            this(null);
        }

        @Override
        public final void accept(ApiResult<TData> res) throws Exception {
            this.processResult(res.data, res);
        }
        protected abstract void processResult(TData data, ApiResult<TData> res);
    }

    public static class ApiErrorConsumer implements Consumer<java.lang.Throwable> {
        private final Context context;

        protected ApiErrorConsumer(Context context) {
            this.context = context;
        }
        protected ApiErrorConsumer() {
            this(null);
        }

        @Override
        public final void accept(Throwable ex) throws Exception {
            this.handleError(ex);
        }
        protected void handleError(Throwable ex) {
            Context ctx = this.context;
            if (ctx != null) {
                String message;
                if (ex instanceof CodedException) {
                    CodedException cex = (CodedException)ex;
                    message = cex.getMessage();
                    if (StringUtil.isNullOrEmpty(message)) {
                        message = cex.getCode();
                    }
                    int resID = cex instanceof  BusinessException ? R.string.error_business : R.string.error_coded;
                    if (cex instanceof  BusinessException) {
                        message = ctx.getString(resID, message);
                    }
                } else if (ex instanceof NetworkErrorException) {
                    message = ctx.getString(R.string.error_network);
                } else {
                    message = ctx.getString(R.string.error_api);
                }
                this.showAlert(message);
            }
        }
        private void showAlert(String message) {
            ThreadHelper.runOnUiThread(new RunnableKeepingScope(() -> {
                Popups.buildAlert(this.context, message, true);
            }));
        }
    }

    public static class ApiErrorObserver<T> extends ObserverWithProgress<T> {

        public ApiErrorObserver(Context context) {
            super(context);
        }

        public ApiErrorObserver() {
            this(null);
        }

        @Override
        public void onError(Throwable ex) {
            super.onError(ex);
            ExceptionUtil.runAndRethrowAsRuntime(() -> new ApiErrorConsumer(this.getContext()).accept(ex), null);
        }
    }

    public static abstract class ApiObserver<TData> extends ApiErrorObserver<ApiResult<TData>> {

        public ApiObserver(Context context) {
            super(context);
        }

        public ApiObserver() {
            this(null);
        }

        @Override
        public final void onNext(ApiResult<TData> res) {
            super.onNext(res);
            this.processResult(res.data, res);
        }

        /**
         * @param data
         * @param res
         * @return true means result processed, false then execute the default following step (callback.onNext())
         */
        protected abstract boolean processResult(TData data, ApiResult<TData> res);
    }

    private static class SynchronousCallAdapterFactory extends CallAdapter.Factory {

        @Override
        public CallAdapter<?, ?> get(Type type, Annotation[] annotations, Retrofit retrofit) {
            return new CallAdapter<Object, Object>() {
                @Override
                public Type responseType() {
                    return type;
                }

                @Override
                public Object adapt(Call<Object> call) {
                    return ExceptionUtil.runAndRethrowAsRuntime(() -> {
                        retrofit2.Response res = call.execute();
                        if (res.isSuccessful()) {
                            return res.body();
                        }
                        throw new HttpException(res);
                    }, null);
                }
            };
        }
    }

    private class FulfillTokenInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request req = chain.request();
            TokenInfo t = token;
            if (t != null) {
                req = req.newBuilder()
                        .header(REQUEST_HEADER_KEY_TOKEN, token.value)
                        .header(REQUEST_HEADER_KEY_TOKEN_VERSION, token.version)
                        .build();
            }
            return chain.proceed(req);
        }
    }

    private class ConverterFactory extends Converter.Factory {
        private final Converter.Factory innerFactory;

        ConverterFactory(Converter.Factory innerFactory) {
            this.innerFactory = innerFactory;
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return new ResponseBodyConverter(this.innerFactory.responseBodyConverter(type, annotations, retrofit));
        }
        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
            return this.innerFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
        }
        @Override
        public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return this.innerFactory.stringConverter(type, annotations, retrofit);
        }
    }

    private class ResponseBodyConverter<TData> implements Converter<ResponseBody, ApiResult<TData>> {
        private final Converter<ResponseBody, ApiResult<TData>> innerConverter;

        ResponseBodyConverter(Converter<ResponseBody, ApiResult<TData>> innerConverter) {
            this.innerConverter = innerConverter;
        }

        @Override
        public ApiResult<TData> convert(ResponseBody responseBody) throws IOException {
            ApiResult<TData> res = this.innerConverter.convert(responseBody);
            updateServerTime(res.timestamp);
            this.checkUpdateToken(res.token);
            mergePocket(res.pocket);
            ErrorInfo err = res.error;
            if (err != null) {
                if (err.isLogic) {
                    throw new BusinessException(err.code, err.message);
                } else {
                    throw new CodedException(err.code, err.message);
                }
            }
            return res;
        }

        private void checkUpdateToken(TokenInfo token) {
            if (token != null) {
                if (StringUtil.isNullOrEmpty(token.value)) {
                    token = null;
                }
                updateToken(token);
            }
        }
    }
}
