package com.paolovalerdi.abbey.appshortcuts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.paolovalerdi.abbey.model.Playlist
import com.paolovalerdi.abbey.model.smartplaylist.LastAddedPlaylist
import com.paolovalerdi.abbey.model.smartplaylist.MyTopTracksPlaylist
import com.paolovalerdi.abbey.model.smartplaylist.ShuffleAllPlaylist
import com.paolovalerdi.abbey.service.*

class AppShortcutLauncherActivity : AppCompatActivity() {

    companion object {

        const val KEY_SHORTCUT_TYPE = "com.paolovalerdi.abbey.appshortcuts.ShortcutType";
        const val SHORTCUT_TYPE_SHUFFLE_ALL = 0
        const val SHORTCUT_TYPE_TOP_TRACKS = 1
        const val SHORTCUT_TYPE_LAST_ADDED = 2
        const val SHORTCUT_TYPE_NONE = -1

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = intent?.extras?.getInt(KEY_SHORTCUT_TYPE, SHORTCUT_TYPE_NONE)
            ?: SHORTCUT_TYPE_NONE

        when (type) {
            SHORTCUT_TYPE_SHUFFLE_ALL -> {
                startServiceWithPlaylist(
                    SHUFFLE_MODE_SHUFFLE,
                    ShuffleAllPlaylist(applicationContext)
                )
            }
            SHORTCUT_TYPE_TOP_TRACKS -> {
                startServiceWithPlaylist(
                    SHUFFLE_MODE_SHUFFLE,
                    MyTopTracksPlaylist(applicationContext)
                )
            }
            SHORTCUT_TYPE_LAST_ADDED -> {
                startServiceWithPlaylist(
                    SHUFFLE_MODE_SHUFFLE,
                    LastAddedPlaylist(applicationContext)
                )
            }
        }

        finish()
    }

    private fun startServiceWithPlaylist(shuffleMode: Int, playlist: Playlist) {
        val intent = Intent(applicationContext, MusicService::class.java).apply {
            action = ACTION_PLAY_PLAYLIST

            val bundle = Bundle().apply {
                putParcelable(INTENT_EXTRA_PLAYLIST, playlist)
                putInt(INTENT_EXTRA_SHUFFLE_MODE, shuffleMode)
            }

            putExtras(bundle)
        }

        ContextCompat.startForegroundService(applicationContext, intent)
    }

}