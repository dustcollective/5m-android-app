package com.hanacek.android.utilLib.cache;

import java.io.File;

import com.hanacek.android.utilLib.util.Log;
import com.hanacek.android.utilLib.util.Util;

/**
 * Generic cache implementation with save, load, remove, as a cache interface it uses the AbstractCache but can be replaced by any other class
 * implementing CacheInterface.
 */
public class Cache {

    private CacheInterface mInterface;
    private CacheConfiguration cacheConfiguration;

    public Cache (CacheInterface cacheInterface, CacheConfiguration cacheConfiguration) {
        this.mInterface = cacheInterface;
        this.cacheConfiguration = cacheConfiguration;
    }

    public void save(String link, byte[] content, long expirationTs) {
//        Log.debug("Cache file save: " + link);
//        Log.debug("Cache file save: "+hash(link));
        mInterface.saveFile(hash(link), content, expirationTs);
    }
    
    public byte[] load(String link, boolean allowExpired) {
        File content = mInterface.getFile(hash(link));
        if (content == null || !content.exists()) {
//            Log.debug("Cache file not found: "+link);
            return null;
        }

//        Log.debug("Cache file found: "+link + ", is expired: " + (System.currentTimeMillis() > content.lastModified()) + ", will expire in "
//                +((content.lastModified() - System.currentTimeMillis())/1000)+" s");

        if (allowExpired || System.currentTimeMillis() < content.lastModified()) {
//            Log.debug("Cache file found: " + hash(link));
            return mInterface.readFile(content);
        }
        
//        Log.debug("Cache file found but is expired, and expired are not allowed: " + link);
        
        return null;
    }
    
    private String hash(String link) {
        return Util.getMd5Hash(link);
    }
    
    public boolean isValid(String link) {
        File content = mInterface.getFile(hash(link));
        if (content == null) {
            return false;
        }     

        if (System.currentTimeMillis() < content.lastModified()) {
//            Log.debug("valid, expires at: " + new Date(content.lastModified()).toString());
            return true;
        } else {
//            Log.debug("Not valid, expires at: " + new Date(content.lastModified()).toString());
        }
        return false;
    }

    public void clearExpiredCache() {
        File[] files = mInterface.getCacheFiles();
            for (File file : files) {
                if (file.isFile() && file.lastModified() + cacheConfiguration.cacheValidity < System.currentTimeMillis()) {
//                    Log.debug("file removed from cache: " + file.getAbsolutePath());
                    if (!file.delete()) {
                        Log.error("could not delete cache file: " + file.getAbsolutePath());
                    }
                }
        }
    }

    public void remove(String link) {
        mInterface.removeFile(link);
    }
}
