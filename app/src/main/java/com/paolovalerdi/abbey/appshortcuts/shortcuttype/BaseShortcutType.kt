package com.paolovalerdi.abbey.appshortcuts.shortcuttype

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.pm.ShortcutInfoCompat
import com.paolovalerdi.abbey.appshortcuts.AppShortcutLauncherActivity


@TargetApi(Build.VERSION_CODES.N_MR1)
abstract class BaseShortcutType(val context: Context) {

    companion object {

        const val ID_PREFIX = "com.paolovalerdi.abbey.appshortcuts.id."

    }

    abstract val id: String

    abstract val shortcutInfoCompat: ShortcutInfoCompat

    fun getPlaySongsIntent(shortcutType: Int): Intent {
        val intent = Intent(context, AppShortcutLauncherActivity::class.java)
        intent.action = Intent.ACTION_VIEW

        val b = Bundle()
        b.putInt(AppShortcutLauncherActivity.KEY_SHORTCUT_TYPE, shortcutType)

        intent.putExtras(b)

        return intent
    }

}
