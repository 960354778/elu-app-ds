package velites.android.utility.framework;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class BasePrefManager {
    protected final SharedPreferences pref;

    protected BasePrefManager(Context ctx) {
        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void setStr(String key, String value) {
        pref.edit().putString(key, value).apply();
    }

    public String getStr(String key) {
        return pref.getString(key, "");
    }

    public void setLong(String key, long value) {
        pref.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return pref.getLong(key, 0L);
    }

    public void setBool(String key, boolean value) {
        pref.edit().putBoolean(key, value).apply();
    }

    public boolean getBool(String key) {
        return pref.getBoolean(key, false);
    }
}
