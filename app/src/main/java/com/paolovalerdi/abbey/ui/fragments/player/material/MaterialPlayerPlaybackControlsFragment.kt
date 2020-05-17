package com.paolovalerdi.abbey.ui.fragments.player.material

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.ui.dialogs.CreatePlaylistDialogKt
import com.paolovalerdi.abbey.ui.dialogs.SleepTimerDialog
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.helper.PlayPauseButtonOnClickHandler
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.fragments.player.base.AbsPlaybackControlsFragment
import com.paolovalerdi.abbey.util.NavigationUtil
import com.paolovalerdi.abbey.util.extensions.animateColoChanging
import com.paolovalerdi.abbey.util.extensions.animateColorChanging
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.extensions.setTint
import kotlinx.android.synthetic.main.fragment_material_player_playback_controls.*

/**
 * @author Paolo Valerdi
 */
class MaterialPlayerPlaybackControlsFragment : AbsPlaybackControlsFragment(), PopupMenu.OnMenuItemClickListener {

    @ColorInt
    private var lastColor = -1

    override fun onCurrentSongChanged(song: Song) {
        song_name?.text = song.title
        song_subtext?.text = song.artistName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moreIcon?.apply {
            setTint(lastPlaybackControlsColor)
            setOnClickListener {
                PopupMenu(context, it).apply {
                    gravity = Gravity.END
                    inflate(R.menu.menu_player_no_toolbar)
                    setOnMenuItemClickListener(this@MaterialPlayerPlaybackControlsFragment)
                }.show()
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_clear_playing_queue -> {
                MusicPlayerRemote.clearQueue()
                return true
            }
            R.id.action_save_playing_queue -> {
                CreatePlaylistDialogKt.create(MusicPlayerRemote.getPlayingQueue()).show(childFragmentManager, "ADD_TO_PLAYLIST")
                return true
            }
            R.id.action_sleep_timer -> {
                SleepTimerDialog().show(childFragmentManager, "SLEEP_TIMER")
                return true
            }
            R.id.action_equalizer -> {
                NavigationUtil.openEqualizer(activity as AppCompatActivity)
                return true
            }

            R.id.action_go_to_album -> {
                NavigationUtil.goToArtist(
                    activity as AppCompatActivity,
                    MusicPlayerRemote.getCurrentSong().artistId
                )
                return true
            }
            R.id.action_go_to_artist -> {
                NavigationUtil.goToArtist(
                    activity as AppCompatActivity,
                    MusicPlayerRemote.getCurrentSong().artistId
                )
                return true
            }
        }
        return false
    }

    override val layoutRes = R.layout.fragment_material_player_playback_controls

    override fun initPlaybackControlsColor() = requireContext().resolveAttrColor(R.attr.colorControlNormal)

    override fun setUpPlayPauseButton(view: View) {
        player_play_pause_fab.apply {
            setImageDrawable(playPauseDrawable)
            setOnClickListener(PlayPauseButtonOnClickHandler())
        }
    }

    override fun setColors(color: Int) {
        song_subtext.animateColorChanging(lastColor, color)
        player_play_pause_fab.animateColoChanging(lastColor, color)
        progressBar.animateColorChanging(lastColor, color)
        volumeSliderFragment.updateColor(lastColor, color)
        lastColor = color
    }
}