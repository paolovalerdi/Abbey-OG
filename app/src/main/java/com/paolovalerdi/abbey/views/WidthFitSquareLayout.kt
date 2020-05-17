package com.paolovalerdi.abbey.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class WidthFitSquareLayout : FrameLayout {

    private var forceSquare = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            if (forceSquare) widthMeasureSpec else heightMeasureSpec
        )
    }

    fun forceSquare(forceSquare: Boolean) {
        this.forceSquare = forceSquare
        requestLayout()
    }
}
