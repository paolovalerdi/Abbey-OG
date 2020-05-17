package com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.paolovalerdi.abbey.adapter.playlist.GenreAdapterKt
import com.paolovalerdi.abbey.model.Genre
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base.AbsLibraryPagerRecyclerViewCustomGridSizeFragment
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil

/**
 * @author Paolo Valerdi
 */
class GenresFragment : AbsLibraryPagerRecyclerViewCustomGridSizeFragment<GenreAdapterKt, GridLayoutManager>() {

    private var cachedGenres: List<Genre>? = null
        set(value) {
            field = value
            field?.run {
                mAdapter?.dataSet = this
            }
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        libraryFragment.viewModel.getAllGenres().observe(viewLifecycleOwner,
            Observer<List<Genre>> { genres ->
                cachedGenres = genres
            })
    }

    override fun createLayoutManager(): GridLayoutManager = GridLayoutManager(activity, gridSize).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                if (position == 0)
                    gridSize
                else 1
        }
    }

    override fun createAdapter(): GenreAdapterKt = GenreAdapterKt(
        this,
        itemLayoutRes,
        cachedGenres ?: emptyList(),
        libraryFragment
    )

    override fun loadGridSize(): Int = PreferenceUtil.genreGridSize

    override fun saveGridSize() {
        PreferenceUtil.genreGridSize = gridSize
    }

    override fun loadGridSizeLand(): Int = PreferenceUtil.genreGridSizeLand

    override fun saveGridSizeLand() {
        PreferenceUtil.genreGridSizeLand = gridSize
    }

    override fun updateGridSize(newGridSize: Int) {
        mLayoutManager.spanCount = newGridSize
        mAdapter?.notifyDataSetChanged()
    }

    override fun loadSortOrder(): String = ""
    override fun saveSortOrder(sortOrder: String) {}
    override fun updateSortOrder() {}
}
