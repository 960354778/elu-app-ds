package com.qingyun.zhiyunelu.ds;

import android.Manifest;
import android.os.Environment;

import com.qingyun.zhiyunelu.ds.data.Setting;

import java.util.GregorianCalendar;

import velites.java.utility.misc.PathUtil;

/**
 * Created by regis on 16/11/11.
 */

public final class Constants {
    private Constants() {
    }

    public static final class Misc {
        private Misc() {
        }

        public static final int REQUEST_CODE_REQUIRE_PERMISSION = 17801;
        public static final String[] PERMISSIONS_MUST_HAVE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,Manifest.permission.READ_SMS};
        public static final String[] PERMISSIONS_NICE_TO_HAVE = new String[]{};
    }

    public static final class Paths {
        private Paths() {
        }

        public static final String STORAGE_ROOT_FORMAT = PathUtil.concat(Environment.getExternalStorageDirectory().getAbsolutePath(), "zhiyun/%s");
        public static final String LOG_DIR_FORMAT = PathUtil.concat(STORAGE_ROOT_FORMAT, "log");
        public static final String LOG_IMPORTANT_DIR_FORMAT = PathUtil.concat(STORAGE_ROOT_FORMAT, "log-important");
        public static final String MISC_DIR_FORMAT = PathUtil.concat(STORAGE_ROOT_FORMAT, "misc");
        public static final String UPLOADED_FILE_DIR_FORMAT = PathUtil.concat(STORAGE_ROOT_FORMAT, "files");
        public static final String TEMP_DIR_WX_SEGMENT = "wx/%s";
        public static final String URL_SEGMENT_MESSAGE_HUB = "message";
    }

    public static final class Logic {
        private Logic() {
        }

        public static final String EVENT_MESSAGE_HUB_DIALING = "Dialing";
    }

    public static final Setting SETTING_BASIC;
    private static Setting buildBasicSetting() {
        Setting ret = new Setting();
        ret.network = new Setting.Network();
        ret.network.connectTimeoutMs = 15 * 60 * 1000L;
        ret.network.readTimeoutMs = 15 * 60 * 1000L;
        ret.network.writeTimeoutMs = 15 * 60 * 1000L;
        ret.logging = new Setting.Logging();
        ret.logging.logLevel = 0;
        ret.logging.suppressPrimitiveLog = false;
        ret.logging.suppressFileLog = false;
        ret.logging.suppressLogReport = false;
        ret.format = new Setting.Format();
        ret.format.defaultDateTime = "yyyy-MM-dd HH:mm:ss.SSSZ";
        ret.logic = new Setting.Logic();
        ret.logic.callElapseThresholdMs = 2 * 60 * 60 * 1000L;
        ret.logic.callRecordMatchDelayMs = 5 * 1000L;
        ret.logic.callRecordMatchIntervalMs = 2 * 1000L;
        ret.logic.callRecordMatchSameTimesThreshold = 16;
        ret.logic.callRecordUnmatchedOffsetMs = 40 * 60 * 1000L;
        ret.logic.callRecordUnmatchedStartEpochMs = new GregorianCalendar(2018, 2, 1).getTimeInMillis();
        ret.logic.wxChatSyncReserveMs = 2 * 1000L;
        ret.logic.wxChatSyncCountThreshold = 200L;
        ret.logic.smsChatSyncReserveMs = 2 * 1000L;
        ret.logic.smsChatSyncCountThreshold = 200L;
        ret.logic.pollingIntervalMs = 2 * 60 * 60 * 1000L;
        ret.logic.messageHubAutoReconnectIntervalMs = 10 * 1000L;
        ret.path = new Setting.Path();
        ret.path.decryptedWxDbFileName = "wx.db";
        return ret;
    }

    static {
        SETTING_BASIC = buildBasicSetting();
    }
}
