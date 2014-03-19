package com.benchmark.fivem.theDairySite.app;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class ModConfig extends AbstractModConfig {

    public ModConfig() {
        super();
        
        strings.put(Strings.DATABASE_AUTHORITY, "com.benchmark.fivem.theDairySite.db");
        strings.put(Strings.DATABASE_NAME, "com.benchmark.fivem.theDairySite");
        strings.put(Strings.SHARED_PREFS, "dairy_prefs");
        strings.put(Strings.FEED_URL, "http://dairy.ios-app-feed.5m-app.dust.screenformat.com/");
        
        ints.put(Integers.DATABASE_VERSION, 1);
    }
}
