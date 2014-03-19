package com.m5.android.avicola;

import android.app.Application;

import com.m5.android.avicola.app.AppContext;

public class M5Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.initialize(getApplicationContext());
    }

}
