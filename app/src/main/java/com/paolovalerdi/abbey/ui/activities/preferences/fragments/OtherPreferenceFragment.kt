package com.paolovalerdi.abbey.ui.activities.preferences.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.preferences.BlacklistPreference
import com.paolovalerdi.abbey.preferences.BlacklistPreferenceDialog

/**
 * @author Paolo Valerdi
 */
class OtherPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_other, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is BlacklistPreference) {
            val fragment = BlacklistPreferenceDialog.newInstance()
            fragment.setTargetFragment(this, 0)
            fragment.show(fragmentManager!!, preference.key)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

}