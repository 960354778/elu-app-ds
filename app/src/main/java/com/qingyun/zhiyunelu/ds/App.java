package com.qingyun.zhiyunelu.ds;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qingyun.zhiyunelu.ds.data.Setting;
import com.qingyun.zhiyunelu.ds.op.ApiService;
import com.qingyun.zhiyunelu.ds.op.DataCenter;
import com.qingyun.zhiyunelu.ds.op.MessagingCenter;
import com.qingyun.zhiyunelu.ds.op.PhoneCenter;
import com.qingyun.zhiyunelu.ds.op.PollingCenter;
import com.qingyun.zhiyunelu.ds.op.Prefs;
import com.qingyun.zhiyunelu.ds.op.SmsCenter;
import com.qingyun.zhiyunelu.ds.op.WechatCenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.reactivex.plugins.RxJavaPlugins;
import velites.android.support.devices.xiaomi.XiaomiConstants;
import velites.android.utility.framework.BaseApplication;
import velites.android.utility.framework.EnvironmentInfo;
import velites.android.utility.logger.LocalFileLogProcessor;
import velites.android.utility.logger.PrimitiveLogProcessor;
import velites.android.utility.logger.SingleLooperLogProcessor;
import velites.android.utility.root.RootUtility;
import velites.android.utility.misc.SystemHelper;
import velites.java.utility.log.AggregatedLogProcessor;
import velites.java.utility.log.LogStub;
import velites.java.utility.log.LogProcessor;
import velites.java.utility.merge.ObjectMerger;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.FileUtil;
import velites.java.utility.misc.PathUtil;
import velites.java.utility.misc.RxUtil;
import velites.java.utility.misc.SerializationUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.misc.SyntaxUtil;
import velites.java.utility.thread.BaseInitializer;

/**
 * Created by regis on 2017/11/29.
 */

public class App extends BaseApplication {
    public static class Assistant {
        private Assistant() {
        }

        private Context defaultContext;
        private ApplicationInfo applicationInfoWthMetaData;
        private String channel;
        private String device;
        private boolean debug;
        private String buildType;
        private File miscDir;
        private File uploadedFileDir;
        private String wxTempDirPathRoot;
        private String wxTempDirPathFormat;
        private String buildDate;
        private String buildEpoch;
        private String buildRevision;
        private Prefs prefs;
        private Setting setting;
        private DataCenter data;
        private ApiService api;
        private MessagingCenter messaging;
        private PhoneCenter phone;
        private WechatCenter wechat;
        private SmsCenter sms;
        private PollingCenter polling;

