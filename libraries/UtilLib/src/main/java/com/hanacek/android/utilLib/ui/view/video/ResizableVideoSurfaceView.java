package com.hanacek.android.utilLib.ui.view.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hanacek.android.utilLib.R;
import com.hanacek.android.utilLib.app.Constants;
import com.hanacek.android.utilLib.app.GenericAppContext;
import com.hanacek.android.utilLib.model.MeasurableInterface;
import com.hanacek.android.utilLib.util.IntentUtil;
import com.hanacek.android.utilLib.util.Log;

/**
 * The required view tree is
 *
 * <anchorView><backgroundView>this<backgroundView/><anchorView/>
 *
 * AnchorView and backgroundView have to be both relativeLayouts and set from outside
 * Because of fullscreen: anchorView can have padding, both of them cannot have margins, any parents of anchorView are forbidden to have padding and margins
 * AnchorView is used for controllers to appear on the bottom of the view
 * BackgroundView should usually have the black background so that in fullscreen or predefined non fullscreen size when video keeps its aspect ration, the black
 * stripes are visible.
 */
public class ResizableVideoSurfaceView extends SurfaceView implements MediaPlayer.OnBufferingUpdateListener	, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener	, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener, FixedMediaController.FixedMediaPlayerControl {

    public static final String LOG_INSTANCE = "video";

    public interface VideoItem extends MeasurableInterface {
        public String getSrc();
    }

    public interface OnStartRequestedListener {
        public void onStartRequested();
    }

    private OnStartRequestedListener onStartRequestedListener;

    /**
     * Sent in the broadcast to know who sent it, because it can change the behaviour
     */
    public enum BroadcastSender {
        /**
         * ResizableVideoSurfaceView
         */
        VIEW,

        /**
         * Component that take care to scroll the full sized view to proper position, forbid scrolling if needed
         */
        VIEW_PARENT,

        /**
         * Component that handles orientation change requests, most likely activity or fragment
         */
        ORIENTATION_MASTER
    }

    private VideoItem videoItem;
    private int width;
    private int height;

    private boolean surfaceReady;
    private MediaPlayer mediaPlayer;

    private MediaPlayer.OnCompletionListener onCompletionListener;
    private FixedMediaController mediaController;
    private OnPlayPauseListener onPlayPauseListener;

    /**
     * Receive requests from outside for video leaving fullscreen.
     */
    private VideoBroadcastReceiver broadcastReceiver;
    private PowerManager.WakeLock wakeLock;
    private boolean fullscreenRequested;

    private int listPosition = -1;
    private RelativeLayout anchorView;
    private RelativeLayout backgroundView;

    private View progress;
    private boolean canScaleUp = true;
    private Rect padding;
    private boolean isPrepared;

    /**
     * When seeking starts, store the url of the video so that when async listener onSeekComplete is called we can find out if we are still playing the same video.
     */
    private String seekInVideo;

    public ResizableVideoSurfaceView(Context context) {
        super(context);
        init();
    }

