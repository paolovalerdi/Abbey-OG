package com.paolovalerdi.abbey.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import androidx.core.view.updateLayoutParams

class NavigationBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        updateLayoutParams {
            height = insets.systemWindowInsetBottom
        }
        return insets
    }

}
