package com.paolovalerdi.abbey.adapter.epoxy

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.OnViewRecycled

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ScrollAwareCarousel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Carousel(
    context,
    attrs,
    defStyleAttr
) {

    @ModelProp(options = [ModelProp.Option.DoNotHash])
    override fun addOnScrollListener(listener: OnScrollListener) {
        super.addOnScrollListener(listener)
    }

}