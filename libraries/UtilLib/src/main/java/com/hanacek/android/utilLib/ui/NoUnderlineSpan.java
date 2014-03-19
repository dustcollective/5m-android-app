package com.hanacek.android.utilLib.ui;

import android.graphics.Color;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.URLSpan;

public class NoUnderlineSpan extends URLSpan{
    private static final String STERN_RED_COLOUR = "#ED1C24";

    private NoUnderlineSpan(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setColor(Color.parseColor(STERN_RED_COLOUR));
    }

    public static Spannable removeUnderlines(Spannable spanText) {
        URLSpan[] spans = spanText.getSpans(0, spanText.length(), URLSpan.class);

        for(URLSpan span:spans) {
            int start = spanText.getSpanStart(span);
            int end = spanText.getSpanEnd(span);
            spanText.removeSpan(span);
            span = new NoUnderlineSpan(span.getURL());
            TextPaint p = new TextPaint();
            spanText.setSpan(span, start, end, 0);
        }
        return spanText;
    }

    public static Spannable removeUnderlines(Spanned spannedText) {
        return removeUnderlines((Spannable) spannedText);
    }

    public static Spannable removeUnderlines(CharSequence charSequence) {
        return removeUnderlines((Spannable) charSequence);
    }
}
