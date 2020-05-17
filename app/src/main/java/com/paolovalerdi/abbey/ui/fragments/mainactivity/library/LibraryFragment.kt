package com.paolovalerdi.abbey.ui.fragments.mainactivity.library

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialcab.MaterialCab
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.MusicLibraryPagerAdapter
import com.paolovalerdi.abbey.ui.dialogs.CreatePlaylistDialogKt
import com.paolovalerdi.abbey.interfaces.CabHolder
import com.paolovalerdi.abbey.ui.activities.MainActivity
import com.paolovalerdi.abbey.ui.activities.SearchActivity
import com.paolovalerdi.abbey.ui.fragments.mainactivity.AbsMainActivityFragment
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.pager.PlaylistFragment
import com.paolovalerdi.abbey.util.Util
import com.paolovalerdi.abbey.util.extensions.applyColor
import com.paolovalerdi.abbey.util.extensions.doOnApplyWindowInsets
import com.paolovalerdi.abbey.util.extensions.isLight
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.preferences.LIBRARY_CATEGORIES
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import com.paolovalerdi.abbey.views.ContextualToolbar
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.sliding_music_panel_layout.*


class LibraryFragment : AbsMainActivityFragment(),
    CabHolder,
    MainActivity.MainActivityFragmentCallbacks,
    ViewPager.OnPageChangeListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {

        fun newInstance(): LibraryFragment = LibraryFragment()

    }

    private var cab: MaterialCab? = null

    private lateinit var fragmentAdapter: MusicLibraryPagerAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainActivity.hideBottomNavigationBar(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_library, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        PreferenceUtil.registerOnSharedPreferenceChangedListener(this)
        setUpViewPager()
        setUpAppBar()
    }

    override fun onDestroyView() {
        PreferenceUtil.unregisterOnSharedPreferenceChangedListener(this)
        super.onDestroyView()
        libraryViewPager.removeOnPageChangeListener(this)
    }

    override fun openCab(menuRes: Int, callback: MaterialCab.Callback?): MaterialCab {
        val primaryColor = requireContext().resolveAttrColor(R.attr.colorSurfaceElevated)
        cab?.run { if (isActive) finish() }
        cab = ContextualToolbar(activity as AppCompatActivity, R.id.cab_stub)
            .setMenu(menuRes)
            .setCloseDrawableRes(R.drawable.ic_close)
            .setPopupMenuTheme(if (primaryColor.isLight) R.style.Widget_MPM_Menu_RoundedPopUpMenuTheme else R.style.Widget_MPM_Menu_Dark_RoundedPopUpMenuTheme)
            .setBackgroundColor(primaryColor)
            .start(callback)
        return cab as MaterialCab
    }

    override fun handleBackPress(): Boolean {
        cab?.run {
            if (isActive) {
                finish()
                return true
            }
        }
        return false
    }

    override fun reloadUserImage() {
        loadUserImageInto(userImage)
    }

    override fun onPageSelected(position: Int) {
        mainActivity.bottomNavigationView.selectedItemId = position
        showFab(fragmentAdapter.getFragment(position) is PlaylistFragment)
        PreferenceUtil.lastPage = position
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == LIBRARY_CATEGORIES) {
            fragmentAdapter.setCategories(PreferenceUtil.libraryCategories)
            var position = fragmentAdapter.getItemPosition(fragmentAdapter.getFragment(libraryViewPager.currentItem))
            if (position < 0) position = 0
            libraryViewPager?.offscreenPageLimit = fragmentAdapter.count - 1
            libraryViewPager?.currentItem = position
            mainActivity.getBottomNavigationBar().selectedItemId = position
            PreferenceUtil.lastPage = position
        }
    }

    private fun setUpViewPager() {
        populateBottomBar()
        fragmentAdapter = MusicLibraryPagerAdapter(requireContext(), childFragmentManager)

        val lastPosition = PreferenceUtil.lastPage

        mainActivity.getBottomNavigationBar().selectedItemId = lastPosition

        Log.d("Bottom navigation bar", "Shown fragments ${fragmentAdapter.count}")

        libraryViewPager?.apply {
            adapter = fragmentAdapter
            currentItem = lastPosition
            offscreenPageLimit = fragmentAdapter.count - 1
            addOnPageChangeListener(this@LibraryFragment)
        }
    }

    private fun setUpAppBar() {
        toolbar_container.doOnApplyWindowInsets { v, insets ->
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.systemWindowInsetTop
            }
        }

        val statusBarColor = if (Util.hasMarshmallowOrHigher()) requireContext().resolveAttrColor(R.attr.colorSurfaceElevated) else Color.BLACK
        val alpha = if (Util.hasMarshmallowOrHigher()) 0.85f else 0.20f
        statusBar.setBackgroundColor(ColorUtil.withAlpha(statusBarColor, alpha))

        userImage.apply {
            loadUserImageInto(this)
            setOnClickListener {
                mainActivity.showBottomNavigation()
            }
        }

        searchIcon.setOnClickListener { startActivity(Intent(mainActivity, SearchActivity::class.java)) }

        libraryFab?.apply {
            applyColor(ThemeStore.accentColor(requireContext()))
            if (fragmentAdapter.getFragment(PreferenceUtil.lastPage) is PlaylistFragment) show() else hide()
            setOnClickListener { CreatePlaylistDialogKt.create().show(childFragmentManager, null) }
        }

        mainActivity.getBottomNavigationBar().setOnNavigationItemSelectedListener { item ->
            libraryViewPager?.currentItem = item.itemId
            true
        }
    }

    private fun populateBottomBar(shouldPopulate: Boolean = false) {
        mainActivity.populateBottomBar(PreferenceUtil.libraryCategories, shouldPopulate)
    }

    private fun showFab(show: Boolean) {
        if (show) libraryFab?.show() else libraryFab?.hide()
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

}
