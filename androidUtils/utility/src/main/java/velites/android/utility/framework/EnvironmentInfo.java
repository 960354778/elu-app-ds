package velites.android.utility.framework;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import velites.java.utility.generic.Tuple2;
import velites.java.utility.generic.Tuple3;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.thread.BaseInitializer;

/**
 * Created by regis on 17/3/31.
 */

public class EnvironmentInfo implements Cloneable {

    private static final DateFormat df = DateTimeUtil.DEFAULT_DATEFORMAT_DATETIME_DETAIL;

    private static BaseInitializer<Tuple3<Context, String, String>> initializer = new BaseInitializer<Tuple3<Context, String, String>>(false, null) {
        @Override
        protected void doInit(Tuple3<Context, String, String> values) {
            prototype = new EnvironmentInfo();
            prototype.fulfillWithFixedInfo(values.v1, values.v2, values.v3);
        }
    };

    private static EnvironmentInfo prototype = new EnvironmentInfo();
    public static EnvironmentInfo getPrototype() {
        initializer.awaitInitializedNoThrows(null);
        return prototype;
    }


    public static EnvironmentInfo obtainClientInfo(Context ctx) {
        return obtainClientInfo(ctx, true);
    }

    public static EnvironmentInfo obtainClientInfo(Context ctx, boolean awaitInit) {
        if (awaitInit) {
            initializer.awaitInitializedNoThrows(null);
        }
        EnvironmentInfo client = null;
        EnvironmentInfo proto = prototype;
        if (proto == null) {
            client = new EnvironmentInfo();
        } else {
            try {
                client = (EnvironmentInfo) proto.clone();
            } catch (CloneNotSupportedException e) {
                ExceptionUtil.rethrowAsRuntime(e); // it is sure to be Cloneable
            }
        }
        client.fulfillWithEnv(ctx);
        return client;
    }

    public int uid;
    public String timestamp;
    public String channel;
    public String buildType;
    public String packageName;
    public int processId;
    public String processName;
    public int versionCode;
    public String versionName;
    public String locale;
    public float screenDensity;
    public int screenWidth;
    public int screenHeight;
    public String hardwareBrand;
    public String hardwareModel;
    public String androidVersion;
    public int androidSdk;
    public String deviceId;
    public int networkType;
    public int orientation;


    @SuppressLint("MissingPermission")
    public void fulfillWithFixedInfo(Context ctx, String channel, String buildType) {
        if (ctx != null) {
            this.channel = channel;
            this.buildType = buildType;
            packageName = ctx.getPackageName();
            locale = ctx.getResources().getConfiguration().locale.toString();
            DisplayMetrics dp = ctx.getResources().getDisplayMetrics();
            screenDensity = dp.density;
            screenWidth = dp.widthPixels;
            screenHeight = dp.heightPixels;
            hardwareBrand = Build.BRAND;
            hardwareModel = Build.MODEL;
            androidVersion = Build.VERSION.RELEASE;
            androidSdk = Build.VERSION.SDK_INT;
            Tuple2<String, Integer> versions = obtainAppVersion(ctx);
            if (versions != null) {
                versionName = versions.v1;
                versionCode = versions.v2;
            }
            processId = android.os.Process.myPid();
            for (ActivityManager.RunningAppProcessInfo p : ((ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()) {
                if (p.pid == processId) {
                    processName = p.processName;
                }
            }
            // TODO: make deviceId solid
            deviceId = getLocalMac(ctx);
        }
    }

    private void fulfillWithEnv(Context ctx) {
        timestamp = df.format(new Date());
        if (ctx != null) {
            try {
                networkType = ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType();
            } catch (Exception ex) {
                ExceptionUtil.swallowThrowable(ex, this); // error then give up fulfill.
            }
            try {
                orientation = ctx.getResources().getConfiguration().orientation;
            } catch (Exception ex) {
                ExceptionUtil.swallowThrowable(ex, this); // error then give up fulfill.
            }
        }
    }

    public static final void ensureInit(Context ctx, String channel, String buildType) {
        initializer.ensureInit(new Tuple3<>(ctx, channel, buildType));
    }

    public static final Tuple2<String, Integer> obtainAppVersion(Context ctx) {
        if (ctx == null) {
            return null;
        }
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return new Tuple2<>(pi.versionName, pi.versionCode);
        } catch (PackageManager.NameNotFoundException ex) {
            ExceptionUtil.swallowThrowable(ex, null); // this should not happen
        }
        return null;
    }

    private static String getLocalMac(Context context) {
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            return buf.toString();
        } catch (Exception ex) {
            ExceptionUtil.swallowThrowable(ex);
            return null;
        }
    }
}
