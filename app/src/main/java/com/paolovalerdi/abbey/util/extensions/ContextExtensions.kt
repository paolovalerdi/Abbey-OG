package com.paolovalerdi.abbey.util.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.paolovalerdi.abbey.R


fun Activity.screenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.makeToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.makeToast(@StringRes resId: Int) {
    makeToast(getString(resId))
}

@ColorInt
fun Context.resolveAttrColor(@AttrRes attr: Int): Int {
    val a = theme.obtainStyledAttributes(intArrayOf(attr))
    val color: Int
    try {
        color = a.getColor(0, 0)
    } finally {
        a.recycle()
    }
    return color
}

@ColorInt
fun Context.getColorControlNormal(light: Boolean): Int = if (light) {
    ContextCompat.getColor(this, R.color.ate_control_normal_light)
} else getPrimaryTextColor(false)

@ColorInt
fun Context.getColorControlDisabled(light: Boolean): Int {
    val resId = if (light) R.color.ate_control_disabled_light else R.color.ate_control_disabled_dark
    return ContextCompat.getColor(this, resId)
}


val Resources.isLandscape: Boolean
    get() = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Resources.convertDpToPixels(dp: Float): Float = dp * displayMetrics.density