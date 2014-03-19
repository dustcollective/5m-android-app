package com.hanacek.android.utilLib.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hanacek.android.utilLib.util.Log;

public class PresetSizeImageView extends ImageView {

    private float presetHeight;
    private float presetWidth;

    private boolean isShowingLogs;
    private int pressedOverlayColor;

    public PresetSizeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PresetSizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PresetSizeImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setBackgroundColor(getResources().getColor(android.R.color.black));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        log("Image - onMeasure(), presetWidth: " + presetWidth + ", presetHeight: " + presetHeight);

        if (presetWidth != 0 && presetHeight != 0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int)presetWidth, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int)presetHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void presetDimensions(float width, float height) {
        this.presetWidth = width;
        this.presetHeight = height;
    }

    /**
     * Turn logs only for some images
     *
     * @param log
     */
    public void setLogging(boolean log){
        this.isShowingLogs = log;
    }

    private void log(String message) {
        if (isShowingLogs) {
            Log.debug(message);
        }
    }

    /**
     * On ImageView pressed it will draw a color over the image
     *
     * Put here R.color.{x}
     * Do not put here getContext().getResources().getColor(R.color.{x})
     */
    public void setPressedOverlayColor(int color) {
        this.pressedOverlayColor = color;
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressedOverlayColor != 0) {
            if (pressed) {
                setColorFilter(getContext().getResources().getColor(pressedOverlayColor), PorterDuff.Mode.SRC_ATOP);
            }
            else {
                clearColorFilter();
            }
        }
        super.setPressed(pressed);
    }

    /**
     * If icon is needed to be shown over the image, extends this class and override this method
     *
     * @return
     */
    protected Bitmap getIcon() {
        return null;
    }

    final protected Bitmap bitmapFromResource(int resourceId) {
        return BitmapFactory.decodeResource(getContext().getResources(), resourceId);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        log("Image - onDraw()");

        //show icon if set
        Bitmap icon = getIcon();
        if (icon != null) {
            int height = getMeasuredHeight();
            int width = getMeasuredWidth();

            log("Image - icon is not null");
            //check if we have to scale the bitmap
            float heightOverlap = (float)height / (20 + icon.getHeight());
            float widthOverlap = (float)width / (20 + icon.getWidth());
            float maxOverlap = Math.min(heightOverlap, widthOverlap);
            if (maxOverlap < 1) { //scale needed
                int scaledHeight = (int) (icon.getHeight() * maxOverlap);
                int scaledWidth = (int) (icon.getWidth() * maxOverlap);

                log("Image - scale icon to width: " + scaledWidth + ", height: " + scaledHeight);
                icon = Bitmap.createScaledBitmap(icon, scaledWidth, scaledHeight, true);
            }

            log("Image - draw icon with canvas height: " + height + ", width: " + width);
            int topOffset = height / 2 - icon.getHeight() / 2;
            int leftOffset = width / 2 - icon.getWidth() / 2;
            log("Image - draw icon with topOffset: " + topOffset + ", leftOffset: " + leftOffset);
            canvas.drawBitmap(icon, leftOffset, topOffset, null);
            icon.recycle();
        }
    }
}
