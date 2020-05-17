package com.paolovalerdi.abbey.ui.activities.base

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.interfaces.NowPlayingCallback
import com.paolovalerdi.abbey.model.CategoryInfo
import com.paolovalerdi.abbey.ui.fragments.player.MiniPlayerFragment
import com.paolovalerdi.abbey.ui.fragments.player.NowPlayingScreen
import com.paolovalerdi.abbey.ui.fragments.player.abbey.AbbeyPlayerFragment
import com.paolovalerdi.abbey.ui.fragments.player.base.AbsPlayerFragment
import com.paolovalerdi.abbey.ui.fragments.player.blur.BlurPlayerFragment
import com.paolovalerdi.abbey.ui.fragments.player.card.CardPlayerFragmentKt
import com.paolovalerdi.abbey.ui.fragments.player.flat.FlatPlayerFragment
import com.paolovalerdi.abbey.ui.fragments.player.material.MaterialPlayerFragment
import com.paolovalerdi.abbey.util.Util
import com.paolovalerdi.abbey.util.extensions.*
import com.paolovalerdi.abbey.util.preferences.BOTTOM_BAR_LABEL_MODE
import com.paolovalerdi.abbey.util.preferences.LIBRARY_CATEGORIES
import com.paolovalerdi.abbey.util.preferences.PARALLAX_EFFECT
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.sliding_music_panel_layout.*

/**
 * Do not use [.setContentView]. Instead wrap your layout with
 * [.wrapSlidingMusicPanel] first and then return it in [.createContentView]
 */
