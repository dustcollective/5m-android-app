package com.m5.android.avicola.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Point;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.hanacek.android.utilLib.app.GenericAppContext;
import com.hanacek.android.utilLib.cache.Cache;
import com.hanacek.android.utilLib.cache.CacheConfiguration;
import com.hanacek.android.utilLib.cache.ImageCache;
import com.hanacek.android.utilLib.tasks.HttpAsyncTaskConfiguration;
import com.hanacek.android.utilLib.util.Log;
import com.hanacek.android.utilLib.util.Util;
import com.m5.android.avicola.app.modConfig.AbstractModConfig;
import com.m5.android.avicola.db.dao.FavoriteDao;
import com.m5.android.avicola.file.InternalStorage;
import com.m5.android.avicola.tracking.GoogleAnalytics;
import com.m5.android.avicola.util.Cfg;

public class AppContext extends GenericAppContext {

    private static Prefs prefs;
    private static Cache cache;
    private static ImageCache imageCache;
    private static InternalStorage internalStorage;
    private static HttpAsyncTaskConfiguration httpAsyncTaskConfiguration;

    private static FavoriteDao favoriteDao;
    private static AbstractModConfig modConfig;
    private static GoogleAnalytics ga;

    /**
     * application context
     *
     * @param context
     */
    public static void initialize(Context context) {
        GenericAppContext.initialize(context);

        //logger
        Log.initialize("m5", 100);
        
        //global access objects
        prefs           = new Prefs(context);
        internalStorage = new InternalStorage(context);
        cache           = new Cache(internalStorage, new CacheConfiguration.Builder().build());
        cache.clearExpiredCache();

        httpAsyncTaskConfiguration = createDefaultHttpAsyncTaskConfigurationBuilder().build();
        imageCache      = new ImageCache(new HttpAsyncTaskConfiguration.Builder().setCache(cache).setUseHeaderExpiresAt(false)
                .setCachePeriod(Cfg.IMAGE_DEFAULT_CACHE_EXPIRE).build());
        ga = new GoogleAnalytics(context);

        favoriteDao = new FavoriteDao(context);
    }

    public static Context context() {
        return context;
    }

    public static GoogleAnalytics ga() {
        return ga;
    }

    public static AbstractModConfig modConfig() {
        return modConfig;
    }

    public static void setModConfig(AbstractModConfig modConfig) {
        AppContext.modConfig = modConfig;
    }

    public static FavoriteDao favoriteDao() {
        return favoriteDao;
    }

    public static ImageCache imageCache() {
        return imageCache;
    }

    public static HttpAsyncTaskConfiguration.Builder createDefaultHttpAsyncTaskConfigurationBuilder() {
        return new HttpAsyncTaskConfiguration.Builder().setCache(cache).setUseHeaderExpiresAt(true);
    }

    public static HttpAsyncTaskConfiguration httpAsyncTaskConfiguration() {
        return httpAsyncTaskConfiguration;
    }

    public static Prefs prefs() {
        return prefs;
    }
	
    public static Cache cache() {
        return cache;
    }

    public static InternalStorage internalStorage() {
        return internalStorage;
    }
	
	public static String getDeviceUid() {
	    String uid = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        
        if (TextUtils.isEmpty(uid)) {
            uid += Util.getDeviceHardwareUid();
        }
        uid = Util.getMd5Hash(uid);
        
        while (uid.length() < 32) {
            uid = "0" + uid;
        }
        
        return uid;
	}

    @SuppressWarnings("deprecation")
    /**
     * The size without the status bar (bottom), but includes action bar (top)
     */
    public static int getDisplayWidth() {
	    final Display display = getDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
        else {
            return display.getWidth();
        }
	}

    @SuppressWarnings("deprecation")
    /**
     * The size without the status bar (bottom), but includes action bar (top)
     */
    public static int getDisplayHeight() {
        final Display display = getDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            return size.y;
        }
        else {
            return display.getHeight();
        }
    }

    @SuppressWarnings("deprecation")
    /**
     * The size without the status bar (bottom), but includes action bar (top)
     */
    public static Point getDisplaySize() {
        final Display display = getDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            return size;
        }
        else {
            return new Point(display.getWidth(), display.getHeight());
        }
    }

    @SuppressWarnings("deprecation")
    /**
     * The size with the status bar (bottom) and action bar (top)
     */
    public static Point getDisplayRealSize() {
        final Display display = getDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getRealSize(size);
            return size;
        }
        else {
            return new Point(display.getWidth(), display.getHeight());
        }
    }
	
	public static Display getDisplay() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); 
        return wm.getDefaultDisplay();
    }

    @SuppressWarnings("deprecation")
    public static boolean isLandscape() {
        final Display display = getDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            return (size.x > size.y) ? true : false;
        }
        else {
            return (display.getWidth() > display.getHeight()) ? true : false;
        }
	}
	
	public static int px2dp(float px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    public static int dp2px(float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
    
    public static boolean isDev() {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    /**
     * @param v the view to be changed
     * @param relativeWidth 1-100
     */
    public static void setViewRelativeWidth(final View v, final int relativeWidth) {
        v.post(new Runnable() {
            @Override
            public void run() {
                v.getLayoutParams().width = (int)((float)AppContext.getDisplayWidth()/100*relativeWidth);
            }
        });
    }
}
