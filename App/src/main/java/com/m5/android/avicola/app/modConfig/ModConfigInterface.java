package com.m5.android.avicola.app.modConfig;

public interface ModConfigInterface {
    
    public String   getString(AbstractModConfig.Strings key);
    public boolean  getBoolean(AbstractModConfig.Booleans key);
    public int      getInt(AbstractModConfig.Integers key);
    public long     getLong(AbstractModConfig.Longs key);
    public double   getDouble(AbstractModConfig.Doubles key);
}
