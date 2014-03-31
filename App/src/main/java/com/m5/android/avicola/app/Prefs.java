package com.m5.android.avicola.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class Prefs {

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
}
