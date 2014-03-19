package com.hanacek.android.utilLib.cache;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.ImageView;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;

import com.hanacek.android.utilLib.tasks.AbstractHttpImageAsyncTask;
import com.hanacek.android.utilLib.tasks.HttpAsyncTaskConfiguration;
import com.hanacek.android.utilLib.util.Log;
import com.hanacek.android.utilLib.util.Util;

public class ImageCache {

    /**
     * In case of 5, images will be kept in memory until it exceeds 1/5 of total memory available for the app. Least recently used policy is used for eviction.
     */
    private static final int MEMORY_POOL = 5;

    final private com.google.common.cache.Cache<String, Bitmap> memCache;
    final private HttpAsyncTaskConfiguration httpAsyncTaskConfiguration;

    public interface OnImageLoadedInterface {
        public void onImageLoaded(String url, Bitmap bitmap);
    }

    public ImageCache(HttpAsyncTaskConfiguration httpAsyncTaskConfiguration) {
        this.httpAsyncTaskConfiguration = httpAsyncTaskConfiguration;
        memCache = CacheBuilder.newBuilder()
                .maximumWeight(Runtime.getRuntime().maxMemory()/MEMORY_POOL)
                .weigher(new Weigher<String, Bitmap>() {
                    @SuppressLint("NewApi")
                    public int weigh(String url, Bitmap bitmap) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                            return bitmap.getRowBytes() * bitmap.getHeight();
                        } else {
                            return bitmap.getByteCount();
                        }
                    }
                })
                .build();
    }

    public void displayImage(final String url, final OnImageLoadedInterface onImageLoadedInterface) {
        final Bitmap bitmap = retrieveFromMemCache(url, null);
        if (bitmap == null || bitmap.isRecycled()) {
            new AbstractHttpImageAsyncTask(url, httpAsyncTaskConfiguration){
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    memCache.put(getMemCacheHash(url, null), bitmap);
                    onImageLoadedInterface.onImageLoaded(url, bitmap);
                }
            }.executeOnSeparateThread();
        }
        else {
            onImageLoadedInterface.onImageLoaded(url, bitmap);
        }

        log();
    }

    private void log() {
        //Log.debug("Cache - >>> i:cache size :"+ memCache.size() +":"+ memCache.stats());
        Log.debug("Cache: " + Util.formatSize(Runtime.getRuntime().totalMemory()) + " (alloc: "
                + Util.formatSize(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
                + ") /" + Util.formatSize(Runtime.getRuntime().maxMemory()/MEMORY_POOL) +
                ", number of items stored in cache :"+ memCache.size());
    }

    public void displayImage(String url, final ImageView imageView) {
        final Bitmap bitmap = retrieveFromMemCache(url, imageView);
        if (bitmap == null) {
            new AbstractHttpImageAsyncTask(url, imageView, httpAsyncTaskConfiguration){
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    memCache.put(getMemCacheHash(url, imageView), bitmap);
                }
            }.executeOnSeparateThread();
        }
        else {
            imageView.setImageBitmap(bitmap);
        }

        log();
    }

    /**
     * Test if cache can be cleaned
     */
    public void testCache() {
        displayImage("http://d1.stern.de/bilder/stern_5/panorama/2013/KW50/lanz_1260_maxsize_1340_894.jpg", new OnImageLoadedInterface() {
            @Override
            public void onImageLoaded(String url, Bitmap bitmap) {
                boolean gogogo = true;
                int size = 500;
                while(gogogo) {
                    Bitmap bmp = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()+size, bitmap.getHeight()+size, true);
                    memCache.put(size+"", bmp);
                    size++;
                    Log.debug("CACHE: " + Util.formatSize(Runtime.getRuntime().totalMemory()) + "/" + Util.formatSize(Runtime.getRuntime().maxMemory()));
                }
            }
        });
    }

    public static String getLocalCacheHash(String url) {
        return Util.getMd5Hash(url);
    }

    /**
     * Get the hash for memCache
     *
     * @param url
     * @param imageView
     * @return
     */
    private String getMemCacheHash(String url, ImageView imageView) {
        return getMemCacheHash(url, imageView, null);
    }

    /**
     * Get the hash for memCache
     * When imageView is not measured and bitmap is not null, bitmap size is used when saving to the cache
     *
     * When ImageView doesn't have preset size then at some point it might return 0 while bitmap at that time is ready and re-sized, so we should rather use
     * size of the bitmap than size of the imageView.
     *
     * @param url
     * @param imageView
     * @param bitmap
     * @return
     */
    public static String getMemCacheHash(String url, ImageView imageView, Bitmap bitmap) {
        String toBeHashed;
        if (imageView == null) {
            toBeHashed = url;
        }
        else {
            float w = Math.max(imageView.getMeasuredWidth(), (imageView.getLayoutParams() == null) ? 0 : imageView.getLayoutParams().width);
            float h = Math.max(imageView.getMeasuredHeight(), (imageView.getLayoutParams() == null) ? 0 : imageView.getLayoutParams().height);

            if ((w < 3 || h < 3) && bitmap != null && !bitmap.isRecycled()) {
                toBeHashed = url + bitmap.getWidth() + bitmap.getHeight();
            }
            else {
                toBeHashed = url + w + h;
            }
        }
        return Util.getMd5Hash(toBeHashed);
    }

    private Bitmap retrieveFromMemCache(String url, ImageView imageView) {
        try {
            return memCache.getIfPresent(getMemCacheHash(url, imageView));
        }
        catch (Exception e) {
            Log.error("ImageCache", e);
        }
        return null;
    }
}
