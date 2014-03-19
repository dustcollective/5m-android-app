package com.hanacek.android.utilLib.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

public class ViewUtil {

    @SuppressWarnings("deprecation")
    public static void removeLayoutListenerFromViewTreeObserver(ViewTreeObserver viewTreeObserver, ViewTreeObserver.OnGlobalLayoutListener layoutListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewTreeObserver.removeOnGlobalLayoutListener(layoutListener);
        }
        else {
            viewTreeObserver.removeGlobalOnLayoutListener(layoutListener);
        }
    }

    @SuppressLint("NewApi")
    public static void hideStatusBar(Context context, Window window) {
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = window.getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
