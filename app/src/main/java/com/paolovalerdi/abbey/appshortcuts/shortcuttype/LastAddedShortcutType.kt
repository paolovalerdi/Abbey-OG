package com.paolovalerdi.abbey.appshortcuts.shortcuttype

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.appshortcuts.AppShortcutIconGenerator
import com.paolovalerdi.abbey.appshortcuts.AppShortcutLauncherActivity.Companion.SHORTCUT_TYPE_LAST_ADDED

@TargetApi(Build.VERSION_CODES.N_MR1)
class LastAddedShortcutType(context: Context) : BaseShortcutType(context) {

    override val id: String
        get() = ID_PREFIX + "last_added"

    override val shortcutInfoCompat: ShortcutInfoCompat
        get() = ShortcutInfoCompat.Builder(context, id)
            .setShortLabel(context.getString(R.string.app_shortcut_last_added_short))
            .setLongLabel(context.getString(R.string.last_added))
            .setIcon(AppShortcutIconGenerator.getIcon(context, R.drawable.ic_app_shortcut_last_added))
            .setIntent(getPlaySongsIntent(SHORTCUT_TYPE_LAST_ADDED))
            .build()

}