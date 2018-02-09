package com.qingyun.zhiyunelu.ds;

import android.Manifest;
import android.os.Environment;

/**
 * Created by regis on 16/11/11.
 */

public final class Constants {
    private Constants() {
    }

    public static final String BASE_URL = ChannelConfig.BASE_URL;
    public static final int PAGE_SIZE = 20;
    public static final int UPLOAD_WX_CYCLE = 2 * 60 * 60 * 1000;//2个小时


    public static final String[] PERMISSIONS_MUST_HAVE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE};
    public static final String[] PERMISSIONS_NICE_TO_HAVE = new String[]{Manifest.permission.READ_PHONE_STATE};

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
        public static final String WX_SHARE_PREFS_PATH = "/data/data/com.tencent.mm/shared_prefs/auth_info_key_prefs.xml";
        public static final String WX_MICROMS_PATH = "/data/data/com.tencent.mm/MicroMsg";
        public static final String WX_MS_DB_NAME = "EnMicroMsg.db";
        public static final String WX_PACKAGE = "com.tencent.mm";

        public static final String MIUI_SOUND_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MIUI/sound_recorder/call_rec/";
        public static final String CARCH_LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/zhiyun/cache/";
    }

    public static final class PrefsKey{
        private PrefsKey() {
        }
        public static final String AUTH_TOKEN_KEY = "authToken";
        public static final String AUTH_EXPIRE_KEY = "authExpire";
        public static final String ACCOUNT_NAME_KEY = "accountName";
        public static final String ACCOUNT_PWD_KEY = "accountPwd";
        public static final String LOGIN_NAME = "loginName";
        public static final String DISPLAY_NAME = "displayName";

    }

}
