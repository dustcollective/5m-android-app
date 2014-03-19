package com.benchmark.fivem.app;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class ModConfig extends AbstractModConfig {

    public ModConfig() {
        super();
        
        strings.put(Strings.DATABASE_AUTHORITY, "com.benchmark.fivem.db");
        strings.put(Strings.DATABASE_NAME, "com.benchmark.fivem");
        strings.put(Strings.SHARED_PREFS, "crop_prefs");
        strings.put(Strings.FEED_URL, "http://crop.ios-app-feed.5m-app.dust.screenformat.com/");
        
        ints.put(Integers.DATABASE_VERSION, 1);
    }
}
