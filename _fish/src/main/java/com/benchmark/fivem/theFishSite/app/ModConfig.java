package com.benchmark.fivem.theFishSite.app;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class ModConfig extends AbstractModConfig {

    public ModConfig() {
        super();
        
        strings.put(Strings.DATABASE_AUTHORITY, "com.benchmark.fivem.theFishSite.db");
        strings.put(Strings.DATABASE_NAME, "com.benchmark.fivem.theFishSite");
        strings.put(Strings.SHARED_PREFS, "fish_prefs");
        strings.put(Strings.FEED_URL, "http://fish.ios-app-feed.5m-app.dust.screenformat.com/");
        strings.put(Strings.GOOGLE_ANALYTICS_PROPERTY_ID, "UA-49810562-6");

        ints.put(Integers.DATABASE_VERSION, 1);
    }
}
