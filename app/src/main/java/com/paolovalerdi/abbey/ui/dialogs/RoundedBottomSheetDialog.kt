package com.paolovalerdi.abbey.ui.dialogs

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.util.Util
import com.paolovalerdi.abbey.util.extensions.isLandscape
import com.paolovalerdi.abbey.util.extensions.isLight
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

/**
 * @author Paolo Valerdi
 */
open class RoundedBottomSheetDialog : BottomSheetDialogFragment() {

    protected val job = Job()
    private val scope = CoroutineScope(job + Main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setNavigationBarColor(requireContext().resolveAttrColor(R.attr.colorBackgroundFloating))
        }
    }

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
    }

    protected fun launch(
        context: CoroutineDispatcher = Main,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    @RequiresApi(Build.VERSION_CODES.O)
    protected fun setNavigationBarColor(color: Int) {
        dialog?.window?.let { window ->
            if (Util.hasOreoOrHigher() and resources.isLandscape.not()) {
                window.navigationBarColor = color
                if (color.isLight) window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                window.navigationBarColor = Color.BLACK
            }
        }
    }

}

