package com.paolovalerdi.abbey.ui.fragments.player.base

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.player.NowPlayingCoverAdapter
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.activities.base.AbsMusicServiceActivity
import com.paolovalerdi.abbey.ui.fragments.player.CarouselTransformer
import com.paolovalerdi.abbey.ui.fragments.player.NowPlayingScreen
import com.paolovalerdi.abbey.util.extensions.convertDpToPixels
import com.paolovalerdi.abbey.util.extensions.dpToPx
import com.paolovalerdi.abbey.util.preferences.MATERIAL_NOW_PLAYING_PADDING
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_player_album_cover.*
import kotlinx.android.synthetic.main.fragment_player_album_cover.view.*

/**
 * TODO: A value very close to 0 doesn't place the viewpager current item correctly.
 * @author Paolo Valerdi
 */
class PagedAlbumCoverFragment : AbsPlayerAlbumCoverFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val viewPagerCallbacks = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            if (position != MusicPlayerRemote.getPosition()) {
                MusicPlayerRemote.playSongAt(position)
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as AbsMusicServiceActivity).serviceViewModel.getIsPlaying().observe(
            viewLifecycleOwner,
            Observer { isPlaying ->
                if (PreferenceUtil.nowPlayingScreen == NowPlayingScreen.MATERIAL) {
                    onPlayStateChanged(isPlaying)
                }
            }
        )
    }

    private lateinit var nowPlayingCoverAdapter: NowPlayingCoverAdapter

    override val layoutRes: Int = R.layout.fragment_player_album_cover

    override fun onPlayingQueueChange(newPlayingQueue: List<Song>) {
        nowPlayingCoverAdapter.updateQueue(newPlayingQueue)
        playerAlbumViewPager.setCurrentItem(latestPosition)
    }

    override fun onCurrentQueuePostionChange(newPosition: Int) {
        Log.d("MUSICVIEWMODEL", "onCurrentPositionChanged newPosition = $newPosition and latestPosition = ${MusicPlayerRemote.getPosition()}")
        playerAlbumViewPager.currentItem = newPosition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (PreferenceUtil.nowPlayingScreen) {
            NowPlayingScreen.ABBEY -> {
                playerAlbumOverlay.isVisible = true
            }
            NowPlayingScreen.MATERIAL -> {
                updateLyricsMargins()
            }
            NowPlayingScreen.BLUR -> {
                playerAlbumViewPager.apply {
                    (getChildAt(0) as? RecyclerView?).run {
                        setPadding(dpToPx(28f))
                    }
                    clipToPadding = false
                    clipChildren = false
                    setPageTransformer(MarginPageTransformer(dpToPx(22f)))

                }
                playerAlbumLyricsContainer.radius = resources.getDimensionPixelSize(R.dimen.corner_radius_medium).toFloat()
                playerAlbumLyricsContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    setMargins(resources.convertDpToPixels(28f).toInt())
                }
            }
        }

        nowPlayingCoverAdapter = NowPlayingCoverAdapter()
        playerAlbumViewPager.apply {
            adapter = nowPlayingCoverAdapter
            registerOnPageChangeCallback(viewPagerCallbacks)
            offscreenPageLimit = 1
        }
        PreferenceUtil.registerOnSharedPreferenceChangedListener(this)
    }

    override fun onDestroyView() {
        PreferenceUtil.unregisterOnSharedPreferenceChangedListener(this)
        playerAlbumViewPager.unregisterOnPageChangeCallback(viewPagerCallbacks)
        super.onDestroyView()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == MATERIAL_NOW_PLAYING_PADDING) {
            nowPlayingCoverAdapter.notifyDataSetChanged()
            updateLyricsMargins()
        }
    }

    private fun onPlayStateChanged(isPlaying: Boolean) {
        val scale = if (isPlaying) 1f else 0.90f
        val alpha = if (isPlaying) 1f else 0f
        playerAlbumViewPager.animate().scaleY(scale).scaleX(scale).setDuration(250L).setInterpolator(OvershootInterpolator()).start()
    }

    private fun updateLyricsMargins() {
        playerAlbumLyricsContainer.radius = resources.getDimensionPixelSize(R.dimen.corner_radius_medium).toFloat()
        playerAlbumLyricsContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(resources.convertDpToPixels(PreferenceUtil.imagePadding.toFloat()).toInt())
        }
    }

}