package com.hanacek.android.utilLib.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Locale;

import com.hanacek.android.utilLib.R;
import com.hanacek.android.utilLib.ui.TypeFaceFactory;
import com.hanacek.android.utilLib.util.Log;

public class CustomTypeFaceTextView extends TextView {

    public final static float NORMAL_LETTER_SPACING = 0;
    
    private float letterSpacing;
    private boolean textAllCaps;
    
    public CustomTypeFaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processCustomAttributes(context, attrs);
    }

    public CustomTypeFaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        processCustomAttributes(context, attrs);
    }

    public CustomTypeFaceTextView(Context context) {
        super(context);
    }

    private void processCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.CustomTypeFaceTextView);
            this.letterSpacing = a.getFloat(R.styleable.CustomTypeFaceTextView_letterSpacing, NORMAL_LETTER_SPACING);
            String customFont = a.getString(R.styleable.CustomTypeFaceTextView_customFont);
            this.textAllCaps = a.getBoolean(R.styleable.CustomTypeFaceTextView_textAllCapsLegacy, false);
            if (!TextUtils.isEmpty(customFont)) {
                setTypeface(TypeFaceFactory.getTypeFace(customFont));
            }
            setText(getText());
        }
        catch (Exception e) {
            Log.error(e);
        } finally {
            if (a != null) {
                a.recycle();
            }
        }

        if (this.letterSpacing != NORMAL_LETTER_SPACING) {
            applyLetterSpacing(getText());
        }
    }
    
    @Override
    public void setText(CharSequence text, BufferType type) {
        if (textAllCaps && text != null) {
            text = text.toString().toUpperCase(Locale.GERMANY);
        }
        super.setText(text, type);
    }

    public void setCustomFont(String customFont) {
        setTypeface(TypeFaceFactory.getTypeFace(customFont));
    }
    
    public void setLetterSpacedText(CharSequence text) {
        applyLetterSpacing(text);
    }
    
    private void applyLetterSpacing(CharSequence text) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < text.length(); i++) {
            builder.append(text.charAt(i));
            if(i+1 < text.length()) {
                builder.append(" ");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if(builder.toString().length() > 1) {
            for(int i = 1; i < builder.toString().length(); i+=2) {
                finalText.setSpan(new ScaleXSpan((letterSpacing+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }
}
