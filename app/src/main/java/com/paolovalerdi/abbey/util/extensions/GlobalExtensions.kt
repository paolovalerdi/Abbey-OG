package com.paolovalerdi.abbey.util.extensions

import android.os.Build

fun isMOrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun isLollipop() = Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP