package com.paolovalerdi.abbey.preferences;

import android.content.Context;
import android.util.AttributeSet;

import com.kabouzeid.appthemehelper.common.prefs.ATEDialogPreference;

public class NowPlayingScreenPreference extends ATEDialogPreference {

    public NowPlayingScreenPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NowPlayingScreenPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NowPlayingScreenPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NowPlayingScreenPreference(Context context) {
        super(context);
    }
}