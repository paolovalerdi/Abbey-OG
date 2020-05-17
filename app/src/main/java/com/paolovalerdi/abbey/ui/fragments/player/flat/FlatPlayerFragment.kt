package com.paolovalerdi.abbey.ui.fragments.player.flat

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.ATHUtil
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.base.MediaEntryViewHolder
import com.paolovalerdi.abbey.ui.dialogs.SongShareDialog
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.helper.menu.SongMenuHelper
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.activities.base.AbsSlidingMusicPanelActivity
import com.paolovalerdi.abbey.ui.fragments.player.base.AbsPlayerFragment
import com.paolovalerdi.abbey.ui.fragments.player.base.PagedAlbumCoverFragment
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.ViewUtil
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.extensions.screenWidth
import com.paolovalerdi.abbey.util.extensions.tintContentColorFor
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.fragment_flat_player.*

class FlatPlayerFragment : AbsPlayerFragment<FlatPlayerPlaybackControlsFragment, PagedAlbumCoverFragment>() {
    override val layoutRes: Int = R.layout.fragment_flat_player

    override val navigationBarColor: Int
        get() = requireContext().resolveAttrColor(R.attr.colorSurfaceElevated)

    override fun onFavoriteUpdated(drawable: Drawable) {
        playerToolbar?.menu?.findItem(R.id.action_toggle_favorite)?.icon = drawable
    }

    override var lastColor: Int = -1

    private lateinit var currentSongViewHolder: MediaEntryViewHolder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCurrentSongViewHolder()
    }


    override fun getPaletteColor(): Int = lastColor


    override fun onColorChanged(color: Int, isTopLight: Boolean) {
        animateColorChange(color)
        playerToolbar?.tintContentColorFor(isTopLight)
        super.onColorChanged(color, isTopLight)
    }

    override fun setUpPanelHeight() {
        val availablePanelHeight = playerSlidingPanelLayout.height - (requireActivity().screenWidth())

        playerSlidingPanelLayout.panelHeight = availablePanelHeight

        card_content?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = navigationBarHeight
        }

        (activity as AbsSlidingMusicPanelActivity).setAntiDragView(playerSlidingPanelLayout.findViewById(R.id.player_panel))

    }

    private fun initCurrentSongViewHolder() {
        currentSongViewHolder = MediaEntryViewHolder(view?.findViewById(R.id.current_song))
        currentSongViewHolder.apply {
            separator?.visibility = View.VISIBLE
            image.apply {
                this?.scaleType = ImageView.ScaleType.CENTER
                this?.setColorFilter(ATHUtil.resolveColor(activity!!, R.attr.iconColor, ThemeStore.textColorSecondary(activity!!)), PorterDuff.Mode.SRC_IN)
                this?.setImageResource(R.drawable.ic_playing_bars)
            }
            itemView.setOnClickListener {
                if (playerSlidingPanelLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    playerSlidingPanelLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                } else if (playerSlidingPanelLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    playerSlidingPanelLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
                }
            }
            menu?.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(activity as AppCompatActivity) {
                override fun getSong(): Song = MusicPlayerRemote.getCurrentSong()

                override fun getMenuRes(): Int = R.menu.menu_item_playing_queue_song

                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.action_remove_from_playing_queue -> {
                            MusicPlayerRemote.removeFromQueue(MusicPlayerRemote.getPosition())
                            return true
                        }
                        R.id.action_share -> {
                            SongShareDialog.create(song).show(childFragmentManager, "SONG_SHARE_DIALOG")
                            return true
                        }
                    }
                    return super.onMenuItemClick(item)
                }
            })
        }

    }

    override fun onCurrentSongChanged(latestSong: Song) {
        super.onCurrentSongChanged(latestSong)
        currentSongViewHolder.title?.text = latestSong.title
        currentSongViewHolder.text?.text = MusicUtil.getSongInfoString(latestSong)
    }

    private fun animateColorChange(newColor: Int) {
        val backgroundAnimator: Animator = ViewUtil.createBackgroundColorTransition(status_bar, lastColor, newColor)
        AnimatorSet().apply {
            play(backgroundAnimator)
            duration = ViewUtil.VINYL_MUSIC_PLAYER_ANIM_TIME.toLong()
        }.start()
    }

}
