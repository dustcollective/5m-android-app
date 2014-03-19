package com.m5.android.avicola.file;

import android.content.Context;

import com.hanacek.android.utilLib.cache.AbstractCache;

import java.io.File;

public class InternalStorage extends AbstractCache {

    public InternalStorage(Context context) {
        super(context);
    }
    
    public File getRootDirectory() {
        return context.getCacheDir();
    }
}
