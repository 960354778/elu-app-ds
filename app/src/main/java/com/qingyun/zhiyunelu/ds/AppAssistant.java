package com.qingyun.zhiyunelu.ds;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.qingyun.zhiyunelu.ds.data.DbOperator;
import com.qingyun.zhiyunelu.ds.net.ApiService;
import com.qingyun.zhiyunelu.ds.net.NetLife.NetLifeManager;
import com.qingyun.zhiyunelu.ds.net.NetLife.RequestQueue;
import com.qingyun.zhiyunelu.ds.op.Prefs;
import com.qingyun.zhiyunelu.ds.record.SoundRecordSynchronizer;
import com.qingyun.zhiyunelu.ds.sms.SmsManager;
import com.qingyun.zhiyunelu.ds.wechat.WxManager;

import velites.android.utility.framework.EnvironmentInfo;
import velites.android.utility.logger.ReportLogLooperProcessor;
import velites.android.utility.logger.SingleLooperLogProcessor;
import velites.java.utility.log.AggregatedLogProcessor;
import velites.java.utility.log.LogHub;
import velites.java.utility.log.LogProcessor;
import velites.java.utility.log.LogReport;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.thread.BaseInitializer;

/**
 * Created by regis on 17/4/21.
 */

public final class AppAssistant {
    private AppAssistant() {
    }

    private static BaseInitializer<Context> initializer = new BaseInitializer<Context>(false, null) {
        @Override
        protected void doInit(Context ctx) {
            defaultContext = ctx;
            try {
                applicationInfoWthMetaData = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                ExceptionUtil.rethrowAsRuntime(e); // this should not happen
            }
            db = new DbOperator(ctx);
            debug = (applicationInfoWthMetaData.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;
            buildType = debug ? "debug" : "release";
            buildDate = applicationInfoWthMetaData.metaData.getString("BuildDate");
            buildEpoch = applicationInfoWthMetaData.metaData.getString("BuildEpoch");
            buildRevision = applicationInfoWthMetaData.metaData.getString("BuildRevision");
            channel = applicationInfoWthMetaData.metaData.getString("Channel");
            logDir = String.format(Constants.FilePaths.LOG_DIR_FORMAT, channel);
            uploadedFileDir = String.format(Constants.FilePaths.UPLOADED_FILE_DIR_FORMAT, channel);
            device = applicationInfoWthMetaData.metaData.getString("Device");
            showLog = ChannelConfig.FORCE_SHOW_LOG || debug;
            EnvironmentInfo.ensureInit(ctx, channel, buildType);
            if (showLog) {
                LogReport.initialize(new LogReport.Builder()
                        .setEnable(true)
                        .setReportDir(logDir)
                        .setLogProcessor(new SingleLooperLogProcessor())
                        .setReportProcessor(new ReportLogLooperProcessor())
                        .builder()
                );
                LogProcessor log = LogReport.getBuilder().getLogProcessor();
                LogProcessor report = LogReport.getBuilder().getReportProcessor();

                LogHub.setProcessor(new AggregatedLogProcessor(report,log, report));

            }
            prefs = new Prefs(ctx);
            apiBaseUrl = ChannelConfig.BASE_URL;
            apiService = new ApiService();
            requestQueue = NetLifeManager.newRequestQueue();
            ExceptionUtil.wrapperGlobalUncaughtExceptionHandlerWithLog();
//            WxManager.initWxManager(ctx);
//            SmsManager.initSms();
            SoundRecordSynchronizer.init();
        }
    };

    private static Context defaultContext;
    private static ApplicationInfo applicationInfoWthMetaData;
    private static DbOperator db;
    private static String channel;
    private static String device;
    private static boolean debug;
    private static String buildType;
    private static boolean showLog;
    private static Prefs prefs;
    private static ApiService apiService;
    private static RequestQueue requestQueue;
    private static String logDir;
    private static String uploadedFileDir;
    private static String buildDate;
    private static String buildEpoch;
    private static String buildRevision;
    private static String apiBaseUrl;

    public static String getLogDir() {
        return logDir;
    }

    public static String getUploadedFileDir() {
        return uploadedFileDir;
    }

    public static Context getDefaultContext() {
        initializer.awaitInitializedNoThrows(null);
        return defaultContext;
    }

    public static ApplicationInfo getApplicationInfoWthMetaData() {
        initializer.awaitInitializedNoThrows(null);
        return applicationInfoWthMetaData;
    }

    public static DbOperator getDb() {
        initializer.awaitInitializedNoThrows(null);
        return db;
    }

    public static String getChannel() {
        initializer.awaitInitializedNoThrows(null);
        return channel;
    }

    public static String getDevice() {
        initializer.awaitInitializedNoThrows(null);
        return device;
    }

    public static boolean isDebug() {
        initializer.awaitInitializedNoThrows(null);
        return debug;
    }

    public static String getBuildType() {
        initializer.awaitInitializedNoThrows(null);
        return buildType;
    }

    public static boolean isShowLog() {
        initializer.awaitInitializedNoThrows(null);
        return showLog;
    }

    public static Prefs getPrefs() {
        initializer.awaitInitializedNoThrows(null);
        return prefs;
    }

    public static String getApiBaseUrl() {
        if (debug) {
            String url = prefs.getStr(Constants.PrefsKey.DEBUG_API_BASE_URL);
            if (!StringUtil.isNullOrSpace(url)) {
                return url;
            }
        }
        return apiBaseUrl;
    }

    public static void setApiBaseUrl(String url) {
        AppAssistant.getPrefs().setStr(Constants.PrefsKey.DEBUG_API_BASE_URL, url);
    }

    public static ApiService getApi() {
        initializer.awaitInitializedNoThrows(null);
        return apiService;
    }

    public static RequestQueue getRequestQueue() {
        initializer.awaitInitializedNoThrows(null);
        return requestQueue;
    }

    public static String getBuildDate() {
        return buildDate;
    }

    public static String getBuildEpoch() {
        return buildEpoch;
    }

    public static String getBuildRevision() {
        return buildRevision;
    }

    public static final void ensureInit(Context ctx) {
        initializer.ensureInit(ctx);
    }
}
