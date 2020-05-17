package com.paolovalerdi.abbey.ui.activities.preferences

import android.os.Bundle
import androidx.fragment.app.transaction
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.common.prefs.ATEPreference
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.appshortcuts.DynamicShortcutManager
import com.paolovalerdi.abbey.ui.activities.base.AbsThemeActivity
import com.paolovalerdi.abbey.util.Util
import com.paolovalerdi.abbey.util.extensions.isLight
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.extensions.tintContentColorFor
import kotlinx.android.synthetic.main.activity_preferences.*
import me.jfenn.attribouter.Attribouter

/**
 * @author Paolo Valerdi
 */
class PreferencesActivity : AbsThemeActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, ColorChooserDialog.ColorCallback {

    companion object {

        private const val TITLE = "toolbar_title"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        setSystemBarsColorAuto()
        toolbar.tintContentColorFor(resolveAttrColor(R.attr.colorSurface).isLight)
        setSupportActionBar(toolbar)
        title = null

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settingsFragmentContainer, RootPreferenceScreen())
                .commit()
        } else {
            toolbarTitle.text = savedInstanceState.getCharSequence(TITLE)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0)
                toolbarTitle.text = resources.getString(R.string.action_settings)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(TITLE, toolbarTitle.text)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        } else {
            finish()
        }
        return super.onSupportNavigateUp()
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat?, pref: Preference?): Boolean {
        val args = pref?.extras
        pref?.fragment?.let { className ->

            val fragment = if (className == "about_custom_fragment") {
                Attribouter.from(this@PreferencesActivity)
                    .toFragment()
            } else {
                supportFragmentManager.fragmentFactory.instantiate(classLoader, className)
            }.apply {
                arguments = args
                setTargetFragment(caller, 0)
            }

            supportFragmentManager.transaction {
                setCustomAnimations(
                    R.anim.ds_grow_fade_in_center,
                    R.anim.ds_shrink_fade_out_center,
                    R.anim.ds_grow_fade_in_center,
                    R.anim.ds_shrink_fade_out_center
                )
                replace(R.id.settingsFragmentContainer, fragment)
                addToBackStack(null)
            }

            toolbarTitle.text = pref.title
        }
        return true
    }

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        ThemeStore.editTheme(this)
            .accentColor(selectedColor)
            .commit()

        if (Util.isNougatOrHigher()) {
            DynamicShortcutManager(this).updateDynamicShortcuts()
        }

        recreate()
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {}

    class RootPreferenceScreen : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_main, rootKey)
            findPreference<ATEPreference>("look_fragment")?.tintIcon(ThemeStore.accentColor(requireContext()))
        }

    }
}
