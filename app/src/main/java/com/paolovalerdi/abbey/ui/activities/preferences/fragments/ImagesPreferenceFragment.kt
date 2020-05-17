package com.paolovalerdi.abbey.ui.activities.preferences.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.paolovalerdi.abbey.R

/**
 * @author Paolo Valerdi
 */
class ImagesPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_images, rootKey)
    }

}