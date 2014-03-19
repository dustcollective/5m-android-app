package com.benchmark.fivem.elSitioAvicola.app;

import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class ModConfig extends AbstractModConfig {

    public ModConfig() {
        super();
        
        strings.put(Strings.DATABASE_AUTHORITY, "com.benchmark.fivem.elSitioAvicola.db");
        strings.put(Strings.DATABASE_NAME, "com.benchmark.fivem.elSitioAvicola");
        strings.put(Strings.SHARED_PREFS, "avicola_prefs");
        strings.put(Strings.FEED_URL, "http://avicola.ios-app-feed.5m-app.dust.screenformat.com/");
        
        ints.put(Integers.DATABASE_VERSION, 1);
    }
}
