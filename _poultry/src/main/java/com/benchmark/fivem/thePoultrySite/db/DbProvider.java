package com.benchmark.fivem.thePoultrySite.db;

import com.benchmark.fivem.thePoultrySite.app.ModConfig;
import com.m5.android.avicola.app.AppContext;

public class DbProvider extends com.m5.android.avicola.db.DbProvider {

    /**
     * the very first point we have control over the app, even before the Application is run, so we set the config here
     * although it is not really a good place
     */
    public DbProvider() {
        AppContext.setModConfig(new ModConfig());
        super.init();
    }

    @Override
    public boolean onCreate() {
        return super.onCreate();
    }
}
