package com.m5.android.avicola.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class Prefs {

    private static final String PREFS_APP_RUN_COUNTER = "prefs_app_run_counter";
    private static final String PREFS_RATE_APP_SHOWN = "prefs_rate_app_shown";

    private Context context;

    public Prefs(Context context) {
        this.context = context;
    }

    private SharedPreferences getPrefs() {
        return context.getSharedPreferences(AppContext.modConfig().getString(AbstractModConfig.Strings.SHARED_PREFS), 0);
    }

    public boolean isGaEnabled() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("pref_key_ga_enabled", true);
    }

    public void incrementAppRunCounter() {
        getPrefs().edit().putInt(PREFS_APP_RUN_COUNTER, getPrefs().getInt(PREFS_APP_RUN_COUNTER, 0)+1).commit();
    }

    public int getAppRunCounter() {
        return getPrefs().getInt(PREFS_APP_RUN_COUNTER, 0);
    }

    public void setRateAppShown() {
        getPrefs().edit().putBoolean(PREFS_RATE_APP_SHOWN, true).commit();
    }

    public boolean wasRateAppShown() {
        return getPrefs().getBoolean(PREFS_RATE_APP_SHOWN, false);
    }
}
