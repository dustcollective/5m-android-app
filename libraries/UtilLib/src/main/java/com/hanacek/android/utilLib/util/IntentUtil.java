package com.hanacek.android.utilLib.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import com.hanacek.android.utilLib.R;
import com.hanacek.android.utilLib.app.Constants;
import com.hanacek.android.utilLib.ui.view.video.ResizableVideoSurfaceView;

public class IntentUtil {

    // ------------------------------ EXTERNAL START

    public static void video(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setDataAndType(Uri.parse(url), "video/mp4");
        context.startActivity(Intent.createChooser(intent, context.getResources().getText(R.string.video_playback_fallback_2_3)));
    }

    /**
     * Brings user to desktop.
     *
     * @param context
     */
    public static void desktop(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void googlePlay(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        context.startActivity(intent);
    }

    // ------------------------------ EXTERNAL END

    // ------------------------------ SEND BROADCAST

    public static void broadcastRequestFullscreen(Context context, ResizableVideoSurfaceView.BroadcastSender broadcastSender) {
        broadcastRequestFullscreen(context, broadcastSender, -1);
    }

    public static void broadcastRequestFullscreen(Context context, ResizableVideoSurfaceView.BroadcastSender broadcastSender, int listPosition) {
        final Intent intent = new Intent(Constants.BROADCAST_REQUEST_FULLSCREEN);
        intent.putExtra(Constants.EXTRAS_LIST_POSITION, listPosition);
        broadcastVideo(context, broadcastSender, intent);
    }

    /**
     * Only component responsible for orientation change should listen to this.
     *
     * @param context
     * @param broadcastSender
     */
    public static void broadcastRequestLandscape(Context context, ResizableVideoSurfaceView.BroadcastSender broadcastSender) {
        final Intent intent = new Intent(Constants.BROADCAST_REQUEST_LANDSCAPE);
        broadcastVideo(context, broadcastSender, intent);
    }

    /**
     * Only component responsible for orientation change should listen to this.
     *
     * @param context
     * @param broadcastSender
     * @param requestedOrientation @see ActivityInfo, for example ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
     */
    public static void broadcastRequestLandscape(Context context, ResizableVideoSurfaceView.BroadcastSender broadcastSender, int requestedOrientation) {
        final Intent intent = new Intent(Constants.BROADCAST_REQUEST_LANDSCAPE);
        intent.putExtra(Constants.EXTRAS_ORIENTATION, requestedOrientation);
        broadcastVideo(context, broadcastSender, intent);
    }

    /**
     * Only component responsible for orientation change should listen to this.
     *
     * @param context
     * @param broadcastSender
     */
    public static void broadcastRequestLandscapeInvalidate(Context context, ResizableVideoSurfaceView.BroadcastSender broadcastSender) {
        final Intent intent = new Intent(Constants.BROADCAST_REQUEST_LANDSCAPE_INVALIDATE);
        broadcastVideo(context, broadcastSender, intent);
    }

    public static void broadcastRequestFullscreenInvalidate(Context context, ResizableVideoSurfaceView.BroadcastSender broadcastSender) {
        final Intent intent = new Intent(Constants.BROADCAST_REQUEST_FULLSCREEN_INVALIDATE);
        broadcastVideo(context, broadcastSender, intent);
    }

    public static void broadcastVideoStarted(Context context, ResizableVideoSurfaceView.BroadcastSender broadcastSender) {
        final Intent intent = new Intent(Constants.BROADCAST_VIDEO_STARTED);
        broadcastVideo(context, broadcastSender, intent);
    }

    public static void broadcastVideoPaused(Context context, ResizableVideoSurfaceView.BroadcastSender broadcastSender) {
        final Intent intent = new Intent(Constants.BROADCAST_VIDEO_PAUSED);
        broadcastVideo(context, broadcastSender, intent);
    }

    private static void broadcastVideo(Context context, ResizableVideoSurfaceView.BroadcastSender broadcastSender, Intent intent) {
        intent.putExtra(Constants.EXTRAS_BROADCAST_SENDER, broadcastSender);
        context.sendBroadcast(intent);
    }

    public static void broadcastRequestVideoStart(Context context, int type) {
        final Intent intent = new Intent(Constants.BROADCAST_REQUEST_VIDEO_START);
        intent.putExtra(Constants.EXTRAS_VIDEO_TYPE, type);
        context.sendBroadcast(intent);
    }

    public static void broadcastRequestVideoStop(Context context) {
        final Intent intent = new Intent(Constants.BROADCAST_REQUEST_VIDEO_STOP);
        context.sendBroadcast(intent);
    }

    public static void broadcastConnectionReady(Context context) {
        final Intent intent = new Intent(Constants.BROADCAST_CONNECTION_READY);
        context.sendBroadcast(intent);
    }

    // ------------------------------ BROADCAST INTENT FILTERS

    public static void registerRequestFullscreen(Activity activity, BroadcastReceiver broadcastReceiver) {
        final IntentFilter intentFilter = new IntentFilter();
        registerRequestFullscreen(activity, broadcastReceiver, intentFilter);
    }

    public static void registerRequestFullscreen(Activity activity, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        intentFilter.addAction(Constants.BROADCAST_REQUEST_FULLSCREEN);
        intentFilter.addAction(Constants.BROADCAST_REQUEST_FULLSCREEN_INVALIDATE);
        activity.registerReceiver(broadcastReceiver, intentFilter);
    }

    public static void registerFullVideo(Activity activity, BroadcastReceiver broadcastReceiver) {
        IntentFilter intentFilter = new IntentFilter();
        registerFullVideo(activity, broadcastReceiver, intentFilter);
    }

    public static void registerFullVideo(Activity activity, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        intentFilter.addAction(Constants.BROADCAST_REQUEST_FULLSCREEN);
        intentFilter.addAction(Constants.BROADCAST_REQUEST_FULLSCREEN_INVALIDATE);
        intentFilter.addAction(Constants.BROADCAST_VIDEO_PAUSED);
        intentFilter.addAction(Constants.BROADCAST_VIDEO_STARTED);
        activity.registerReceiver(broadcastReceiver, intentFilter);
    }
}
