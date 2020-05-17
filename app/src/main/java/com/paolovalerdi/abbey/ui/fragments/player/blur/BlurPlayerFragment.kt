package com.paolovalerdi.abbey.ui.fragments.player.blur

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.ui.dialogs.LyricsDialog
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.BlurTransformation
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.activities.base.AbsSlidingMusicPanelActivity
import com.paolovalerdi.abbey.ui.fragments.player.base.AbsPlayerFragment
import com.paolovalerdi.abbey.ui.fragments.player.base.PagedAlbumCoverFragment
import com.paolovalerdi.abbey.util.extensions.convertDpToPixels
import com.paolovalerdi.abbey.util.extensions.screenWidth
import com.paolovalerdi.abbey.util.extensions.setAlphaAndHide
import kotlinx.android.synthetic.main.fragment_blur_player.*

class BlurPlayerFragment : AbsPlayerFragment<BlurPlayerPlaybackControlsFragment, PagedAlbumCoverFragment>(), PopupMenu.OnMenuItemClickListener {
    override var lastColor: Int = -1

    override val layoutRes: Int = R.layout.fragment_blur_player

    override val navigationBarColor: Int = Color.TRANSPARENT

    override fun onFavoriteUpdated(drawable: Drawable) {
        drawable.mutate().setTint(Color.WHITE)
        favoriteIcn.setImageDrawable(drawable)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpIcons()
    }

    override fun getPaletteColor(): Int = Color.TRANSPARENT

    override fun onPanelSlide(p0: View?, offset: Float) {
        val hideAlpha = 1 - (offset * 2)
        blurIconsContainer.setAlphaAndHide(hideAlpha)
        queueContainer.alpha = offset
    }


    override fun setUpPanelHeight() {
        val panelHeight: Int = ((resources.convertDpToPixels(48f) + navigationBarHeight).toInt())
        playerSlidingPanelLayout.panelHeight = panelHeight

        queueContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = navigationBarHeight
        }

        val playbackControlsHeight = playerSlidingPanelLayout.height - (requireActivity().screenWidth() + panelHeight)
        playbackControls.view?.updateLayoutParams {
            height = playbackControlsHeight
        }

        (requireActivity() as AbsSlidingMusicPanelActivity).setAntiDragView(playerSlidingPanelLayout.findViewById(R.id.player_panel))
    }

    private fun setUpIcons() {
        favoriteIcn.setOnClickListener { toggleFavorite() }
        lyricsIcon.setOnClickListener { LyricsDialog.create(lyrics).show(childFragmentManager, null) }
        moreIcon.setOnClickListener {
            val popup = PopupMenu(requireContext(), it)
            popup.gravity = Gravity.CENTER
            popup.inflate(R.menu.menu_player_no_toolbar)
            popup.setOnMenuItemClickListener(this)
            popup.show()
        }
    }

    override fun onCurrentSongChanged(latestSong: Song) {
        super.onCurrentSongChanged(latestSong)
        GlideApp.with(this@BlurPlayerFragment)
            .load(AbbeyGlideExtension.getSongModel(latestSong))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(R.drawable.default_album_art)
            .override(500, 500)
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .transform(BlurTransformation
                .Builder(requireContext())
                .blurRadius(9f)
                .build())
            .into(blurredImage)
    }

}