package com.paolovalerdi.abbey.ui.activities.preferences.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.kabouzeid.appthemehelper.common.prefs.ATESeekBarPreference
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.preferences.NowPlayingScreenPreference
import com.paolovalerdi.abbey.preferences.NowPlayingScreenPreferenceDialog
import com.paolovalerdi.abbey.ui.fragments.player.NowPlayingScreen
import com.paolovalerdi.abbey.util.preferences.NOW_PLAYING_SCREEN_ID
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil

/**
 * @author Paolo Valerdi
 * TODO: Use a summary provider rather than the preferences listener to update the selected now playing screen summary.
 */
class NowPlayingPreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var nowPlayingScreenPreference: DialogPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_now_playing, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PreferenceUtil.registerOnSharedPreferenceChangedListener(this@NowPlayingPreferenceFragment)
        invalidateSettings()
    }

    override fun onDestroyView() {
        PreferenceUtil.unregisterOnSharedPreferenceChangedListener(this)
        super.onDestroyView()
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is NowPlayingScreenPreference) {
            val fragment = NowPlayingScreenPreferenceDialog.newInstance()
            fragment.show(childFragmentManager, preference.key)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == NOW_PLAYING_SCREEN_ID) {
            nowPlayingScreenPreference?.setSummary(PreferenceUtil.nowPlayingScreen.titleRes)
            findPreference<ATESeekBarPreference>("material_now_playing_padding")?.isVisible = PreferenceUtil.nowPlayingScreen == NowPlayingScreen.MATERIAL
        }
    }

    private fun invalidateSettings() {
        nowPlayingScreenPreference = findPreference("now_playing_screen_id")
        nowPlayingScreenPreference?.setSummary(PreferenceUtil.nowPlayingScreen.titleRes)
        findPreference<ATESeekBarPreference>("material_now_playing_padding")?.isVisible = PreferenceUtil.nowPlayingScreen == NowPlayingScreen.MATERIAL
    }

}