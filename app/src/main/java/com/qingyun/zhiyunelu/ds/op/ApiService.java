package com.qingyun.zhiyunelu.ds.op;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
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
import java.net.HttpURLConnection;
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
import velites.android.utility.framework.EnvironmentInfo;
import velites.android.utility.misc.NetHelper;
import velites.android.utility.misc.RxHelper;
import velites.android.utility.misc.ThreadHelper;
import velites.java.utility.ex.BusinessException;
import velites.java.utility.ex.CodedException;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.CollectionUtil;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.RxUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.thread.RunnableKeepingScope;

public class ApiService {
    private static final String REQUEST_HEADER_KEY_CLIENT = "client";
    private static final String REQUEST_HEADER_KEY_TOKEN = "token";
    private static final String REQUEST_HEADER_KEY_TOKEN_VERSION = "token_version";

    private final App.Assistant assistant;

    private Map<String, List<SimpleItem>> pocket;
    private TokenInfo token;
    public TokenInfo getToken() {
        return token;
    }
    public boolean isLoggedIn() {
        return this.token != null;
    }
    private Subject<Boolean> loginStateChanged;
    public Observable<Boolean> getLoginStateChanged() {
        return loginStateChanged;
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

    public ApiService(App.Assistant assistant) {
        this.assistant = assistant;
        this.initToken();
        this.initPocket();
    }

    private void initPocket() {
        String str = this.assistant.getPrefs().getSerializedPocket();
        if (StringUtil.isNullOrEmpty(str)) {
            this.pocket = new ConcurrentHashMap<>();
        } else {
            this.pocket = this.assistant.getGson().fromJson(str, new TypeToken<ConcurrentHashMap<String, List<SimpleItem>>>(){}.getType());
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Restored pocket from cache: %s", str));
        }
    }

    private void initToken() {
        String str = this.assistant.getPrefs().getSerializedToken();
        if (!StringUtil.isNullOrEmpty(str)) {
            this.token = this.assistant.getGson().fromJson(str, TokenInfo.class);
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Restored token from cache: %s", str));
        }
        loginStateChanged = BehaviorSubject.createDefault(this.isLoggedIn());
    }

    private void updateToken(TokenInfo token) {
        boolean originLoggedIn = this.isLoggedIn();
        TokenInfo originToken = this.token;
        this.token = token;
        String str = this.assistant.getGson().toJson(token);
        this.assistant.getPrefs().setSerializedToken(token == null ? null : str);
        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Replaces token from %s to %s", this.assistant.getGson().toJson(originToken), str));
        this.checkStartExpireCheck();
        boolean currentLoggedIn = this.isLoggedIn();
        if (currentLoggedIn != originLoggedIn) {
            loginStateChanged.onNext(currentLoggedIn);
        }
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

    public Map<String, String> obtainExtraHeaders() {
        Map<String, String> ret = new HashMap<>();
        ret.put(REQUEST_HEADER_KEY_CLIENT, assistant.getGson().toJson(EnvironmentInfo.obtainClientInfo(assistant.getDefaultContext(), false)));
        TokenInfo t = token;
        if (t != null) {
            ret.put(REQUEST_HEADER_KEY_TOKEN, t.value);
            ret.put(REQUEST_HEADER_KEY_TOKEN_VERSION, t.version);
        }
        return ret;
    }

    private void mergePocket(Map<String, List<SimpleItem>> p) {
        if (!CollectionUtil.isNullOrEmpty(p)) {
            for (Map.Entry<String, List<SimpleItem>> ety : p.entrySet()) {
                String k = ety.getKey();
                List<SimpleItem> v = ety.getValue();
                if (v == null) {
                    pocket.remove(k);
                } else {
                    pocket.put(k, v);
                }
            }
            assistant.getPrefs().setSerializedPocket(assistant.getGson().toJson(pocket));
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
        HttpLoggingInterceptor.Logger customLogger = message -> Logger.i( message);
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(customLogger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okb = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(new FulfillTokenInterceptor())
                .sslSocketFactory(NetHelper.createTrustAllSSLSocketFactory(), new NetHelper.TrustAllCerts())
                .hostnameVerifier(new NetHelper.TrustAllHostnameVerifier());
        Setting.Network setting = this.assistant.getSetting().network;
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

    public AsyncApiService createAsyncApi(String url) {
        return new Retrofit.Builder().client(createClient()).baseUrl(url).addConverterFactory(new ConverterFactory(GsonConverterFactory.create(this.assistant.getGson()))).addCallAdapterFactory(new AsynchronousCallAdapterFactory()).build().create(AsyncApiService.class);
    }

    public AsyncApiService createAsyncApi() {
        return createAsyncApi(this.assistant.getSetting().network.apiRootUrl);
    }

    public SyncApiService createSyncApi(String url) {
        return new Retrofit.Builder().client(createClient()).baseUrl(url).addConverterFactory(new ConverterFactory(GsonConverterFactory.create(this.assistant.getGson()))).addCallAdapterFactory(new SynchronousCallAdapterFactory()).build().create(SyncApiService.class);
    }

    public SyncApiService createSyncApi() {
        return createSyncApi(this.assistant.getSetting().network.apiRootUrl);
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
                } else if (ex instanceof HttpException) {
                    switch (((HttpException)ex).code()) {
                        case HttpURLConnection.HTTP_UNAUTHORIZED:
                            message = ctx.getString(R.string.error_unauthroized);
                            break;
                        case HttpURLConnection.HTTP_FORBIDDEN:
                            message = ctx.getString(R.string.error_forbidden);
                            break;
                        default:
                            message = ctx.getString(R.string.error_http);
                            break;
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

    private class AsynchronousCallAdapterFactory extends CallAdapter.Factory {
        private CallAdapter.Factory innerFactory = RxJava2CallAdapterFactory.create();

        @Override
        public CallAdapter<?, ?> get(Type type, Annotation[] annotations, Retrofit retrofit) {
            CallAdapter origin = innerFactory.get(type, annotations, retrofit);
            return new CallAdapter<Object, Object>() {
                @Override
                public Type responseType() {
                    return origin.responseType();
                }

                @Override
                public Object adapt(Call<Object> call) {
                    Subject ret = PublishSubject.create();
                    Observable ob = (Observable) origin.adapt(call);
                    ob.subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeComputationSchedule())
                            .subscribe(new RxUtil.ObserverDelegate(ret, null) {
                                @Override
                                public void onError(Throwable e) {
                                    if (e instanceof HttpException) {
                                        if (((HttpException) e).code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                            updateToken(null);
                                        }
                                    }
                                    super.onError(e);
                                }
                            });
                    return ret;
                }
            };
        }
    }

    private class SynchronousCallAdapterFactory extends CallAdapter.Factory {
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
                        if (res.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            updateToken(null);
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
            Request.Builder req = chain.request().newBuilder();
            Map<String, String> extra = obtainExtraHeaders();
            if (!CollectionUtil.isNullOrEmpty(extra)) {
                for (Map.Entry<String, String> ety : extra.entrySet()) {
                    req.header(ety.getKey(), ety.getValue());
                }
            }
            return chain.proceed(req.build());
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
