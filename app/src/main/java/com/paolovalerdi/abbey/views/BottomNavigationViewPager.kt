package com.paolovalerdi.abbey.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class BottomNavigationViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(
    context,
    attrs
) {

    var isSwipingEnabled: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?) = if (isSwipingEnabled) {
        super.onTouchEvent(ev)
    } else false

    override fun onInterceptTouchEvent(ev: MotionEvent?) = if (isSwipingEnabled) {
        super.onInterceptTouchEvent(ev)
    } else false

}