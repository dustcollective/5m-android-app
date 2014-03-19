package com.hanacek.android.utilLib.cache;

import android.content.Context;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.hanacek.android.utilLib.util.Log;

abstract public class AbstractCache implements CacheInterface {

    protected Context context;

    public AbstractCache(Context context) {
        this.context = context;
    }

    abstract public File getRootDirectory();

    @Override
    public void saveFile(String filename, byte[] bytes, long expirationTs) {
        File file = new File(getRootDirectory(), filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);

            if (!file.setLastModified(expirationTs)) {
                Log.error("File - Could not set last modified ts, filename: " + filename);
            }
        }
        catch (Exception e) {
            Log.error("File - Could not save file, filename: " + filename, e);
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (Exception ignore) {}
            }
        }
    }

    @Override
    public File getFile(String filename) {
        return new File(getRootDirectory(), filename);
    }

    @Override
    public byte[] readFile(File file) {
        byte[] bytes = new byte[(int) file.length()];
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(bytes);
            dis.close();
            return bytes;
        }
        catch (Exception e) {
            Log.error("File - Could not read file into byte[], filename: " + file.getAbsolutePath(), e);
        }
        finally {
            if (dis != null) {
                try {
                    dis.close();
                }
                catch (Exception ignore) {}
            }
        }
        return null;
    }

    @Override
    public File[] getCacheFiles() {
        return getRootDirectory().listFiles();
    }

    @Override
    public void removeFile(String filename) {
        final File file = getFile(filename);
        if (file != null) {
            file.delete();
        }
    }
}
