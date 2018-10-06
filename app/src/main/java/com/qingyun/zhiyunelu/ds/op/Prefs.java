package com.qingyun.zhiyunelu.ds.op;

import android.content.Context;

import velites.android.utility.framework.BasePrefManager;

/**
 * Created by regis on 17/4/26.
 */

public class Prefs extends BasePrefManager {

    private static final String PERMISSIONS_REQUESTED = "permissions_requested";
    private static final String SERIALIZED_TOKEN = "serialized_token";
    private static final String SERIALIZED_POCKET = "serialized_pocket";
    private static final String LAST_USERNAME = "accountName";
    private static final String LAST_PASSWORD = "accountPwd";
    private static final String SELF_PHONE = "mySelfPhoneNum";
    private static final String DEBUG_API_BASE_URL = "debugApiBaseUrl";

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

    public String getSerializedPocket() {
        return this.getStr(SERIALIZED_POCKET);
    }

    public void setSerializedPocket(String value) {
        this.setStr(SERIALIZED_POCKET, value);
    }

    public String getLastUsername() {
        return this.getStr(LAST_USERNAME);
    }

    public void setLastUsername(String value) {
        this.setStr(LAST_USERNAME, value);
    }

    public String getLastPassword() {
        return this.getStr(LAST_PASSWORD);
    }

    public void setLastPassword(String value) {
        this.setStr(LAST_PASSWORD, value);
    }

    public String getSelfPhone() {
        return this.getStr(SELF_PHONE);
    }

    public void setSelfPhone(String value) {
        this.setStr(SELF_PHONE, value);
    }

    public String getDebugApiBase() {
        return this.getStr(DEBUG_API_BASE_URL);
    }

    public void setDebugApiBase(String value) {
        this.setStr(DEBUG_API_BASE_URL, value);
    }
}
