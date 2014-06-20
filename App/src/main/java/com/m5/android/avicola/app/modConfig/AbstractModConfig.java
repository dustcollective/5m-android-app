package com.m5.android.avicola.app.modConfig;

import java.util.HashMap;
import java.util.Map;

abstract public class AbstractModConfig implements ModConfigInterface {
    
    public enum Integers {DATABASE_VERSION};
    
    public enum Strings {DATABASE_NAME, DATABASE_AUTHORITY, SHARED_PREFS, FEED_URL, GOOGLE_ANALYTICS_PROPERTY_ID};
    
    public enum Booleans {};
    
    public enum Longs {};
    
    public enum Doubles {};

    protected Map<Strings, String> strings;
    protected Map<Booleans, Boolean> booleans;
    protected Map<Integers, Integer> ints;
    protected Map<Longs, Long> longs;
    protected Map<Doubles, Double> doubles;
    
    public AbstractModConfig() {
        strings = new HashMap<Strings, String>();
        booleans = new HashMap<Booleans, Boolean>();
        ints = new HashMap<Integers, Integer>();
        longs = new HashMap<Longs, Long>();
        doubles = new HashMap<Doubles, Double>();
    }
    
    @Override
    public String getString(Strings key) {
        return strings.get(key);
    }

    @Override
    public boolean getBoolean(Booleans key) {
        return booleans.get(key);
    }

    @Override
    public int getInt(Integers key) {
        return ints.get(key);
    }

    @Override
    public long getLong(Longs key) {
        return longs.get(key);
    }
    
    @Override
    public double getDouble(Doubles key) {
        return doubles.get(key);
    }
}
