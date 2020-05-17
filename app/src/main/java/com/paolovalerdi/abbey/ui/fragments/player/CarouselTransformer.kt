package com.paolovalerdi.abbey.ui.fragments.player

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.paolovalerdi.abbey.util.extensions.convertDpToPixels
import kotlin.math.abs


class CarouselTransformer(context: Context) : ViewPager2.PageTransformer {

    private val maxTranslateOffsetX: Int = context.resources.convertDpToPixels(180f).toInt()
    private var viewPager: RecyclerView? = null

    override fun transformPage(view: View, position: Float) {
        if (viewPager == null) {
            viewPager = view.parent as RecyclerView
        }

        val leftInScreen = view.left - viewPager!!.scrollX
        val centerXInViewPager = leftInScreen + view.width / 2
        val offsetX = centerXInViewPager - viewPager!!.width / 2
        val offsetRate = offsetX.toFloat() * 0.38f / viewPager!!.width
        val scaleFactor = 1 - abs(offsetRate)

        if (scaleFactor > 0) {
            view.scaleX = scaleFactor
            view.scaleY = scaleFactor
            view.translationX = -maxTranslateOffsetX * offsetRate
        }
    }
}