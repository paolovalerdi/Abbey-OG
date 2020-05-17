package com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paolovalerdi.abbey.R

abstract class AbsLibraryPagerRecyclerViewFragment<A : RecyclerView.Adapter<*>, LM : RecyclerView.LayoutManager> : AbsLibraryPagerFragment() {

    protected lateinit var container: View
    lateinit var recyclerView: RecyclerView

    protected var mAdapter: A? = null
        private set

    protected lateinit var mLayoutManager: LM
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.fragment_main_activity_recycler_view,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view.findViewById(R.id.container)
        recyclerView = view.findViewById(R.id.recycler_view)
        initLayoutManager()
        initAdapter()
        setUpRecyclerView()
    }

    protected fun invalidateAdapter() {
        initAdapter()
        recyclerView.adapter = mAdapter
    }

    protected fun invalidateLayoutManager() {
        initLayoutManager()
        recyclerView.layoutManager = mLayoutManager
    }

    private fun initLayoutManager() {
        mLayoutManager = createLayoutManager()
    }

    private fun initAdapter() {
        mAdapter = createAdapter()
    }

    private fun setUpRecyclerView() {
        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = mAdapter
        }
    }

    protected abstract fun createLayoutManager(): LM
    protected abstract fun createAdapter(): A

}
