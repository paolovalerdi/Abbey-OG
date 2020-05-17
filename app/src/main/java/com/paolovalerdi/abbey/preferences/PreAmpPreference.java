package com.paolovalerdi.abbey.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.paolovalerdi.abbey.R;

public class PreAmpPreference extends DialogPreference {
    public PreAmpPreference(Context context) {
        super(context);
        init();
    }

    public PreAmpPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreAmpPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PreAmpPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.preference_custom);
    }

}
