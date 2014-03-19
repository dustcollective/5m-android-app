package com.hanacek.android.utilLib.cache;

import java.io.File;

public interface CacheInterface {
    public void saveFile(String link, byte[] content, long expirationTs);
    public File getFile(String link);
    public byte[] readFile(File file);
    public File[] getCacheFiles();
    public void removeFile(String link);
}