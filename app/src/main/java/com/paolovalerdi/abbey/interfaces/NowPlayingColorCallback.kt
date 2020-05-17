package com.paolovalerdi.abbey.interfaces

import androidx.annotation.ColorInt

interface NowPlayingColorCallback {

    fun onColorChanged(@ColorInt color: Int, isTopLight: Boolean)

}