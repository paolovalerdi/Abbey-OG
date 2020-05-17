package com.paolovalerdi.abbey.util.extensions

import android.media.AudioManager

/**
 * @author Paolo Valerdi
 */

val AudioManager.mediaMaxVolume: Int
    get() = getStreamMaxVolume(AudioManager.STREAM_MUSIC)

val AudioManager.mediaVolume
    get() = getStreamVolume(AudioManager.STREAM_MUSIC)

operator fun AudioManager?.unaryPlus() {
    this?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0)
}

operator fun AudioManager?.unaryMinus() {
    this?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0)
}

