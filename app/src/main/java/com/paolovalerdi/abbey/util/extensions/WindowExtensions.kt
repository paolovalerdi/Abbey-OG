package com.paolovalerdi.abbey.util.extensions

import android.view.View
import android.view.Window
import android.view.WindowManager

fun Window.allowDrawUnderStatusBar() {
    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}

fun Window.allowDrawUnderNavigationBar(isLandscape: Boolean) {
    if (isLandscape.not()) {
        decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    } else {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }
}