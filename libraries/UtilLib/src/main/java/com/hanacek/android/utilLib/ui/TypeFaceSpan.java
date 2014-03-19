package com.hanacek.android.utilLib.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Style a {@link Spannable} with a custom {@link Typeface}.
 *
 * @author Tristan Waddington
 */
public class TypeFaceSpan extends MetricAffectingSpan {

    private Typeface mTypeface;

    /**
     * Load the {@link Typeface} and apply to a {@link Spannable}.
     */
    public TypeFaceSpan(Context context, String typefaceName) {
        mTypeface = TypeFaceFactory.getTypeFace(typefaceName);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(mTypeface);

        // Note: This flag is required for proper typeface rendering
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(mTypeface);

        // Note: This flag is required for proper typeface rendering
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}