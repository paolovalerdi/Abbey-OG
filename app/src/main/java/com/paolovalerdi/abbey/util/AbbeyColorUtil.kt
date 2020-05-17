package com.paolovalerdi.abbey.util

import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach

/**
 * @author Paolo Valerdi
 */

object AbbeyAppColorUtil {

    fun setToolbarContentColor(toolbar: Toolbar, color: Int) {
        toolbar.apply {
            navigationIcon?.mutate()?.setTint(color)
            overflowIcon?.mutate()?.setTint(color)
            menu.forEach { menuItem ->
                menuItem.icon?.mutate()?.setTint(color)
            }
        }

    }
}