abstract class AbsSlidingMusicPanelActivity : AbsMusicServiceActivity()
    , SlidingUpPanelLayout.PanelSlideListener
    , NowPlayingCallback
    , SharedPreferences.OnSharedPreferenceChangeListener {

    private var navBarColor = 0
    private var lightNavigation = false
    private var placedFromOnServiced = false

    private var isBottomBarPopulated = false
    private lateinit var miniPlayer: MiniPlayerFragment
    private lateinit var fragment: AbsPlayerFragment<*, *>
    private lateinit var currentNowPlayingScreen: NowPlayingScreen

    private val argbEvaluator: ArgbEvaluator = ArgbEvaluator()
    private val panelState: SlidingUpPanelLayout.PanelState
        get() = slidingLayout.panelState

    var navigationBarHeight: Int = 0
    private var insetRequestRecord = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createContentView())
        allowDrawUnderStatusBar()
        allowDrawUnderNavigationBar()

        setUpNowPlayingScreenFragment()
        setUpMiniPlayerFragment()
        setUpSlidingPanel()
        setUpBottomNavigationBar()
        PreferenceUtil.registerOnSharedPreferenceChangedListener(this)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        slidingLayout.doOnApplyWindowInsets { _, insets ->
            navigationBarHeight = insets.systemWindowInsetBottom
            bottomNavigationView?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = navigationBarHeight
            }
            hideMiniPlayer(MusicPlayerRemote.getPlayingQueue().isEmpty())
        }

    }

    override fun onQueueChanged() {
        super.onQueueChanged()
        hideMiniPlayer(MusicPlayerRemote.getPlayingQueue().isEmpty())
    }

    override fun onResume() {
        super.onResume()
        if (currentNowPlayingScreen != PreferenceUtil.nowPlayingScreen) {
            setUpNowPlayingScreenFragment()
        }
    }

    override fun onPanelSlide(panel: View?, slideOffSet: Float) {
        val alpha = 1 - slideOffSet
        miniPlayer.view?.setAlphaAndHide(alpha)
        bottomNavigationView.translationY = slideOffSet * 500
        navBarColor = if (Util.hasOreoOrHigher()) navBarColor else Color.BLACK
        super.setNavigationBarColor(argbEvaluator.evaluate(slideOffSet, navBarColor, fragment.navigationBarColor) as Int)
    }

    override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
        when (newState) {
            SlidingUpPanelLayout.PanelState.COLLAPSED -> onPanelCollapsed()
            SlidingUpPanelLayout.PanelState.EXPANDED -> onPanelExpanded()
            SlidingUpPanelLayout.PanelState.ANCHORED -> collapsePanel()
            else -> {
            }
        }
    }

    override fun onPaletteColorChanged() {
        if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            val playerColor = fragment.paletteColor
            super.setLightStatusBar(ColorUtil.isColorLight(playerColor))
            super.setLightNavigationBar(ColorUtil.isColorLight(fragment.navigationBarColor))
        }
    }

    override fun setLightStatusBar(isColorLight: Boolean) {
        lightNavigation = isColorLight
        if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) super.setLightStatusBar(isColorLight)
    }

    override fun setNavigationBarColor(color: Int) {
        navBarColor = color
        if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) super.setNavigationBarColor(color)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PARALLAX_EFFECT -> setUpParallax(PreferenceUtil.parallaxEffect)
            BOTTOM_BAR_LABEL_MODE -> bottomNavigationView.labelVisibilityMode = PreferenceUtil.bottomBarLabelMode
            LIBRARY_CATEGORIES -> populateBottomBar(PreferenceUtil.libraryCategories, true)
        }
        if (key == PARALLAX_EFFECT) {
            setUpParallax(PreferenceUtil.parallaxEffect)
        } else if (key == BOTTOM_BAR_LABEL_MODE) {
            bottomNavigationView.labelVisibilityMode = PreferenceUtil.bottomBarLabelMode
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceUtil.unregisterOnSharedPreferenceChangedListener(this)
    }

    override fun onBackPressed() {
        if (handleBackPress().not()) super.onBackPressed()
    }

    protected open fun handleBackPress(): Boolean {
        if ((slidingLayout.panelHeight != 0).and(fragment.onBackPressed()))
            return true
        if (slidingLayout.isExpanded) {
            collapsePanel()
            return true
        }
        return false
    }

    fun onPanelCollapsed() {
        super.setLightStatusBar(lightNavigation)
        super.setNavigationBarColor(navBarColor)
        fragment.setMenuVisibility(false)
        fragment.onHide()
    }

    fun onPanelExpanded() {
        val playerColor = fragment.paletteColor
        super.setLightStatusBar(ColorUtil.isColorLight(playerColor))
        super.setLightNavigationBar(ColorUtil.isColorLight(fragment.navigationBarColor))
        fragment.setMenuVisibility(true)
        fragment.onShow()
    }

    fun setAntiDragView(antiDragView: View) {
        slidingLayout.setAntiDragView(antiDragView)
    }

    fun getMenu(): Menu = bottomNavigationView.menu

    fun getBottomNavigationBar() = bottomNavigationView

    fun clearMenu() {
        bottomNavigationView.menu.clear()
    }

    fun hideBottomNavigationBar(hide: Boolean) {
        bottomNavigationView.isGone = hide
        hideMiniPlayer(MusicPlayerRemote.getPlayingQueue().isEmpty())
    }

    fun populateBottomBar(categories: List<CategoryInfo>, shouldPopulate: Boolean = false) {
        if (isBottomBarPopulated.not() or shouldPopulate) {
            var indexID = 0
            clearMenu()
            val menu = getBottomNavigationBar().menu
            categories.forEach { categoryInfo ->
                if (categoryInfo.visible) {
                    menu.add(
                        Menu.NONE,
                        indexID,
                        Menu.NONE,
                        categoryInfo.category.stringRes
                    ).setIcon(categoryInfo.category.iconRes)
                    Log.d("Bottom navigation bar", "${getString(categoryInfo.category.stringRes)} added at $indexID")
                    indexID++
                }
            }
            isBottomBarPopulated = true
        }
    }

    @SuppressLint("InflateParams")
    protected fun wrapInSlidingMusicPanel(layoutResId: Int): View {
        val slidingMusicPanelLayout: View = layoutInflater.inflate(R.layout.sliding_music_panel_layout, null)
        val contentContainer: ViewGroup = slidingMusicPanelLayout.findViewById(R.id.mainContentContainer)
        layoutInflater.inflate(layoutResId, contentContainer)
        return slidingMusicPanelLayout
    }

    protected fun getMiniPlayer(): MiniPlayerFragment = miniPlayer

    private fun collapsePanel() {
        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    private fun expandPanel() {
        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    private fun setUpNowPlayingScreenFragment() {
        currentNowPlayingScreen = PreferenceUtil.nowPlayingScreen
        fragment = when (currentNowPlayingScreen) {
            NowPlayingScreen.ABBEY -> AbbeyPlayerFragment()
            NowPlayingScreen.MATERIAL -> MaterialPlayerFragment()
            NowPlayingScreen.FLAT -> FlatPlayerFragment()
            NowPlayingScreen.BLUR -> BlurPlayerFragment()
            else -> CardPlayerFragmentKt()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.playerFragmentContainer, fragment)
            .commit()
        supportFragmentManager.executePendingTransactions()
    }

    private fun setUpMiniPlayerFragment() {
        miniPlayer = supportFragmentManager.findFragmentById(R.id.miniPlayerFragment) as MiniPlayerFragment
        miniPlayer.view?.setOnClickListener { expandPanel() }
    }

    private fun setUpSlidingPanel() {
        slidingLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                slidingLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                when (panelState) {
                    SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                        onPanelCollapsed()
                    }
                    SlidingUpPanelLayout.PanelState.EXPANDED -> {
                        onPanelSlide(slidingLayout, 1f)
                        onPanelExpanded()
                    }
                    else -> fragment.onHide()
                }
            }
        })
        slidingLayout.addPanelSlideListener(this)
        setUpParallax(PreferenceUtil.parallaxEffect)
    }

    private fun setUpParallax(enable: Boolean) {
        val offSet = if (enable) resources.convertDpToPixels(60f).toInt() else 0
        slidingLayout.setParallaxOffset(offSet)
    }

    private fun setUpBottomNavigationBar() {
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ),
            intArrayOf(
                ColorUtil.withAlpha(resolveAttrColor(R.attr.colorControlNormal), 0.38f),
                ThemeStore.accentColor(this)
            )
        )
        bottomNavigationView.itemIconTintList = colorStateList
        bottomNavigationView.itemTextColor = colorStateList
        bottomNavigationView.itemRippleColor = ColorStateList.valueOf(ColorUtil.withAlpha(ThemeStore.accentColor(this), 0.10f))
        bottomNavigationView.labelVisibilityMode = PreferenceUtil.bottomBarLabelMode
    }

    private fun hideMiniPlayer(hide: Boolean) {
        val miniPlayerHeight = resources.getDimensionPixelSize(R.dimen.mini_player_height)
        val isBottomBarVisible = bottomNavigationView.isVisible
        if (hide) {
            collapsePanel()
            ViewCompat.setElevation(bottomNavigationView, 4f)
            slidingLayout.apply {
                shadowHeight = 0
                panelHeight = if (isBottomBarVisible) miniPlayerHeight + (navigationBarHeight - 15) else navigationBarHeight - 15
            }
        } else {
            ViewCompat.setElevation(bottomNavigationView, 0f)
            slidingLayout.apply {
                shadowHeight = resources.getDimensionPixelSize(R.dimen.toolbar_elevation)
                panelHeight = if (isBottomBarVisible) (miniPlayerHeight * 2) + navigationBarHeight else (navigationBarHeight + miniPlayerHeight)
            }
        }
    }

    protected abstract fun createContentView(): View
}