    public ResizableVideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResizableVideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @SuppressWarnings("deprecation")
    /**
     * SetKeepScreenOn doesn't work at all and is completely ignored on Nexus5 therefore we have to use the deprecated FULL_WAKE_LOCK.
     */
    private void init() {
        //Log.registerInstance(LOG_INSTANCE);

        final PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Video playback");

        final SurfaceHolder surfaceHolder = getHolder();
        if (surfaceHolder != null) {
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    final IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_REQUEST_FULLSCREEN_INVALIDATE);
                    intentFilter.addAction(Constants.BROADCAST_REQUEST_FULLSCREEN);
                    intentFilter.addAction(Constants.BROADCAST_REQUEST_VIDEO_STOP);
                    getContext().registerReceiver(broadcastReceiver, intentFilter);

                    /*
                    Let the onDraw be called to draw black color while switching between videos (while loading new video we do not want to keep last screen
                    from previous video visible).
                    */
                    setWillNotDraw(false);

                    Log.debug(LOG_INSTANCE, "surfaceCreated");
                    surfaceReady = true;
                    prepare();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    Log.debug(LOG_INSTANCE, "surfaceChanged");
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    getContext().unregisterReceiver(broadcastReceiver);
                    release();

                    Log.debug(LOG_INSTANCE, "surfaceDestroyed");
                    surfaceReady = false;
                }
            });
        }

        this.anchorView = (RelativeLayout)getParent();

        mediaController = new FixedMediaController(getContext(), false);
        mediaController.setMediaPlayer(this);

        broadcastReceiver = new VideoBroadcastReceiver(BroadcastSender.VIEW) {
            @Override
            public void fullscreen(int position) {
                if (!isPrepared) {
                    return;
                }

                //orientation changed.
                goFullscreen(true);
                mediaController.toggleFullscreenButton();
                Log.debug("broadcast - view receiver - goFull");
            }

            @Override
            public void fullscreenInvalidate() {
                leaveFullscreen(true);
                mediaController.toggleFullscreenButton();
                Log.debug("broadcast - view receiver - leaveFull");
            }

            @Override
            public void requestVideoStart() {
                if (onStartRequestedListener != null) {
                    onStartRequestedListener.onStartRequested();
                }
            }

            @Override
            public void requestVideoStop() {
                release();
                if (onCompletionListener != null) {
                    onCompletionListener.onCompletion(mediaPlayer);
                }
            }
        };
    }

    public void setRequiredViews(RelativeLayout anchorView, RelativeLayout backgroundView) {
        this.anchorView = anchorView;
        this.backgroundView = backgroundView;
        mediaController.setAnchorView(anchorView);
    }

    public void setProgressView(View progress) {
        this.progress = progress;
    }

    public void setOnStartRequestedListener(OnStartRequestedListener onStartRequestedListener) {
        this.onStartRequestedListener = onStartRequestedListener;
    }

    public void setType(int type) {
        broadcastReceiver.setType(type);
    }

    /**
     * Used when going to the fullscreen
     *
     * @param position
     */
    public void setListPosition(int position) {
        this.listPosition = position;
    }

    public void setItemToBePlayed(VideoItem videoItem, int width, int height) {
        Log.debug(LOG_INSTANCE, "setItemToBePlayed()");
        this.videoItem = videoItem;
        this.width = width;
        this.height = height;
        prepare();
    }

    private void initMediaPlayer() {
        if (mediaPlayer != null) {
            release();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mediaController.toggle();
        return super.onTouchEvent(event);
    }

    private void prepare() {
        Log.debug(LOG_INSTANCE, "prepare()");
        if (this.videoItem == null || !surfaceReady) {
            Log.debug(LOG_INSTANCE, "one of needed items is null, return");
            return;
        }

        try {
            initMediaPlayer();
            mediaPlayer.setDisplay(getHolder());
            mediaPlayer.setDataSource(getContext(), Uri.parse(this.videoItem.getSrc()));
            //mediaPlayer.setDataSource(getContext(), Uri.parse("http://uds.ak.o.brightcove.com/1203065853/1203065853_2877309806001_BundesligaVorschauSpieltag14.mp4"));
            //mediaPlayer.setDataSource(getContext(), Uri.parse("http://brightcove04.brightcove.com/23/68348640001/201401/3755/68348640001_3052634938001_ContentDrifter-KINO-OL.mp4"));
            //mediaPlayer.setDataSource(getContext(), Uri.parse("http://192.168.5.167/clipcanvas_14348_H264_640x360.mp4"));

            mediaPlayer.prepareAsync();
            Log.debug(LOG_INSTANCE, "call prepareAsync(), video url: " + this.videoItem.getSrc());

            if (progress != null) {
                progress.setVisibility(View.VISIBLE);
            }
            invalidate();
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    private void acquireWakeLock() {
        if (!wakeLock.isHeld()) wakeLock.acquire();
    }

    private void releaseWakeLock() {
        if (wakeLock.isHeld()) wakeLock.release();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (onCompletionListener != null) {
            onCompletionListener.onCompletion(mp);
            release();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.error(LOG_INSTANCE, "what: " + what + ", extra: " + extra);

        int messageId = R.string.err_video_generic;
        if ("4.4".equals(Build.VERSION.RELEASE)) { //bug with streaming videos ( https://code.google.com/p/android/issues/detail?id=62304 )
            messageId = R.string.err_video_4_4;
        }

        Toast.makeText(getContext(), messageId, Toast.LENGTH_LONG).show();

        if (progress != null) {
            progress.setVisibility(View.GONE);
        }

        if (onCompletionListener != null) {
            onCompletionListener.onCompletion(mp);
        }
        release();

        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }

        isPrepared = true;
        mp.start();
        acquireWakeLock();
        com.hanacek.android.utilLib.util.IntentUtil.broadcastVideoStarted(getContext(), BroadcastSender.VIEW);
        Log.debug(LOG_INSTANCE, "onPrepared()");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //between different videos, clean the canvas so that when new video is being loaded, the last screen of previous is not on the background
        if (progress != null && progress.getVisibility() == View.VISIBLE) {
            canvas.drawARGB(255, 0, 0, 0);
        }
        super.onDraw(canvas);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (isAlive() && !isPlaying() && videoItem != null && seekInVideo != null && seekInVideo.equals(videoItem.getSrc())) {
            start();
            mediaController.updatePausePlay();
        }
        seekInVideo = null;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        changeSize(this.width, this.height);
    }

    /**
     * Set width and height for the backgroundView view and for the video itself
     *
     * @param width of the backgroundView
     * @param height of the backgroundView
     */
    private void changeSize(int width, int height) {
        Log.debug(LOG_INSTANCE, "changeSize(), backgroundWidth: " + width + ", backgroundHeight: " + height);
        Log.debug(LOG_INSTANCE, "changeSize(), originalWidth: " + videoItem.getWidth() + ", originalHeight: " + videoItem.getHeight());

        int videoWidth = videoItem.getWidth();
        int videoHeight = videoItem.getHeight();
        float widthRatio = (float)width / videoItem.getWidth();
        float heightRatio = (float)height / videoItem.getHeight();
        float finalRatio = Math.min(widthRatio, heightRatio);
        if (canScaleUp) {
            videoWidth = (int) (videoWidth * finalRatio);
            videoHeight = (int) (videoHeight * finalRatio);
        }
        else if (finalRatio < 1) { //only scaleDown
            videoWidth = (int) (videoWidth * finalRatio);
            videoHeight = (int) (videoHeight * finalRatio);
        }

        Log.debug(LOG_INSTANCE, "changeSize(), surfaceWidth: " + videoWidth + ", surfaceHeight: " + videoHeight);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = videoWidth;
        layoutParams.height = videoHeight;
        requestLayout();

        layoutParams = backgroundView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        backgroundView.requestLayout();

        updateAnchorViewSize(width, height);
    }

    /**
     * Avoid fullscreen height when showing the media controls (relative layout alignParentBottom problem), height of the controls parent has to be set, and
     * controls parent is anchorView.
     *
     * @param width
     * @param height
     */
    public void updateAnchorViewSize(int width, int height) {
        final ViewGroup.LayoutParams layoutParams = anchorView.getLayoutParams();
        layoutParams.width = width + anchorView.getPaddingLeft() + anchorView.getPaddingRight();
        layoutParams.height = height + anchorView.getPaddingTop() + anchorView.getPaddingBottom();
        anchorView.requestLayout();
    }

    /* MediaPlayerControl start */

    @Override
    public void start() {
        if (onPlayPauseListener != null) {
            onPlayPauseListener.onResume();
        }
        mediaPlayer.start();
        acquireWakeLock();
        com.hanacek.android.utilLib.util.IntentUtil.broadcastVideoStarted(getContext(), BroadcastSender.VIEW);
    }

    @Override
    public void pause() {
        if (onPlayPauseListener != null) {
            onPlayPauseListener.onPause();
        }
        mediaPlayer.pause();
        releaseWakeLock();
        Log.debug(LOG_INSTANCE, "pause()");
        com.hanacek.android.utilLib.util.IntentUtil.broadcastVideoPaused(getContext(), BroadcastSender.VIEW);
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        seekInVideo = videoItem.getSrc();
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return (mediaPlayer == null) ? false : mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    /**
     * It is not asking if the video can be paused now, for that is the isPlaying() method, it is asking if the video format can handle pause
     * (live streams probably wont)
     *
     * @return in out case always true
     */
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public boolean isAlive() {
        return (mediaPlayer == null) ? false : true;
    }

    @Override
    public void goFullscreen(boolean byBroadcast) {
        if (fullscreenRequested && !byBroadcast) { //we are already in fullscreen and new non broadcast request came, means the fullscreen button was tapped again
            leaveFullscreen(false);
        }
        //still in portrait (button in controls clicked)
        else if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            /**
             * Do nothing, wait for another call when in landscape (activity's broadcast).
             * The reason for this is that size is counted using real display size (with status bar and action bar) and display size (without status bar, with action bar)
             * but on some devices, status bas in portrait has different height than in landscape so while in portrait we cannot resize already for a landscape
             * as we do not know the size.
             */
            IntentUtil.broadcastRequestLandscape(getContext(), BroadcastSender.VIEW, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else { //already in landscape (activity detected orientation change and send broadcast)
            Log.debug("broadcast - view do/send - goFull");
            IntentUtil.broadcastRequestFullscreen(getContext(), BroadcastSender.VIEW, listPosition);
            fullscreenRequested = true;
            anchorView.setPadding(0, 0, 0, 0);
            final Point realDisplaySize = GenericAppContext.getDisplayRealSize(); //with status bar and action bar
            final Point displaySize = GenericAppContext.getDisplaySize(); //without status bar, with action bar

            Log.debug(LOG_INSTANCE, "realDisplaySize.y " + realDisplaySize.y);
            Log.debug(LOG_INSTANCE, "realDisplaySize.x " + realDisplaySize.x);
            Log.debug(LOG_INSTANCE, "displaySize.y " + displaySize.y);
            Log.debug(LOG_INSTANCE, "displaySize.x " + displaySize.x);

            //On some devices (tablets) the bar stays on bottom, while on some (phones) status bar goes to side when in landscape
            int diffX = realDisplaySize.x - displaySize.x;
            int diffY = realDisplaySize.y - displaySize.y;
            int fullScreenWidth;
            int fullScreenHeight;
            if (diffX > diffY) { //phones - side bar
                fullScreenWidth = realDisplaySize.x - diffX;
                fullScreenHeight = realDisplaySize.y;
            }
            else { //tablets - bottom bar
                fullScreenWidth = realDisplaySize.x;
                fullScreenHeight = realDisplaySize.y - diffY;
            }
            changeSize(fullScreenWidth, fullScreenHeight);
        }
    }

    @Override
    public boolean isFullscreen() {
        return fullscreenRequested;
    }

    /* MediaPlayerControl end */

    /**
     * This padding will be set to the anchorView when returning from the fullscreen
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setAnchorViewPadding(int left, int top, int right, int bottom) {
        this.padding = new Rect();
        this.padding.set(left, top, right, bottom);
    }

    /**
     * We want to leave fullscreen mode. This happens when user in fullscreen press the fullscreen button again, when movie finished or when user press back
     * button.
     *
     * @param byBroadcast If the trigger to leave fullscreen came from media controls button broadcast request to invalidate landscape and do nothing, wait for
     *                    broadcast to invalidate fullscreen to avoid resizing of video before other components does their work
     */
    private void leaveFullscreen(boolean byBroadcast) {
        if (!byBroadcast) {
            IntentUtil.broadcastRequestLandscapeInvalidate(getContext(), BroadcastSender.VIEW);
        }
        else {
            fullscreenRequested = false;
            if (padding != null) {
                anchorView.setPadding(padding.left, padding.top, padding.right, padding.bottom);
            }
            changeSize(width, height);
        }
    }

    public void release() {
        if (mediaPlayer == null) { //was already released
            return;
        }

        isPrepared = false;
        releaseWakeLock();
        Log.debug(LOG_INSTANCE, "release()");
        com.hanacek.android.utilLib.util.IntentUtil.broadcastVideoPaused(getContext(), BroadcastSender.VIEW);

        if (fullscreenRequested) {
            leaveFullscreen(false);
        }

        if (progress != null) {
            progress.setVisibility(View.GONE);
        }

        mediaPlayer.release();
        mediaPlayer = null;

        mediaController.hide();
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }

    public void setOnPlayPauseListener(OnPlayPauseListener l) {
        this.onPlayPauseListener = l;
    }

    public static class VideoBroadcastReceiver extends BroadcastReceiver {

        public static final int NOT_SET = -1;

        private final BroadcastSender broadcastSender;

        /**
         * Used by broadcastReceiver on some callbacks to recognize if the received action concerns this particular receiver.
         */
        private int type = -1;

        public VideoBroadcastReceiver(BroadcastSender broadcastSender) {
            this.broadcastSender = broadcastSender;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        final public void onReceive(Context context, Intent intent) {
            //do not bother with broadcasts that were send by you
            BroadcastSender broadcastSenderLocal = (BroadcastSender) intent.getSerializableExtra(Constants.EXTRAS_BROADCAST_SENDER);
            if (broadcastSenderLocal == this.broadcastSender) {
                return;
            }

            if (Constants.BROADCAST_REQUEST_FULLSCREEN.equals(intent.getAction())) {
                final int position = intent.getIntExtra(Constants.EXTRAS_LIST_POSITION, NOT_SET);
                fullscreen(position);
            }
            else if (Constants.BROADCAST_REQUEST_FULLSCREEN_INVALIDATE.equals(intent.getAction())) {
                fullscreenInvalidate();
            }
            else if (Constants.BROADCAST_VIDEO_STARTED.equals(intent.getAction())) {
                started();
            }
            else if (Constants.BROADCAST_VIDEO_PAUSED.equals(intent.getAction())) {
                paused();
            }
            else if (Constants.BROADCAST_REQUEST_LANDSCAPE.equals(intent.getAction())) {
                requestLandscape(intent.getIntExtra(Constants.EXTRAS_ORIENTATION, NOT_SET));
            }
            else if (Constants.BROADCAST_REQUEST_LANDSCAPE_INVALIDATE.equals(intent.getAction())) {
                requestLandscapeInvalidate();
            }
            else if (Constants.BROADCAST_REQUEST_VIDEO_START.equals(intent.getAction())) {
                int type = intent.getIntExtra(Constants.EXTRAS_VIDEO_TYPE, NOT_SET);
                if (type == this.type) {
                    requestVideoStart();
                }
            }
            else if (Constants.BROADCAST_REQUEST_VIDEO_STOP.equals(intent.getAction())) {
                requestVideoStop();
            }
        }

        public void fullscreen(int position){}
        public void fullscreenInvalidate(){}
        public void started(){}
        public void paused(){}
        public void requestLandscape(int orientation){}
        public void requestLandscapeInvalidate(){}
        public void requestVideoStart(){}
        public void requestVideoStop(){}
    }

    public interface OnPlayPauseListener {
        public void onResume();
        public void onPause();
    }
}
