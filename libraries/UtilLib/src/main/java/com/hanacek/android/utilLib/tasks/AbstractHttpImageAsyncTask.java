package com.hanacek.android.utilLib.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.widget.ImageView;

import java.util.concurrent.ConcurrentHashMap;

import com.hanacek.android.utilLib.cache.ImageCache;

public abstract class AbstractHttpImageAsyncTask extends AbstractHttpAsyncTask<byte[]> {

    private static ConcurrentHashMap<ImageView, String> beingLoaded = new ConcurrentHashMap<ImageView, String>();
    private ImageView imageView;

    public AbstractHttpImageAsyncTask(String url, HttpAsyncTaskConfiguration httpAsyncTaskConfiguration) {
        super(url, httpAsyncTaskConfiguration);
        init();
    }

    public AbstractHttpImageAsyncTask(String url, ImageView imageView, HttpAsyncTaskConfiguration httpAsyncTaskConfiguration) {
        super(url, httpAsyncTaskConfiguration);
        this.imageView = imageView;
        init();
        registerHolder(url, imageView);
    }

    private void init() {
        if (url == null) {
            taskDone = true;
            failed = new FailHolder(new IllegalArgumentException("Image url cannot be null."));
            return;
        }

        setExpiresAt(System.currentTimeMillis() + httpAsyncTaskConfiguration.cachePeriod);
    }

    @Override
    final public String extendableGetCacheKey() {
        return ImageCache.getLocalCacheHash(url);
    }

    /**
     * check if image is already being downloaded and in such a case add the holder to the map so that when loading
     * is finished, the view get update
     * 
     * @param url
     * @param imageView
     */
    final private void registerHolder(String url, ImageView imageView) {
        if (taskDone) return;

        imageView.setImageBitmap(null);
        beingLoaded.put(imageView, url);
    }

    @Override
    final protected byte[] extendedPostDoInBackground(byte[] result) {
        return result;
    }

    private Bitmap scaleByBoth(float w, float h, Bitmap bitmap) {
        if (w < bitmap.getWidth() && h < bitmap.getHeight()) {
            float wr = w / bitmap.getWidth();
            float hr = h / bitmap.getHeight();
            float fr = Math.max(wr, hr);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int)(fr * bitmap.getWidth()), (int)(fr * bitmap.getHeight()), false);
//            Log.debug("bmp, scalebyboth: " + fr + "w: " + bitmap.getWidth() + ", h: " + bitmap.getHeight());
        }
        return bitmap;
    }

    private Bitmap scaleByOne(float viewDimension, int bitmapDimension, Bitmap bitmap) {
        if (viewDimension < bitmapDimension) {
            float fr = viewDimension / bitmapDimension;
            bitmap = Bitmap.createScaledBitmap(bitmap, (int)(fr * bitmap.getWidth()), (int)(fr * bitmap.getHeight()), false);
//            Log.debug("bmp, scalebyone: " + fr + "w: " + bitmap.getWidth() + ", h: " + bitmap.getHeight());
        }
        return bitmap;
    }

    @Override
    final public void onSuccess(byte[] result) {
        if (taskDone) return;

        final Options options = new Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length, options);
        if (bitmap == null) {
            //RemoveFile invalid cache.
            if (httpAsyncTaskConfiguration.cache != null) {
                httpAsyncTaskConfiguration.cache.remove(extendableGetCacheKey());
            }
            return;
        }

        //scale the bitmap for imageView so that we will hold only the required size in memcache
        if (imageView != null) {
            float w = Math.max(imageView.getMeasuredWidth(), (imageView.getLayoutParams() == null) ? 0 : imageView.getLayoutParams().width);
            float h = Math.max(imageView.getMeasuredHeight(), (imageView.getLayoutParams() == null) ? 0 : imageView.getLayoutParams().height);
            if (w > 1 && h > 1) {
                bitmap = scaleByBoth(w, h, bitmap);
            }
            else if (w > 1 || h > 1) {
                bitmap = scaleByOne(Math.max(w,h), (w > h) ? bitmap.getWidth() : bitmap.getHeight() , bitmap);
            }
        }

        if (this.imageView == null) {
            onBitmapReady(bitmap);
            return;
        }

        if (this.url.equals(beingLoaded.get(imageView))) {
            imageView.setImageBitmap(bitmap);
            beingLoaded.remove(imageView);
            final Bitmap bitmap_f = bitmap;
            //as the imageView size is used to create a hash for cache, wait till the layout is done before continue
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    onBitmapReady(bitmap_f);
                }
            });
        }
        else {
            onBitmapReady(bitmap);
        }
    }

    public void onBitmapReady(Bitmap bitmap) {};
    
    @Override
    final public void extendedOnFailed(FailHolder failHolder) {
        if (imageView != null) {
            beingLoaded.remove(this.imageView); //do not leak view/context
        }
        primitiveOnFailed(failHolder);
    }

    protected void primitiveOnFailed(FailHolder failHolder) {}
}
