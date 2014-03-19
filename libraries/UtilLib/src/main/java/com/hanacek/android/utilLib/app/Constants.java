package com.hanacek.android.utilLib.app;

public class Constants {

    public static final String EXTRAS_LIST_POSITION = "extras_list_position";
    public static final String EXTRAS_BROADCAST_SENDER = "extras_broadcast_sender";
    public static final String EXTRAS_VIDEO_TYPE = "extras_video_type";
    public static final String EXTRAS_ORIENTATION = "extras_orientation";

    public static final String BROADCAST_REQUEST_FULLSCREEN = "com.hanacek.android.utilLib.BROADCAST_REQUEST_FULLSCREEN";
    public static final String BROADCAST_REQUEST_LANDSCAPE = "com.hanacek.android.utilLib.BROADCAST_REQUEST_LANDSCAPE";
    public static final String BROADCAST_REQUEST_FULLSCREEN_INVALIDATE = "com.hanacek.android.utilLib.BROADCAST_REQUEST_FULLSCREEN_INVALIDATE";
    public static final String BROADCAST_REQUEST_LANDSCAPE_INVALIDATE = "com.hanacek.android.utilLib.BROADCAST_REQUEST_LANDSCAPE_INVALIDATE";

    public static final String BROADCAST_VIDEO_STARTED = "com.hanacek.android.utilLib.BROADCAST_VIDEO_STARTED";
    public static final String BROADCAST_VIDEO_PAUSED = "com.hanacek.android.utilLib.BROADCAST_VIDEO_PAUSED";
    public static final String BROADCAST_REQUEST_VIDEO_START = "com.hanacek.android.utilLib.BROADCAST_REQUEST_VIDEO_START";
    public static final String BROADCAST_REQUEST_VIDEO_STOP = "com.hanacek.android.utilLib.BROADCAST_REQUEST_VIDEO_STOP";

    public static final String BROADCAST_CONNECTION_READY = "com.hanacek.android.utilLib.BROADCAST_CONNECTION_READY";

    public enum Priority {ULTRA_LOW, LOW, NORMAL, HIGH, ULTRA_HIGH}
}
