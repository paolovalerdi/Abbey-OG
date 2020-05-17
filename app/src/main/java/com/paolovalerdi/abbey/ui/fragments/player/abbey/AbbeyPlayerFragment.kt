package com.paolovalerdi.abbey.ui.fragments.player.abbey

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.ui.dialogs.LyricsDialog
import com.paolovalerdi.abbey.ui.activities.base.AbsSlidingMusicPanelActivity
import com.paolovalerdi.abbey.ui.fragments.player.base.AbsPlayerFragment
import com.paolovalerdi.abbey.ui.fragments.player.base.PagedAlbumCoverFragment
import com.paolovalerdi.abbey.util.extensions.setAlphaAndHide
import com.paolovalerdi.abbey.util.extensions.tintContentColorFor
import kotlinx.android.synthetic.main.fragment_abbey_player.*
import kotlinx.android.synthetic.main.fragment_player_abbey_playback_controls.*

/**
 * @author Paolo Valerdi
 */
class AbbeyPlayerFragment : AbsPlayerFragment<AbbeyPlayerPlaybackControlsFragment, PagedAlbumCoverFragment>() {

    override val layoutRes: Int = R.layout.fragment_abbey_player

    override var lastColor: Int = -1

    override val navigationBarColor: Int = Color.TRANSPARENT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerToolbar?.run {
            menu.removeItem(R.id.action_toggle_favorite)
            menu.removeItem(R.id.action_show_lyrics)
            tintContentColorFor(false)
        }
        playbackControls.favoriteIcon?.setOnClickListener { toggleFavorite() }
        playbackControls.moreIcon?.setOnClickListener { LyricsDialog.create(lyrics).show(childFragmentManager, null) }
    }


    override fun onFavoriteUpdated(drawable: Drawable) {
        drawable.mutate().setTint(Color.WHITE)
        playbackControls.favoriteIcon.setImageDrawable(drawable)
    }

    override fun getPaletteColor(): Int = Color.TRANSPARENT

    override fun onPanelSlide(p0: View?, offset: Float) {
        val alpha = 1 - offset
        playerToolbar?.setAlphaAndHide(alpha)
    }

    override fun setUpPanelHeight() {
        val panelHeight: Int = (playbackControls.view!!.height) + navigationBarHeight
        playerSlidingPanelLayout.panelHeight = panelHeight

        card_content.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = navigationBarHeight
        }

        (activity as AbsSlidingMusicPanelActivity).setAntiDragView(playerSlidingPanelLayout.findViewById(R.id.player_panel))
    }

}