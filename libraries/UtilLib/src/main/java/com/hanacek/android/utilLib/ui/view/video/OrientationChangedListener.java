package com.hanacek.android.utilLib.ui.view.video;

import android.content.Context;
import android.content.res.Configuration;
import android.view.OrientationEventListener;

import com.hanacek.android.utilLib.app.GenericAppContext;
import com.hanacek.android.utilLib.util.Log;

abstract public class OrientationChangedListener extends OrientationEventListener {

    private static final String LOG = "orientation_listener";


    /**
     * When fullscreen playback was requested by fullscreen button, do not leave fullscreen unless device reach landscape orientation to reset this value.
     * When fullscreen invalidate was requested by back button, do not go to fullscreen unless orientation reach the portrait to reset this value.
     */
    private boolean videoIgnoreOrientationChange;
    private int ignoredAtAngle;
    private int lastOrientation = ORIENTATION_UNKNOWN;

    private boolean videoInFullscreen;
    private boolean videoIsPlaying;
    private boolean isNaturalOrientationPortrait;

    public OrientationChangedListener(Context context) {
        super(context);
        //Log.registerInstance(LOG);
        isNaturalOrientationPortrait = (GenericAppContext.getNaturalDeviceOrientation() == Configuration.ORIENTATION_PORTRAIT);
    }

    public void setIgnoreOrientationChange() {
        if (lastOrientation != ORIENTATION_UNKNOWN) {
            videoIgnoreOrientationChange = true;
            ignoredAtAngle = lastOrientation;
        }
    }

    public void setVideoInFullscreen(boolean val) {
        this.videoInFullscreen = val;
    }

    public boolean isVideoInFullscreen() {
        return this.videoInFullscreen;
    }

    public void setVideoIsPlaying(boolean val) {
        this.videoIsPlaying = val;
    }

    /**
     * @param orientation -1 when lying
     */
    @Override
    public void onOrientationChanged(int orientation) {
        Log.debug(LOG, "orientation: " + orientation);
        if (orientation == ORIENTATION_UNKNOWN) {
            Log.debug(LOG, "orientation is unknown");
            videoIgnoreOrientationChange = false;
            return;
        }

        lastOrientation = orientation;

        if (videoIgnoreOrientationChange) {
            Log.debug(LOG, "ignored orientation change");
            if (isLandscape(orientation) == isLandscape(ignoredAtAngle)) {
                Log.debug(LOG, "keep ignore");
                return;
            }
            videoIgnoreOrientationChange = false;
        }

        //go landscape
        if (videoIsPlaying) {
            Log.debug(LOG, "video is playing");
            if (isLandscape(orientation)) {
                if (!videoInFullscreen) {
                    videoToFullscreen();
                }
            }
            else { //go portrait
                if (videoInFullscreen) {
                    videoLeaveFullscreen();
                }
            }
        }
    }

    /**
     * @param orientation
     * @return Is landscape when natural position is portrait (phones) and orientation part is false or
     * when natural position is landscape (tablets) and orientation part of the condition is true.
     */
    private boolean isLandscape(int orientation) {
        return isNaturalOrientationPortrait ^ ((orientation >= 0 && orientation <= 45) || (orientation >= 315 && orientation <= 360) || (orientation >= 135 && orientation <= 225)) ? true : false ;
    }

    public abstract void videoToFullscreen();
    public abstract void videoLeaveFullscreen();
}
