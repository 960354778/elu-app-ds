package com.qingyun.zhiyunelu.ds;

import android.Manifest;

/**
 * Created by regis on 16/11/11.
 */

public final class Constants {
    private Constants() {}

    public static final String[] PERMISSIONS_MUST_HAVE = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] PERMISSIONS_NICE_TO_HAVE = new String[] {Manifest.permission.READ_PHONE_STATE};

    public static final class Codes {
        private Codes() {}

        public static final int REQUEST_CODE_REQUIRE_PERMISSION = 17801;
    }
}
