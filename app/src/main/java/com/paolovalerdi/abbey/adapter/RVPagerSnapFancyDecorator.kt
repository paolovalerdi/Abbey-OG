package com.paolovalerdi.abbey.adapter

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

open class RVPagerSnapFancyDecorator : RecyclerView.ItemDecoration {
    private val mInterItemsGap: Int
    private val mNetOneSidedGap: Int

    constructor(context: Context, @Px itemWidth: Int, itemPeekingPercent: Float = .035f)
        : this(context.resources.displayMetrics.widthPixels, itemWidth, itemPeekingPercent)

    constructor(@Px totalWidth: Int, @Px itemWidth: Int, itemPeekingPercent: Float = .035f) {
        val cardPeekingWidth = (itemWidth * itemPeekingPercent + .5f).toInt()

        mInterItemsGap = (totalWidth - itemWidth) / 2
        mNetOneSidedGap = mInterItemsGap / 2 - cardPeekingWidth
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val index = parent.getChildAdapterPosition(view)
        val isFirstItem = isFirstItem(index)
        val isLastItem = isLastItem(index, parent)

        val leftInset = if (isFirstItem) mInterItemsGap else mNetOneSidedGap
        val rightInset = if (isLastItem) mInterItemsGap else mNetOneSidedGap

        outRect.set(leftInset, 0, rightInset, 0)
    }

    private fun isFirstItem(index: Int) = index == 0
    private fun isLastItem(index: Int, recyclerView: RecyclerView) = index == recyclerView.adapter!!.itemCount - 1
}