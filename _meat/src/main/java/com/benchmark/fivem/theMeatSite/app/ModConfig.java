package com.benchmark.fivem.theMeatSite.app;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class ModConfig extends AbstractModConfig {

    public ModConfig() {
        super();
        
        strings.put(Strings.DATABASE_AUTHORITY, "com.benchmark.fivem.theMeatSite.db");
        strings.put(Strings.DATABASE_NAME, "com.benchmark.fivem.theMeatSite");
        strings.put(Strings.SHARED_PREFS, "meat_prefs");
        strings.put(Strings.FEED_URL, "http://meat.ios-app-feed.5m-app.dust.screenformat.com/");
        
        ints.put(Integers.DATABASE_VERSION, 1);
    }
}
