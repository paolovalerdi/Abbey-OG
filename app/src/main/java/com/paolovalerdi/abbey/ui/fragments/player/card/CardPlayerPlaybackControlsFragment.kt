package com.paolovalerdi.abbey.ui.fragments.player.card

import android.view.View
import androidx.annotation.ColorInt
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.helper.PlayPauseButtonOnClickHandler
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.fragments.player.base.AbsPlaybackControlsFragment
import com.paolovalerdi.abbey.util.extensions.animateColoChanging
import com.paolovalerdi.abbey.util.extensions.animateColorChanging
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import kotlinx.android.synthetic.main.fragment_card_player_playback_controls.*

/**
 * @author Paolo Valerdi
 */
class CardPlayerPlaybackControlsFragment : AbsPlaybackControlsFragment() {

    @ColorInt
    private var lastColor = -1

    override val layoutRes: Int = R.layout.fragment_card_player_playback_controls

    override fun initPlaybackControlsColor() = requireContext().resolveAttrColor(R.attr.colorControlNormal)

    override fun setUpPlayPauseButton(view: View) {
        player_play_pause_fab.apply {
            setImageDrawable(playPauseDrawable)
            setOnClickListener(PlayPauseButtonOnClickHandler())
        }
    }

    override fun setColors(color: Int) {
        player_play_pause_fab.animateColoChanging(lastColor, color)
        progressBar.animateColorChanging(lastColor, color, true)
        volumeSliderFragment.updateColor(lastColor, color)
        lastColor = color
    }

    override fun onCurrentSongChanged(song: Song) {}

}
