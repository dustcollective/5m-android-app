package com.benchmark.fivem.thePigSite.app;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class ModConfig extends AbstractModConfig {

    public ModConfig() {
        super();
        
        strings.put(Strings.DATABASE_AUTHORITY, "com.benchmark.fivem.thePigSite.db");
        strings.put(Strings.DATABASE_NAME, "com.benchmark.fivem.thePigSite");
        strings.put(Strings.SHARED_PREFS, "pig_prefs");
        strings.put(Strings.FEED_URL, "http://pig.ios-app-feed.5m-app.dust.screenformat.com/");
        strings.put(Strings.GOOGLE_ANALYTICS_PROPERTY_ID, "UA-49810562-8");

        ints.put(Integers.DATABASE_VERSION, 1);
    }
}
