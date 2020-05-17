package com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.GridSpacingItemDecoration
import com.paolovalerdi.abbey.util.extensions.convertDpToPixels
import com.paolovalerdi.abbey.util.extensions.isLandscape

/**
 * @author Paolo Valerdi
 */
abstract class AbsLibraryPagerRecyclerViewCustomGridSizeFragment<A : RecyclerView.Adapter<*>, LM : RecyclerView.LayoutManager> : AbsLibraryPagerRecyclerViewFragment<A, LM>() {

    var gridSize = 0
        get() {
            if (field == 0) {
                field = if (resources.isLandscape) {
                    loadGridSizeLand()
                } else {
                    loadGridSize()
                }
            }
            return field
        }
        set(value) {
            field = value
            setItemDecorator(field)
        }

    val maxGridSizeForList
        get() = if (resources.isLandscape)
            resources.getInteger(R.integer.default_list_columns_land)
        else
            resources.getInteger(R.integer.default_list_columns)

    val maxGridSize
        get() = if (resources.isLandscape) {
            resources.getInteger(R.integer.max_columns_land)
        } else {
            resources.getInteger(R.integer.max_columns)
        }

    protected open val itemLayoutRes: Int
        get() = if (gridSize > maxGridSizeForList) {
            R.layout.item_grid_card
        } else R.layout.item_list

    private val sortOrder: String
        get() = loadSortOrder()

    private var currenLayoutRes = 0

    // Grid Size

    protected abstract fun loadGridSize(): Int
    protected abstract fun saveGridSize()
    protected abstract fun loadGridSizeLand(): Int
    protected abstract fun saveGridSizeLand()
    protected abstract fun updateGridSize(newGridSize: Int)

    // Sort Order

    protected abstract fun loadSortOrder(): String
    protected abstract fun saveSortOrder(sortOrder: String)
    protected abstract fun updateSortOrder()

    // As viewpager destroys the fragment view, it's necessary to set the item decorator again.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setItemDecorator(gridSize)
    }

    fun setAndSaveGridSize(gridSize: Int) {
        val oldLayoutRes = itemLayoutRes
        this.gridSize = gridSize
        if (resources.isLandscape) {
            saveGridSizeLand()
        } else {
            saveGridSize()
        }

        if (oldLayoutRes != itemLayoutRes) {
            invalidateLayoutManager()
            invalidateAdapter()
        } else {
            updateGridSize(gridSize)
        }
    }

    fun setAndSaveSortOrder(sortOrder: String) {
        saveSortOrder(sortOrder)
        updateSortOrder()
    }

    protected fun notifyLayoutResChanged(@LayoutRes res: Int) {
        currenLayoutRes = res
    }

    private fun setItemDecorator(gridSize: Int) {
        if (gridSize == 0) return
        removeItemDecorator()
        val itemSpacing = if (gridSize == 1) 0 else resources.convertDpToPixels(8f).toInt()
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                gridSize,
                itemSpacing,
                true,
                1
            ),
            0)
    }

    private fun removeItemDecorator() {
        if (recyclerView.itemDecorationCount == 1) {
            recyclerView.removeItemDecorationAt(0)
        }
    }

}