        private BaseInitializer<Context> initializer = new BaseInitializer<Context>(false, null) {
            @Override
            protected void doInit(Context ctx) {
                defaultContext = ctx.getApplicationContext();
                try {
                    applicationInfoWthMetaData = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException e) {
                    ExceptionUtil.rethrowAsRuntime(e); // this should not happen
                }
                debug = (applicationInfoWthMetaData.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;
                buildType = debug ? "debug" : "release";
                buildDate = applicationInfoWthMetaData.metaData.getString("BuildDate");
                buildEpoch = applicationInfoWthMetaData.metaData.getString("BuildEpoch");
                buildRevision = applicationInfoWthMetaData.metaData.getString("BuildRevision");
                channel = applicationInfoWthMetaData.metaData.getString("Channel");
                miscDir = new File(StringUtil.formatInvariant(Constants.Paths.MISC_DIR_FORMAT, channel));
                uploadedFileDir = new File(StringUtil.formatInvariant(Constants.Paths.UPLOADED_FILE_DIR_FORMAT, channel));
                wxTempDirPathRoot = PathUtil.concat(defaultContext.getCacheDir().getPath(), Constants.Paths.TEMP_DIR_WX_ROOT);
                wxTempDirPathFormat = PathUtil.concat(wxTempDirPathRoot, Constants.Paths.TEMP_DIR_WX_SEGMENT);
                device = applicationInfoWthMetaData.metaData.getString("Device");
                prefs = new Prefs(defaultContext);
                EnvironmentInfo.ensureInit(defaultContext, channel, buildType);
                RxJavaPlugins.setErrorHandler(RxUtil.simpleErrorConsumer);
                applySetting(new ObjectMerger(true).merge(ChannelConfig.SETTING_CHANNEL, Constants.SETTING_BASIC, null));
                ExceptionUtil.wrapperGlobalUncaughtExceptionHandlerWithLog();
            }
        };

        private void applySetting(Setting s) {
            setting = s;
            if (debug) {
                String url = prefs.getDebugApiBase();
                if (!StringUtil.isNullOrSpace(url)) {
                    s.network.apiRootUrl = url;
                }
            }
            GsonBuilder gb = new GsonBuilder();
            if (setting.format != null && setting.format.defaultDateTime != null) {
                gb = gb.setDateFormat(setting.format.defaultDateTime);
            }
            gb.registerTypeHierarchyAdapter(Calendar.class, new DateTimeUtil.CalendarTimestampAdapter());
            SerializationUtil.setDefaultGson(gb.create());
            LogProcessor logProcessor = null;
            if (setting.logging != null) {
                List<LogProcessor> lps = new ArrayList<LogProcessor>();
                LogProcessor primitive = new SingleLooperLogProcessor("log_primitive", new PrimitiveLogProcessor());
                if (!SyntaxUtil.nvl(setting.logging.suppressPrimitiveLog, false)) {
                    lps.add(primitive);
                }
                if (!SyntaxUtil.nvl(setting.logging.suppressFileLog, false)) {
                    lps.add(new SingleLooperLogProcessor("log_file", new LocalFileLogProcessor(new File(StringUtil.formatInvariant(Constants.Paths.LOG_DIR_FORMAT, channel)))));
                    lps.add(new SingleLooperLogProcessor("log_file_important", new LocalFileLogProcessor(new File(StringUtil.formatInvariant(Constants.Paths.LOG_IMPORTANT_DIR_FORMAT, channel)), LogStub.LOG_LEVEL_WARNING)));
                }
                logProcessor = new AggregatedLogProcessor(primitive, lps.toArray(new LogProcessor[0]));
            }
            LogStub.setProcessor(logProcessor);
            data = new DataCenter(this);
            api = new ApiService(this);
            messaging = new MessagingCenter(this);
            phone = new PhoneCenter(this);
            wechat = new WechatCenter(this);
            sms = new SmsCenter(this);
            polling = new PollingCenter(this);
        }

        public File getMiscDir() {
            return miscDir;
        }

        public File getUploadedFileDir() {
            return uploadedFileDir;
        }

        public String getWxTempDirPathRoot() {
            return wxTempDirPathRoot;
        }

        public String createWxTempDirPath() {
            return StringUtil.formatInvariant(wxTempDirPathFormat, UUID.randomUUID());
        }

        public Context getDefaultContext() {
            return defaultContext;
        }

        public ApplicationInfo getApplicationInfoWthMetaData() {
            return applicationInfoWthMetaData;
        }

        public String getChannel() {
            return channel;
        }

        public String getDevice() {
            return device;
        }

        public boolean isDebug() {
            return debug;
        }

        public String getBuildType() {
            return buildType;
        }

        public String getBuildDate() {
            return buildDate;
        }

        public String getBuildEpoch() {
            return buildEpoch;
        }

        public String getBuildRevision() {
            return buildRevision;
        }

        public Prefs getPrefs() {
            return prefs;
        }

        public Setting getSetting() {
            return setting;
        }

        public Gson getGson() {
            return SerializationUtil.getDefaultGson();
        }

        public DataCenter getData() {
            return data;
        }

        public ApiService getApi() {
            return api;
        }

        public MessagingCenter getMessaging() {
            return messaging;
        }

        public PhoneCenter getPhone() {
            return phone;
        }

        public WechatCenter getWechat() {
            return wechat;
        }

        public SmsCenter getSms() {
            return sms;
        }

        public PollingCenter getPolling() {
            return polling;
        }

        public final void ensureInit(Context ctx) {
            initializer.ensureInit(ctx);
        }

        public final boolean awaitInit() {
            return this.initializer.awaitInitializedNoThrows(null);
        }

        public Integer checkEnv() {
            if(!RootUtility.isRooted()){
                return R.string.warn_requires_root;
            }
            if(!FileUtil.isExistsForFile(XiaomiConstants.MIUI_SOUND_DIR)){
                return R.string.warn_requires_phone_record;
            }
            return null;
        }

        public void updateDebugApiBase(String url) {
            prefs.setDebugApiBase(url);
            if (debug) {
                setting.network.apiRootUrl = url;
            }
        }
    }

    private static App instance;
    private Assistant assistant;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        if(SystemHelper.isAppProcess(this)){
            assistant = new Assistant();
            assistant.ensureInit(this);
        }
    }

    public static App getInstance() {
        return instance;
    }

    public Assistant getAssistant() {
        assistant.awaitInit();
        return assistant;
    }
}
