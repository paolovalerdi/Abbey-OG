package com.paolovalerdi.abbey.appshortcuts;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.paolovalerdi.abbey.appshortcuts.shortcuttype.LastAddedShortcutType;
import com.paolovalerdi.abbey.appshortcuts.shortcuttype.ShuffleAllShortcutType;
import com.paolovalerdi.abbey.appshortcuts.shortcuttype.TopTracksShortcutType;

import java.util.Arrays;
import java.util.List;

/**
 * @author Adrian Campos
 */

@TargetApi(Build.VERSION_CODES.N_MR1)
public class DynamicShortcutManager {

    private Context context;

    public DynamicShortcutManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static void createPinShortcut(
            Context context,
            String id,
            String label,
            IconCompat icon,
            Intent intent
    ) {
        ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(context.getApplicationContext(), id)
                .setShortLabel(label)
                .setLongLabel(label)
                .setIcon(icon)
                .setIntent(intent)
                .build();
        new DynamicShortcutManager(context).addShortcut(shortcut);
    }

    public static ShortcutInfo createShortcut(Context context, String id, String shortLabel, String longLabel, Icon icon, Intent intent) {
        return new ShortcutInfo.Builder(context, id)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(icon)
                .setIntent(intent)
                .build();
    }

    private void addShortcut(ShortcutInfoCompat shortcutInfoCompat) {
        ShortcutManagerCompat.requestPinShortcut(context, shortcutInfoCompat, null);
    }

    public void initDynamicShortcuts() {
        if (ShortcutManagerCompat.getDynamicShortcuts(context).size() == 0) {
            ShortcutManagerCompat.addDynamicShortcuts(context, getDefaultShortcuts());
        }
    }

    public void updateDynamicShortcuts() {
        ShortcutManagerCompat.updateShortcuts(context, getDefaultShortcuts());
    }

    private List<ShortcutInfoCompat> getDefaultShortcuts() {
        return (Arrays.asList(
                new ShuffleAllShortcutType(context).getShortcutInfoCompat(),
                new TopTracksShortcutType(context).getShortcutInfoCompat(),
                new LastAddedShortcutType(context).getShortcutInfoCompat()
        ));
    }

    public static void reportShortcutUsed(Context context, String shortcutId) {

        context.getSystemService(ShortcutManager.class).reportShortcutUsed(shortcutId);
    }

}
