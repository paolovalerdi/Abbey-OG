package com.paolovalerdi.abbey.ui.activities.preferences.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.TwoStatePreference
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.common.prefs.ATEColorPreference
import com.paolovalerdi.abbey.App
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.appshortcuts.DynamicShortcutManager
import com.paolovalerdi.abbey.ui.dialogs.PurchaseDialog
import com.paolovalerdi.abbey.preferences.CategoryPreference
import com.paolovalerdi.abbey.preferences.CategoryPreferenceDialog
import com.paolovalerdi.abbey.util.Util
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil

/**
 * @author Paolo Valerdi
 */
class LookAndFeelPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_look_and_feel, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invalidateSettings()
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is CategoryPreference) {
            val fragment = CategoryPreferenceDialog.newInstance()
            fragment.show(childFragmentManager, preference.key)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    private fun invalidateSettings() {

        val generalTheme: Preference? = findPreference("general_theme")
        generalTheme?.setOnPreferenceChangeListener { _, newValue ->
            val themeName: String = newValue as String
            if ((themeName == "black").and(App.isProVersion.not())) {
                PurchaseDialog.newInstance().show(fragmentManager!!, null)
                return@setOnPreferenceChangeListener false
            } else {
                when (themeName) {
                    "light" -> {
                        ThemeStore.editTheme(activity as AppCompatActivity)
                            .primaryColorRes(R.color.md_white_1000)
                            .commit()
                    }
                    "dark" -> {
                        ThemeStore.editTheme(activity as AppCompatActivity)
                            .primaryColorRes(R.color.abbey_theme_dark_surface_color)
                            .commit()
                    }
                    else -> {
                        ThemeStore.editTheme(activity as AppCompatActivity)
                            .primaryColorRes(R.color.abbey_theme_black_surface_color)
                            .commit()
                    }
                }

                ThemeStore.markChanged(requireActivity())
                if (Util.isNougatOrHigher()) {
                    requireActivity().setTheme(PreferenceUtil.getThemeResFromValue(themeName))
                    DynamicShortcutManager(requireActivity()).updateDynamicShortcuts()
                }

                requireActivity().recreate()
                true
            }
        }

        val accentColorPreference: ATEColorPreference? = findPreference("accent_color")
        val accentColor = ThemeStore.accentColor(requireContext())
        accentColorPreference?.setColor(accentColor, accentColor)
        accentColorPreference?.setOnPreferenceClickListener {
            ColorChooserDialog.Builder(requireContext(), R.string.accent_color)
                .accentMode(true)
                .allowUserColorInputAlpha(false)
                .allowUserColorInput(true)
                .preselect(accentColor)
                .show(childFragmentManager)
            true
        }

        val colorAppShortcuts: TwoStatePreference? = findPreference("should_color_app_shortcuts")
        if (Util.isNougatOrHigher().not()) {
            colorAppShortcuts?.isVisible = false
        } else {
            colorAppShortcuts?.isChecked = PreferenceUtil.coloredAppShortcuts
            colorAppShortcuts?.setOnPreferenceChangeListener { _, newValue ->
                PreferenceUtil.coloredAppShortcuts = (newValue as Boolean)
                DynamicShortcutManager(requireActivity()).updateDynamicShortcuts()
                true
            }
        }

    }

}