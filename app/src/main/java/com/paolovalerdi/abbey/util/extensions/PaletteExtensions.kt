package com.paolovalerdi.abbey.util.extensions

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.kabouzeid.appthemehelper.util.ColorUtil

/**
 * @author Paolo Valerdi
 */
private const val BLACK_MAX_LIGHTNESS = 0.035f

private fun isBlack(hslColor: FloatArray) = hslColor[2] <= BLACK_MAX_LIGHTNESS

private fun isNearRedLine(hslColor: FloatArray) = hslColor[0] in 10f..37f && hslColor[1] <= 0.82f

private val customWhiteAllowedFilter = Palette.Filter { _, hsl ->
    isBlack(hsl).not().and(isNearRedLine(hsl)).not()
}

inline val Int.isLight: Boolean
    get() = ColorUtil.isColorLight(this)

fun Bitmap.generateWhiteAllowedPalette(): Palette = Palette.from(this)
    .clearFilters()
    .addFilter(customWhiteAllowedFilter)
    .generate()

fun Bitmap.isTopRegionLight(height: Int, displayWidth: Int): Boolean {
    val imageHeightToUse = (height * (width.toFloat() / displayWidth)).toInt()
    val p = Palette.from(this)
        .maximumColorCount(4)
        .clearFilters()
        .setRegion(0, 0, width, imageHeightToUse)
        .generate()

    return p.getColor(Color.BLACK).isLight
}

fun Palette.getColor(fallback: Int): Int {
    dominantSwatch?.let { swatch -> return swatch.rgb }
    vibrantSwatch?.let { swatch -> return swatch.rgb }
    darkVibrantSwatch?.let { swatch -> return swatch.rgb }
    darkMutedSwatch?.let { swatch -> return swatch.rgb }
    lightVibrantSwatch?.let { swatch -> return swatch.rgb }
    lightMutedSwatch?.let { swatch -> return swatch.rgb }
    mutedSwatch?.let { swatch -> return swatch.rgb }
    return fallback
}

fun Palette.getSuitableColorFor(
    background: Int,
    fallback: Int,
    isFullScreenPlayer: Boolean = false
): Int {
    if (isFullScreenPlayer) return lightSwatchesFirst(background, fallback) else {
        dominantSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
        vibrantSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
        darkVibrantSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
        lightVibrantSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
        darkMutedSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
        lightMutedSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
        mutedSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
    }
    return fallback
}

private fun Palette.lightSwatchesFirst(background: Int, fallback: Int): Int {
    lightVibrantSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
    lightMutedSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
    dominantSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
    mutedSwatch?.let { if (hasEnoughContrast(background, it.rgb)) return it.rgb }
    return fallback
}

private fun hasEnoughContrast(
    foreground: Int,
    background: Int
): Boolean = if (background.isLight) {
    ColorUtils.calculateContrast(foreground, background) >= 2
} else {
    ColorUtils.calculateContrast(foreground, background) >= 2.6
}