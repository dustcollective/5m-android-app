package com.m5.android.avicola.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import com.hanacek.android.utilLib.cache.ImageCache;
import com.hanacek.android.utilLib.ui.view.PresetSizeImageView;
import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.util.IntentUtil;

public class InterstitialView extends PresetSizeImageView {

    public interface InterstitialStateListener {
        public void onShow();
        public void onHide();
    }

    public InterstitialView(Context context) {
        super(context);
        init();
    }

    public InterstitialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InterstitialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        presetDimensions(AppContext.getDisplayWidth(), AppContext.getDisplayHeight());
    }

    public void displayImage(int delay, String url, InterstitialStateListener interstitialStateListener) {
        displayImage(delay, url, interstitialStateListener, null);
    }

    /**
     *
     * @param delay in milliseconds
     * @param url image url
     * @param link on click link
     */
    public void displayImage(final int delay, String url, final InterstitialStateListener interstitialStateListener, final String link) {
        AppContext.imageCache().displayImage(url, new ImageCache.OnImageLoadedInterface() {
            @Override
            public void onImageLoaded(String url, Bitmap bitmap) {

                if (interstitialStateListener != null) {
                    interstitialStateListener.onShow();
                }

                setImageBitmap(bitmap);
                if (link != null) {
                    setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            IntentUtil.browser(getContext(), link);
                        }
                    });
                }
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setImageBitmap(null);
                        setOnClickListener(null);

                        if (interstitialStateListener != null) {
                            interstitialStateListener.onHide();
                        }
                    }
                }, delay);
            }
        });
    }
}
