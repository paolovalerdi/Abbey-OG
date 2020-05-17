package com.paolovalerdi.abbey.appshortcuts

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.IconCompat
import com.kabouzeid.appthemehelper.ThemeStore
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.util.ImageUtil
import com.paolovalerdi.abbey.util.Util
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil

@RequiresApi(Build.VERSION_CODES.N_MR1)
object AppShortcutIconGenerator {

    fun getIcon(context: Context, @DrawableRes iconResId: Int) = if (PreferenceUtil.coloredAppShortcuts) {
        generateThemedIcon(context, iconResId)
    } else generateDefaultIcon(context, iconResId)

    private fun generateDefaultIcon(
        context: Context,
        @DrawableRes iconResId: Int
    ) = generateIcon(
        context,
        iconResId,
        context.getColor(R.color.app_shortcut_default_foreground),
        Color.WHITE
    )

    private fun generateThemedIcon(
        context: Context,
        @DrawableRes iconResId: Int
    ) = generateIcon(
        context,
        iconResId,
        ThemeStore.accentColor(context),
        ThemeStore.primaryColor(context)
    )

    private fun generateIcon(
        context: Context,
        @DrawableRes iconId: Int,
        @ColorInt foregroundColor: Int,
        @ColorInt backgroundColor: Int
    ): IconCompat {

        val foregroundDrawable = ImageUtil.getTintedVectorDrawable(context, iconId, foregroundColor)
        val backgroundDrawable = ImageUtil.getTintedVectorDrawable(context, R.drawable.ic_app_shortcut_background, backgroundColor)
        //backgroundDrawable.setTint(backgroundColor)

        return if (Util.hasOreoOrHigher()) {
            IconCompat.createWithAdaptiveBitmap(
                ImageUtil.createBitmap(AdaptiveIconDrawable(backgroundDrawable, foregroundDrawable))
            )
        } else {
            IconCompat.createWithBitmap(
                ImageUtil.createBitmap(LayerDrawable(arrayOf(backgroundDrawable, foregroundDrawable)))
            )
        }
    }

}