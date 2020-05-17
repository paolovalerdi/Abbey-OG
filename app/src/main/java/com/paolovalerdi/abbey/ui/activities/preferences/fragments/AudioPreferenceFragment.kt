package com.paolovalerdi.abbey.ui.activities.preferences.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.view.View
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.preferences.PreAmpPreference
import com.paolovalerdi.abbey.preferences.PreAmpPreferenceDialog
import com.paolovalerdi.abbey.util.NavigationUtil
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import com.paolovalerdi.abbey.util.preferences.RG_SOURCE_MODE
import com.paolovalerdi.abbey.util.preferences.RG_SOURCE_MODE_NONE

/**
 * @author Paolo Valerdi
 */
class AudioPreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var replayGainPreAmp: DialogPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_audio, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invalidateSettings()
        PreferenceUtil.registerOnSharedPreferenceChangedListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceUtil.unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is PreAmpPreference) {
            val fragment = PreAmpPreferenceDialog.newInstance()
            fragment.show(childFragmentManager, preference.key)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == RG_SOURCE_MODE) {
            replayGainPreAmp?.isEnabled = PreferenceUtil.getReplayGainSourceMode() != RG_SOURCE_MODE_NONE
        }
    }

    private fun invalidateSettings() {
        replayGainPreAmp = findPreference("replaygain_preamp")
        replayGainPreAmp?.isEnabled = PreferenceUtil.getReplayGainSourceMode() != RG_SOURCE_MODE_NONE
        val equalizer: Preference? = findPreference("equalizer")
        if (hasEqualizer().not()) {
            equalizer?.isEnabled = false
            equalizer?.setSummary(R.string.no_equalizer)
        }
        equalizer?.setOnPreferenceClickListener {
            NavigationUtil.openEqualizer(activity!!)
            true
        }
    }

    private fun hasEqualizer(): Boolean {
        val effects = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        val pm = activity?.packageManager
        val ri = pm?.resolveActivity(effects, 0)
        return (ri != null)
    }

}