package com.benchmark.fivem.theCattleSite.app;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class ModConfig extends AbstractModConfig {

    public ModConfig() {
        super();
        
        strings.put(Strings.DATABASE_AUTHORITY, "com.benchmark.fivem.theCattleSite.db");
        strings.put(Strings.DATABASE_NAME, "com.benchmark.fivem.theCattleSite");
        strings.put(Strings.SHARED_PREFS, "cattle_prefs");
        strings.put(Strings.FEED_URL, "http://cattle.ios-app-feed.5m-app.dust.screenformat.com/");
        
        ints.put(Integers.DATABASE_VERSION, 1);
    }
}
