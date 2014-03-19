package com.hanacek.android.utilLib.helpers;

import android.content.Context;
import android.media.AudioManager;

public class AudioFocusHelper {
    AudioManager mAM;

    private final AudioManager.OnAudioFocusChangeListener listener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        /**
         * Called by AudioManager on audio focus changes.
         */
        public void onAudioFocusChange(int focusChange) {

        }
    };;

    public AudioFocusHelper(Context ctx) {
        mAM = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
    }

    /** Requests audio focus. Returns whether request was successful or not. */
    public boolean requestFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAM.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    /** Abandons audio focus. Returns whether request was successful or not. */
    public boolean abandonFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAM.abandonAudioFocus(listener);
    }
}
