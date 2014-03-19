package com.m5.android.avicola.ui.view;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;

/**
 * Temporary fix for the problem with
 *
 * Rendering Problems Exception raised during rendering: DrawerLayout must be measured with MeasureSpec.EXACTLY. Only on Android 4.3
 */
public class FixDrawerLayout extends DrawerLayout {

    public FixDrawerLayout(Context context) {
        super(context);
    }

    public FixDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
