package com.paolovalerdi.abbey.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.paolovalerdi.abbey.R;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class BlacklistPreference extends DialogPreference {
    public BlacklistPreference(Context context) {
        super(context);
        init();
    }

    public BlacklistPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BlacklistPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.preference_custom);
    }


}