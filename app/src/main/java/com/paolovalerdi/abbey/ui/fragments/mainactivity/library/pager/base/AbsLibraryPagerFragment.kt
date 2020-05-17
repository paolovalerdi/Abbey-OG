package com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.base

import android.os.Bundle
import com.paolovalerdi.abbey.ui.fragments.AbsMusicServiceFragment
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.LibraryFragment

open class AbsLibraryPagerFragment : AbsMusicServiceFragment() {

    val libraryFragment: LibraryFragment
        get() = parentFragment as LibraryFragment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }
}
