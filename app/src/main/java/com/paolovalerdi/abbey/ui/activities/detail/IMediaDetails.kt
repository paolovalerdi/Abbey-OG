package com.paolovalerdi.abbey.ui.activities.detail

interface IMediaDetails {

    fun setUpRecyclerView()
    fun setUpToolbar()
    fun loadImage()
    fun setColors(backgroundColor: Int, accentColor: Int)
    fun loadContent()

}