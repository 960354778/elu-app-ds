package com.qingyun.zhiyunelu.ds.op;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by regis on 17/4/26.
 */

public class Prefs {

    private static final String PERMISSIONS_REQUESTED = "permissions_requested";

    private final SharedPreferences pref;

    public Prefs(Context ctx) {
        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public boolean getPermissionRequested() {
        return pref.getBoolean(PERMISSIONS_REQUESTED, false);
    }

    public void setPermissionRequested(boolean value) {
        pref.edit().putBoolean(PERMISSIONS_REQUESTED, value).apply();
    }

    public void setStr(String key , String value){
        pref.edit().putString(key, value).apply();
    }

    public String getStr(String key){
        return pref.getString(key, "");
    }

}
