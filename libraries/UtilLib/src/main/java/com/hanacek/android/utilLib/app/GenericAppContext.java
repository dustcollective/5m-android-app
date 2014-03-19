package com.hanacek.android.utilLib.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.hanacek.android.utilLib.util.Util;

public class GenericAppContext {

    /**
     * application context
     */
    protected static Context context;

    public static void initialize(Context context) {
        GenericAppContext.context = context;
    }

    public static Context context() {
        return context;
    }

    public static String getDeviceUid() {
        String uid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

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
                v.getLayoutParams().width = (int)((float)getDisplayWidth()/100*relativeWidth);
            }
        });
    }

    public static int getNaturalDeviceOrientation() {
        WindowManager windowManager =  (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Configuration config = context.getResources().getConfiguration();

        int rotation = windowManager.getDefaultDisplay().getRotation();

        if ( ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && config.orientation == Configuration.ORIENTATION_LANDSCAPE) ||
                ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
            return Configuration.ORIENTATION_LANDSCAPE;
        }
        else {
            return Configuration.ORIENTATION_PORTRAIT;
        }
    }

    /**
     * 1 for mdpi, 1.5 for hdpi (S1), 2 for xhdpi (S3), 3 for xxhdpi (nexus 5), 4 for xxxhdpi (2K)
     *
     * @return
     */
    public static float getDisplayDensity() {
        return context.getResources().getDisplayMetrics().density;
    }

    public static String getAppVersion() {
        try {
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            return versionName + " ("+versionCode+")";
        }
        catch (Exception ignore) {
            return null;
        }
    }

    public static int getAppVersionCode() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }
        catch (Exception ignore) {
            return -1;
        }
    }

    public static String getAppName() {
        try {
            int label = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.labelRes;
            return context.getString(label);
        }
        catch (Exception ignore) {
            return null;
        }
    }
}
