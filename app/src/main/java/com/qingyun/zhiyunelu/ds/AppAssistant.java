package com.qingyun.zhiyunelu.ds;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.qingyun.zhiyunelu.ds.data.DbOperator;
import com.qingyun.zhiyunelu.ds.net.ApiService;
import com.qingyun.zhiyunelu.ds.op.Prefs;

import velites.android.utility.framework.EnvironmentInfo;
import velites.android.utility.logger.SingleLooperLogProcessor;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.thread.BaseInitializer;

/**
 * Created by regis on 17/4/21.
 */

public final class AppAssistant {
    private AppAssistant() {}

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
            channel = applicationInfoWthMetaData.metaData.getString("Channel");
            device = applicationInfoWthMetaData.metaData.getString("Device");
            showLog = ChannelConfig.FORCE_SHOW_LOG || debug;
            EnvironmentInfo.ensureInit(ctx, channel, buildType);
            if (showLog) {
                LogHub.setProcessor(new SingleLooperLogProcessor());
            }
            prefs = new Prefs(ctx);
            apiService = new ApiService();
            ExceptionUtil.wrapperGlobalUncaughtExceptionHandlerWithLog();
        }
    };

    private static Context defaultContext;
    public static ApplicationInfo applicationInfoWthMetaData;
    private static DbOperator db;
    private static String channel;
    private static String device;
    private static boolean debug;
    private static String buildType;
    private static boolean showLog;
    private static Prefs prefs;
    private static ApiService apiService;

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

    public static ApiService getApi() {
        initializer.awaitInitializedNoThrows(null);
        return apiService;
    }


    public static final void ensureInit(Context ctx) {
        initializer.ensureInit(ctx);
    }
}
