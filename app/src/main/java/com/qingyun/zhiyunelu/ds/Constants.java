package com.qingyun.zhiyunelu.ds;

import android.Manifest;
import android.os.Environment;

import com.qingyun.zhiyunelu.ds.data.Setting;

import java.util.concurrent.TimeUnit;

import velites.java.utility.misc.PathUtil;

/**
 * Created by regis on 16/11/11.
 */

public final class Constants {
    private Constants() {
    }

    public static final Setting SETTING_BASIC;
    public static final String[] PERMISSIONS_MUST_HAVE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,Manifest.permission.READ_SMS};
    public static final String[] PERMISSIONS_NICE_TO_HAVE = new String[]{};

    public static final class Codes {
        private Codes() {
        }

        public static final int REQUEST_CODE_REQUIRE_PERMISSION = 17801;
        public static final int REQUEST_NET_MY_DOCTER_LIST_TAG = 21001;
        public static final int REQUEST_NET_MY_HOSPITAL_LIST_TAG = 21002;
        public static final int REQUEST_NET_DOCTER_LIST_TAG = 21003;
        public static final int REQUEST_NET_HOSPITAL_LIST_TAG = 21004;
    }

    public static final class FilePaths {
        private FilePaths() {
        }

        public static final String STORAGE_ROOT_FORMAT = PathUtil.concat(Environment.getExternalStorageDirectory().getAbsolutePath(), "zhiyun/%s");
        public static final String LOG_DIR_FORMAT = PathUtil.concat(STORAGE_ROOT_FORMAT, "log");
        public static final String MISC_DIR_FORMAT = PathUtil.concat(STORAGE_ROOT_FORMAT, "misc");
        public static final String UPLOADED_FILE_DIR_FORMAT = PathUtil.concat(STORAGE_ROOT_FORMAT, "files");
        public static final String TEMP_DIR_WX_SEGMENT = "wx";
    }

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
        return ret;
    }

    static {
        SETTING_BASIC = buildBasicSetting();
    }
}
