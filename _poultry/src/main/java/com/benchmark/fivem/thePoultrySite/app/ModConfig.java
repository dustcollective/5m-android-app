package com.benchmark.fivem.thePoultrySite.app;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class ModConfig extends AbstractModConfig {

    public ModConfig() {
        super();
        
        strings.put(Strings.DATABASE_AUTHORITY, "com.benchmark.fivem.thePoultrySite.db");
        strings.put(Strings.DATABASE_NAME, "com.benchmark.fivem.thePoultrySite");
        strings.put(Strings.SHARED_PREFS, "poultry_prefs");
        strings.put(Strings.FEED_URL, "http://poultry.ios-app-feed.5m-app.dust.screenformat.com/");
        
        ints.put(Integers.DATABASE_VERSION, 1);
    }
}
