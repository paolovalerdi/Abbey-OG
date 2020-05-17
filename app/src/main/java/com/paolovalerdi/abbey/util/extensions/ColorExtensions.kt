package com.paolovalerdi.abbey.util.extensions

import android.graphics.Color
import androidx.annotation.ColorInt

@ColorInt
fun getPrimaryTextColor(isLightBackground: Boolean): Int = if (isLightBackground) {
    Color.parseColor("#DE000000")
} else Color.parseColor("#DEFFFFFF")

@ColorInt
fun getSecondaryTextColor(isLightBackground: Boolean): Int = if (isLightBackground) {
    Color.parseColor("#99000000")
} else Color.parseColor("#99FFFFFF")