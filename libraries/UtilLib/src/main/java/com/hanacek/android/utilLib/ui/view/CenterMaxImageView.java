package com.hanacek.android.utilLib.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.widget.ImageView;

import com.hanacek.android.utilLib.R;

/**
 * scale up image until it reach width of the view (keep aspect ratio)
 */
public class CenterMaxImageView extends ImageView {

    private boolean isPreMeasured;
    private boolean isFinalMeasured;
    private boolean isPercentageIncluded;
    
    private float presetHeight;
    private float presetWidth;
    
    private int finalHeight;
    private int finalWidth;
    
    private int maxHeight;
    private int maxWidth;
    
    /**
     * in case landscape format of image should have different right margin
     */
    private int marginLandscapeRight;
    
    /**
     * For scaling image to different display widths
     */
    private float widthInPercents;
    
    public CenterMaxImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CenterMaxImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CenterMaxImageView(Context context) {
        super(context);
        init(null);
    }
    
    private void init(AttributeSet attrs) {
        setScaleType(ScaleType.MATRIX);
        
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CenterMaxImageView);
            this.marginLandscapeRight = a.getDimensionPixelSize(R.styleable.CenterMaxImageView_marginLandscapeRight, -1);
            this.widthInPercents = a.getFloat(R.styleable.CenterMaxImageView_widthInPercents, -1);
            a.recycle();
        }

        setBackgroundColor(getResources().getColor(android.R.color.black));
    }
        
    private void scaleUp() {
        final float drawableWidth = getDrawable().getIntrinsicWidth();
        final float drawableHeight = getDrawable().getIntrinsicHeight();
        
        if (drawableWidth < 1 || drawableHeight < 1) {
            return;
        }
        
        handleLandscapeFormat(drawableWidth, drawableHeight);
        
//        Log.debug("Image - scaleUp() inside");
        
        if (measureDimensions(drawableWidth, drawableHeight)) {
            isPreMeasured = true;
            isFinalMeasured = true;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        Log.debug("Image - onSizeChanged() - w: " + w + ", oldw:" + oldw + ", h:" + h + ", oldh:" + oldh);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        Log.debug("Image - onLayout() - left: " + left + ", right:" + right + ", top: " + top + ", bottom: " + bottom);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.debug("Image - onMeasure(), isPreMeasured: " + isPreMeasured + ", presetWidth: " + presetWidth + ", presetHeight: " + presetHeight);
        
        if (!isFinalMeasured && getDrawable() != null) {
//            Log.debug("Image - onMeasure() - call scaleUp()");
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            scaleUp();
        }

        if (!isPreMeasured && presetWidth != 0 && presetHeight != 0) {
//            Log.debug("Image - onMeasure() - call preMeasure()");
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            preMeasure();
        }
        
        if (finalHeight != 0 && finalWidth != 0) {
            int w = (maxWidth > 0) ? Math.min(finalWidth, maxWidth) : finalWidth;
            int h = (maxHeight > 0) ? Math.min(finalHeight, maxHeight) : finalHeight;
//            Log.debug("Image - onMeasure(), final measurement, w: " + w + ", h: " + h);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }
        
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    private void preMeasure() {
        if (measureDimensions(presetWidth, presetHeight)) {
            isPreMeasured = true;
        }
    }
    
    /**
     * get the width from internal view measurements (margins are already subtracted)
     * 
     * @param originalWidth
     * @param originalHeight
     * @return
     */
    private boolean measureDimensions(float originalWidth, float originalHeight) {
        float availableWidth = getMeasuredWidth();
//        Log.debug("Image -  mesureDimensions() 1 - availableWidth: " + availableWidth + "original width: " + originalWidth);
        if (availableWidth < 1) {
            return false;
        }
        availableWidth -= (getPaddingLeft() + getPaddingRight());
//        Log.debug("Image -  mesureDimensions() 2 - availableWidth: " + availableWidth + "original width: " + originalWidth);
        doMeasurement(originalWidth, originalHeight, availableWidth);
        return true;
    }
    
    /**
     * @param originalWidth
     * @param originalHeight
     * @param maxWidth preset width when we cannot wait for internal measurement 
     *          (usually it is in listviews and the value is simply a screen width)
     * @return
     */
    private boolean measureDimensions(float originalWidth, float originalHeight, float maxWidth) {
        
        maxWidth -= deductEdges(this);
        
        View v = this;
        while (v.getParent() != null) {
            ViewParent parent = v.getParent();
            if ((parent instanceof ViewGroup) == false) {
                break;
            }
            maxWidth -= deductEdges((View)parent);
            v = (View) parent;
        }
        
        doMeasurement(originalWidth, originalHeight, maxWidth);
        return true;
    }
    
    private int deductEdges(View v) {
        int edge = 0;
        if (v.getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams mlp = (MarginLayoutParams) v.getLayoutParams();
            if (mlp != null) {
                edge += (mlp.rightMargin + mlp.leftMargin);
            }
        }
        edge += (v.getPaddingLeft() + v.getPaddingRight());
        return edge;
    }
    
    private void doMeasurement(float originalWidth, float originalHeight, float availableWidth) {
//        Log.debug("Image - original width: " + originalWidth + ", height: " + originalHeight + ", availableWidth: " + availableWidth);
        float percentsScale = 1;
        if (widthInPercents > 0 && widthInPercents < 100 && !isPercentageIncluded) {
            isPercentageIncluded = true;
            percentsScale = widthInPercents / 100f;
        }
        
        float scaleFactor = availableWidth * percentsScale / originalWidth;
        
        Matrix matrix = getImageMatrix(); 
        matrix.setScale(scaleFactor, scaleFactor, 0, 0);
        setImageMatrix(matrix);
        
        this.finalHeight = (int) (scaleFactor * originalHeight) + (getPaddingTop() + getPaddingBottom());
        this.finalWidth = (int) (scaleFactor * originalWidth) + (getPaddingLeft() + getPaddingRight());
        
//        Log.debug("Image - final width: " + finalWidth + ", height: " + finalHeight);
    }
    
    /**
     * preset original image dimensions so that the view can hold the future size before the image itself is loaded
     *
     * You have to use match_parent in layouts so that this method can take effect
     * 
     * @param width
     * @param height
     */
    public void presetDimensions(float width, float height) {
        this.presetWidth = width;
        this.presetHeight = height;
        handleLandscapeFormat(presetWidth, presetHeight);
    }
    
    /**
     * preset original image dimensions so that the view can hold the future size before the image itself is loaded
     * 
     * @param width
     * @param height
     */
    public void presetDimensions(float width, float height, float maxWidth) {
        presetDimensions(width, height);
        measureDimensions(width, height, maxWidth);
    }
    
    /**
     * if marginLandscapeRight is set and image format is landscape we need to override the right margin of the image
     * 
     * @param width
     * @param height
     */
    private void handleLandscapeFormat(float width, float height) {
        if (width / height > 1.1 && marginLandscapeRight != -1) {
            ((MarginLayoutParams) getLayoutParams()).rightMargin = marginLandscapeRight;
        }
    }
    
    public void setMaxDimensions(int w, int h) {
        maxWidth = w;
        maxHeight = h;
    }
}
