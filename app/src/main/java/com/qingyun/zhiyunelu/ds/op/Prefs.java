package com.qingyun.zhiyunelu.ds.op;

import android.content.Context;

import velites.android.utility.framework.BasePrefManager;

/**
 * Created by regis on 17/4/26.
 */

public class Prefs extends BasePrefManager {

    private static final String PERMISSIONS_REQUESTED = "permissions_requested";
    private static final String SERIALIZED_TOKEN = "serialized_token";

    public Prefs(Context ctx) {
        super(ctx);
    }

    public boolean getPermissionRequested() {
        return this.getBool(PERMISSIONS_REQUESTED);
    }

    public void setPermissionRequested(boolean value) {
        this.setBool(PERMISSIONS_REQUESTED, value);
    }

    public String getSerializedToken() {
        return this.getStr(SERIALIZED_TOKEN);
    }

    public void setSerializedToken(String value) {
        this.setStr(SERIALIZED_TOKEN, value);
    }
}
