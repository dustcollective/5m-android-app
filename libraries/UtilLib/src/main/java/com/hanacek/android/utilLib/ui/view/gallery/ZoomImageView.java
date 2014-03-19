package com.hanacek.android.utilLib.ui.view.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ZoomImageView extends ImageView {

    private static final int SCALE_MIN = 1;
    private static final int SCALE_MAX = 3;
    private boolean isZoomedIn;
    
    private ScaleListener scaleListener;
    private ScaleGestureDetector scaleGestureDetector;
    private TouchListener touchListener;
    private GestureDetector gestureDetector;
    
    protected OnClickListener onClickListener;
    protected OnImageZoomedListener onZoomListener;
    
    protected float scale = SCALE_MIN;
    protected PointF scalePoint;
    protected PointF translatePoint;
    private Matrix matrix;
    
    private int measuredForWidth;
    /** width to height, e.g. 800w x 600h = 1,33 */
    private float sidesRatio;
    private int drawableBaseWidth, drawableBaseHeight;
    private boolean isWidthLimiting;

    public interface OnImageZoomedListener {
        public void onImageZoomed(boolean isZoomed);
    }
    
    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomImageView(Context context) {
        super(context);
        init();
    }
    
    private void init() {
        this.scaleListener = new ScaleListener();
        this.scaleGestureDetector = new ScaleGestureDetector(getContext(), this.scaleListener);
        this.touchListener = new TouchListener();
        this.gestureDetector = new GestureDetector(getContext(), this.touchListener);
        
        this.translatePoint = new PointF();
        this.scalePoint = new PointF();
        this.matrix = new Matrix();
        
        // on Android 4.3, this was causing images not being stretched to fill parent and not being centered
        //setScaleType(ScaleType.MATRIX);
    }
    
    @Override
    public void setImageBitmap(Bitmap bm){
        super.setImageBitmap(bm);
        sidesRatio = ((float) getDrawable().getIntrinsicWidth()) / getDrawable().getIntrinsicHeight();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if image is scaled up, forbid viewpager to scroll
        if (scale != SCALE_MIN) {
            ((ViewGroup)getParent()).requestDisallowInterceptTouchEvent(true);
        }
        
        boolean pinch = false;
        
        if (event.getPointerCount() == 2 && scale == SCALE_MIN) {
            scalePoint.x = (event.getX(0) + event.getX(1))/2;
            scalePoint.y = (event.getY(0) + event.getY(1))/2;
            pinch = true;
        }
        
        if (event.getPointerCount() == 2 && this.scaleGestureDetector.onTouchEvent(event)) {
            pinch = true;
            return true;
        }
                
        if (this.gestureDetector.onTouchEvent(event) && !pinch) {
            return true;
        }
        
        return super.onTouchEvent(event);
    }
    
    private class TouchListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (scale == SCALE_MIN) {
                return false;
            }
            
            translatePoint.x -= distanceX;
            translatePoint.y -= distanceY;
            
            invalidate();
            
            return true;
        }
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (onClickListener != null) {
                onClickListener.onClick(ZoomImageView.this);
                return true;
            }
            
            return false;
        }
        
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            scale = isZoomedIn ? SCALE_MIN : SCALE_MAX;
            isZoomedIn = !isZoomedIn;
            track(isZoomedIn);
            scalePoint.x = e.getX();
            scalePoint.y = e.getY();
            
            invalidate();
            return true;
        }
    }
    
    private class ScaleListener extends SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scale *= scaleFactor;
            if (scale < SCALE_MIN) {
                scale = SCALE_MIN;
            }
            else if (scale > SCALE_MAX) {
                scale = SCALE_MAX;
            }
            
            if (!isZoomedIn && scale > SCALE_MIN) {
                isZoomedIn = true;
                track(isZoomedIn);
            } else if (isZoomedIn && scale == SCALE_MIN) {
                isZoomedIn = false;
                track(isZoomedIn);
            }
            
            invalidate();
            
            return super.onScale(detector);
        }
    }
    
    @Override
    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
        
        //without setting a fake empty useless onclicklistener, the touch does not work (don't know why)
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        measureDrawable();
        
        matrix.setScale(scale, scale, scalePoint.x, scalePoint.y);
        float[] pts = new float[] {0,0, getWidth(), getHeight()};
        matrix.mapPoints(pts);
        
        //do not overscroll image from the screen
        float minX = Math.abs(pts[0]/scale);
        float minY = Math.abs(pts[1]/scale);
        float maxX = -pts[2]/scale+getWidth()/scale;
        float maxY = -pts[3]/scale+getHeight()/scale;
        
        if (isWidthLimiting) {
            pts[0] = 0;
            pts[2] = getWidth();
            if (drawableBaseHeight * scale > getHeight()) {
                pts[1] = getHeight()/2-drawableBaseHeight/2;
                pts[3] = (getHeight()/2+drawableBaseHeight/2);
            } else {
                pts[1] = 0;
                pts[3] = getHeight();
            }
        } else {
            pts[1] = 0;
            pts[3] = getHeight();
            if (drawableBaseWidth * scale > getWidth()) {
                pts[0] = getWidth()/2-drawableBaseWidth/2;
                pts[2] = (getWidth()/2+drawableBaseWidth/2);
            } else {
                pts[0] = 0;
                pts[2] = getWidth();
            }
        }
        matrix.mapPoints(pts);
        minX = Math.abs(pts[0]/scale);
        minY = Math.abs(pts[1]/scale);
        maxX = -pts[2]/scale+getWidth()/scale;
        maxY = -pts[3]/scale+getHeight()/scale;
        
        translatePoint.x = Math.max(Math.min(translatePoint.x, minX), maxX);
        translatePoint.y = Math.max(Math.min(translatePoint.y, minY), maxY);
        
        canvas.scale(scale, scale, scalePoint.x, scalePoint.y);
        canvas.translate(translatePoint.x, translatePoint.y);
        
        super.onDraw(canvas);
    }
    
    /**
     * image has to have fill_parent on both axis so that this method works properly
     */
    private void scaleUp() {
        if (getDrawable() == null) {
            return;
        }
        
        float maxWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        float maxHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        final float drawableHeight = getDrawable().getIntrinsicHeight();
        final float drawableWidth = getDrawable().getIntrinsicWidth();
        
        if (drawableWidth < 1 || maxWidth < 1) {
            return;
        }
        this.measuredForWidth = getMeasuredWidth();
        
        //Matrix matrix = getImageMatrix(); 
        float scaleFactorWidth = maxWidth / drawableWidth;
        float scaleFactorHeight = maxHeight / drawableHeight;
        float scale = Math.min(scaleFactorWidth, scaleFactorHeight);
        this.matrix.setScale(scale, scale, 0, 0);
        
        float scaledWidth = drawableWidth * scale;
        float offsetX = (maxWidth/2-scaledWidth/2)/scale;
        float scaledHeight = drawableHeight * scale;
        float offsetY = (maxHeight/2-scaledHeight/2)/scale;
        
        this.matrix.preTranslate(offsetX, offsetY);
        
//        Log.debug("scale: " + scale);
//        Log.debug("scaledWidth: " + scaledWidth);
//        Log.debug("offsetX: " + offsetX);
//        Log.debug("maxWidth: " + maxWidth);
//        Log.debug("drawableWidth: " + drawableWidth);
        
//        Log.debug("Image - scaleUp() - new height: " + this.height);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.measuredForWidth != getMeasuredWidth()) {
            scaleUp();
        }
        
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    /**
     * Measures the width and height of not zoomed image drawable, taking into consideration device's orientation
     * and stretch of image to fill parent
     */
    private void measureDrawable(){
        /** drawable is wider than available display area => black strips over and under the drawable, display width is the limit factor */
        if (sidesRatio > ((float) getWidth()/getHeight())) {
            isWidthLimiting = true;
            drawableBaseWidth = getWidth();
            drawableBaseHeight = (int) (drawableBaseWidth / sidesRatio + 0.5f);
        } else {
            isWidthLimiting = false;
            drawableBaseHeight = getHeight();
            drawableBaseWidth = (int) (sidesRatio * drawableBaseHeight + 0.5f);
        }
        //Log.debug("zoom image: drawable base dimensions: " + drawableBaseWidth + "*" + drawableBaseHeight);
    }
    
    private void track(boolean isZoomedIn) {
        if (onZoomListener != null) {
            onZoomListener.onImageZoomed(isZoomedIn);
        }
    }

    public void setOnZoomedListener(OnImageZoomedListener listener) {
        this.onZoomListener = listener;
    }
}
