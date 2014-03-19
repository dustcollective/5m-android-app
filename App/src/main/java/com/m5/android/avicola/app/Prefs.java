package com.m5.android.avicola.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class Prefs {

    private Context context;

    public Prefs(Context context) {
        this.context = context;
    }

    private SharedPreferences getPrefs() {
        return context.getSharedPreferences(AppContext.modConfig().getString(AbstractModConfig.Strings.SHARED_PREFS), 0);
    }
}
