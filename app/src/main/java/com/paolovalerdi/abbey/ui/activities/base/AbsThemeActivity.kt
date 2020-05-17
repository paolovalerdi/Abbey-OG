package com.paolovalerdi.abbey.ui.activities.base

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.kabouzeid.appthemehelper.ATH
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.util.Util
import com.paolovalerdi.abbey.util.extensions.allowDrawUnderNavigationBar
import com.paolovalerdi.abbey.util.extensions.allowDrawUnderStatusBar
import com.paolovalerdi.abbey.util.extensions.isLandscape
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil

abstract class AbsThemeActivity : AbsCoroutineActivity() {

    protected val navigationBarView by lazy {
        //window.decorView.rootView.findViewById<NavigationBarView?>(R.id.navigation_bar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(PreferenceUtil.generalTheme)
        super.onCreate(savedInstanceState)
    }

    fun allowDrawUnderStatusBar() {
        window.allowDrawUnderStatusBar()
    }

    fun allowDrawUnderNavigationBar() {
        window.allowDrawUnderNavigationBar(resources.isLandscape)
    }

    fun setSystemBarsColorAuto() {
        setStatusBarColor(resolveAttrColor(R.attr.colorSurface))
        setNavigationBarColor(resolveAttrColor(R.attr.colorSurface))
    }

    fun setStatusBarColorAuto() {
        setStatusBarColor(ThemeStore.primaryColor(this))
    }

    fun setLightNavigationBar(isColorLight: Boolean) {
        ATH.setLightNavigationbar(this, isColorLight)
    }

    fun setNavigationBarColorAuto(onSurface: Boolean = false) {
        val color = if (onSurface)
            ThemeStore.primaryColorDark(this)
        else ThemeStore.primaryColor(this)

        setNavigationBarColor(color)
    }

    open fun setLightStatusBar(isColorLight: Boolean) {
        ATH.setLightStatusbar(this, isColorLight)
    }

    open fun setNavigationBarColor(color: Int) {
        val fixedColor = if (Util.hasOreoOrHigher().and(resources.isLandscape.not())) color else Color.BLACK
        ATH.setNavigationbarColor(this, fixedColor)
    }

    protected fun setStatusBarColor(color: Int) {
        val statusBar: View? = window.decorView.rootView.findViewById(R.id.status_bar)
        val fixedColor = if (Util.hasMarshmallowOrHigher()) color else ColorUtil.darkenColor(color)
        if (statusBar != null) {
            statusBar.setBackgroundColor(fixedColor)
        } else {
            window.statusBarColor = fixedColor
        }
        setLightStatusBar(ColorUtil.isColorLight(fixedColor))
    }
}
