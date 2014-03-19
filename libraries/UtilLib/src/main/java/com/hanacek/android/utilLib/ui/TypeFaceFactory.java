package com.hanacek.android.utilLib.ui;

import android.graphics.Typeface;

import com.google.common.cache.CacheBuilder;

import com.hanacek.android.utilLib.app.GenericAppContext;
import com.hanacek.android.utilLib.util.Log;

public class TypeFaceFactory {

    private static com.google.common.cache.Cache<String, Typeface> memCache = CacheBuilder.newBuilder().build();
    
    public static Typeface getTypeFace(String typefaceName) {
        Typeface typeface = memCache.getIfPresent(typefaceName);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(GenericAppContext.context().getResources().getAssets(), "fonts/" + typefaceName);
                memCache.put(typefaceName, typeface);
            }
            catch (RuntimeException e) {
                Log.error("No typeface found: '" + typefaceName + "'", e);
            }
        }
        return typeface;
    }
    
}