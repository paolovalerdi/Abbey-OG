package com.paolovalerdi.abbey.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.pm.PackageInfoCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import androidx.lifecycle.ViewModelProviders
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.ui.dialogs.BottomNavigationDialog
import com.paolovalerdi.abbey.ui.dialogs.ChangelogDialog
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.helper.SearchQueryHelper
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.repository.AlbumRepository
import com.paolovalerdi.abbey.repository.ArtistRepository
import com.paolovalerdi.abbey.repository.PlaylistSongRepository
import com.paolovalerdi.abbey.service.SHUFFLE_MODE_SHUFFLE
import com.paolovalerdi.abbey.ui.activities.base.AbsSlidingMusicPanelActivity
import com.paolovalerdi.abbey.ui.fragments.mainactivity.folders.FoldersFragment
import com.paolovalerdi.abbey.ui.fragments.mainactivity.library.LibraryFragment
import com.paolovalerdi.abbey.ui.viewmodel.LibraryViewModel
import com.paolovalerdi.abbey.util.extensions.isCollapsed
import com.paolovalerdi.abbey.util.extensions.isLight
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.android.synthetic.main.sliding_music_panel_layout.*
import java.util.*

class MainActivity : AbsSlidingMusicPanelActivity() {

    companion object {

        const val EXPAND_PLAYER = "expand_player_from_notification"
        const val LIBRARY = 0
        const val FOLDER = 1

    }

    lateinit var mainViewModel: LibraryViewModel

    private var blockRequestPermissions = false
    private lateinit var currentFragment: MainActivityFragmentCallbacks
    private lateinit var bottomNavigationSheet: BottomNavigationDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightStatusBar(resolveAttrColor(R.attr.colorSurface).isLight)
        setNavigationBarColor(resolveAttrColor(R.attr.colorSurfaceElevated))

        mainViewModel = ViewModelProviders.of(this).get(LibraryViewModel::class.java)
        addMusicServiceEventListener(mainViewModel)

        bottomNavigationSheet = BottomNavigationDialog.create()

        if (savedInstanceState == null) {
            setFragment(PreferenceUtil.lastMusicChooser)
        } else {
            restoreCurrentFragment()
        }

        showChangelog()
        if (intent.hasExtra(EXPAND_PLAYER) and PreferenceUtil.loadGenres) {
            // Smooth expanding
            Handler().postDelayed({ slidingLayout.isCollapsed = false }, 400)
        }
    }

    override fun createContentView(): View = wrapInSlidingMusicPanel(R.layout.activity_main_layout)

    override fun onServiceConnected() {
        super.onServiceConnected()
        handlePlayBackIntent(intent)
    }

    override fun requestPermissions() {
        if (blockRequestPermissions.not()) super.requestPermissions()
    }

    override fun handleBackPress(): Boolean = super.handleBackPress() or currentFragment.handleBackPress()

    fun setMusicChooser(key: Int) {
        setFragment(key)
    }

    fun showBottomNavigation() {
        if (bottomNavigationSheet.isAdded.not()) {
            bottomNavigationSheet.show(supportFragmentManager, null)
        }
    }

    fun reloadUserImage() {
        currentFragment?.reloadUserImage()
    }

    private fun setFragment(lastMusicChooser: Int) {
        PreferenceUtil.lastMusicChooser = lastMusicChooser
        when (lastMusicChooser) {
            FOLDER -> {
                setCurrentFragment(FoldersFragment.newInstance())
            }
            LIBRARY -> {
                setCurrentFragment(LibraryFragment.newInstance())
            }
        }
    }

    private fun showChangelog() {
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            val currentVersion = PackageInfoCompat.getLongVersionCode(pInfo).toInt()
            if (currentVersion != PreferenceUtil.lastChangelogVersion) {
                ChangelogDialog.create().show(supportFragmentManager, "CHANGE_LOG_DIALOG")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    // TODO: Searching should be handled in MusicService like Android Auto
    private fun handlePlayBackIntent(intent: Intent?) {
        if (intent == null) {
            return
        }

        val uri = intent.data
        val mimeType = intent.type
        var handled = false

        intent.action?.let { action ->
            if (action == MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH) {
                val songs = SearchQueryHelper.getSongs(this@MainActivity, intent.extras!!)
                if (MusicPlayerRemote.getShuffleMode() == SHUFFLE_MODE_SHUFFLE) {
                    MusicPlayerRemote.openAndShuffleQueue(songs, true)
                } else {
                    MusicPlayerRemote.openQueue(songs, 0, true)
                }
                handled = true
            }
        }

        uri?.let { mUri ->
            val path = mUri.path ?: mUri.toString()
            if (path.isNotEmpty()) {
                MusicPlayerRemote.playFromUri(mUri)
                handled = true
            }
        }

        when (mimeType) {
            MediaStore.Audio.Playlists.CONTENT_TYPE -> {
                val id = parseIdFromIntent(intent, "playlistId", "playlist").toInt()
                if (id >= 0) {
                    val position = intent.getIntExtra("position", 0)
                    MusicPlayerRemote.openQueue(PlaylistSongRepository.getPlaylistSongList(this, id) as ArrayList<Song>, position, true)
                    handled = true
                }
            }
            MediaStore.Audio.Albums.CONTENT_TYPE -> {
                val id = parseIdFromIntent(intent, "albumId", "album").toInt()
                if (id >= 0) {
                    val position = intent.getIntExtra("position", 0)
                    MusicPlayerRemote.openQueue(AlbumRepository.getAlbum(this, id).songs, position, true)
                    handled = true
                }
            }
            MediaStore.Audio.Artists.CONTENT_TYPE -> {
                val id = parseIdFromIntent(intent, "artistId", "artist").toInt()
                if (id >= 0) {
                    val position = intent.getIntExtra("position", 0)
                    MusicPlayerRemote.openQueue(ArtistRepository.getArtist(this, id).songs, position, true)
                    handled = true
                }
            }
        }

        if (handled) {
            setIntent(Intent())
        }

    }

    private fun parseIdFromIntent(intent: Intent, longKey: String, stringKey: String): Long {
        var id = intent.getLongExtra(longKey, -1)
        if (id < 0) {
            val idString = intent.getStringExtra(stringKey)
            if (idString != null) {
                try {
                    id = java.lang.Long.parseLong(idString)
                } catch (e: NumberFormatException) {
                    Log.e("MainActivity", e.message)
                }
            }
        }
        return id
    }


    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.transaction {
            setCustomAnimations(
                R.anim.ds_grow_fade_in_center,
                R.anim.ds_shrink_fade_out_center,
                R.anim.ds_grow_fade_in_center,
                R.anim.ds_shrink_fade_out_center
            )
            replace(R.id.contentContainer, fragment, null)
        }
        currentFragment = fragment as MainActivityFragmentCallbacks
    }

    private fun restoreCurrentFragment() {
        currentFragment = supportFragmentManager.findFragmentById(R.id.contentContainer) as MainActivityFragmentCallbacks
    }

    interface MainActivityFragmentCallbacks {

        fun handleBackPress(): Boolean

        fun reloadUserImage()

    }
}
