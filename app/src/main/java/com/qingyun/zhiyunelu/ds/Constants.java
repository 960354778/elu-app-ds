package com.qingyun.zhiyunelu.ds;

import android.Manifest;

/**
 * Created by regis on 16/11/11.
 */

public final class Constants {
    private Constants() {
    }

    public static final String[] PERMISSIONS_MUST_HAVE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE};
    public static final String[] PERMISSIONS_NICE_TO_HAVE = new String[]{Manifest.permission.READ_PHONE_STATE};

    public static final class Codes {
        private Codes() {
        }

        public static final int REQUEST_CODE_REQUIRE_PERMISSION = 17801;
    }

    public static final class FilePaths {
        private FilePaths() {
        }
        public static final String WX_SHARE_PREFS_PATH = "/data/data/com.tencent.mm/shared_prefs/auth_info_key_prefs.xml";
        public static final String WX_MICROMS_PATH = "/data/data/com.tencent.mm/MicroMsg";
        public static final String WX_MS_DB_NAME = "EnMicroMsg.db";
        public static final String WX_PACKAGE = "com.tencent.mm";
    }

    public static final class PrefsKey{
        private PrefsKey() {
        }
    }
}
