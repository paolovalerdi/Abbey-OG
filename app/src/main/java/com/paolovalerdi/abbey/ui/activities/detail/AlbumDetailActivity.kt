package com.paolovalerdi.abbey.ui.activities.detail

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.ui.dialogs.AddToPlaylistSheet
import com.paolovalerdi.abbey.ui.dialogs.DeleteSongsDialog
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.AbbeyMediaColoredTarget
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.model.Album
import com.paolovalerdi.abbey.ui.activities.tageditor.AbsTagEditorActivity
import com.paolovalerdi.abbey.ui.activities.tageditor.AlbumTagEditorActivity
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.NavigationUtil
import kotlinx.android.synthetic.main.activity_media_detail.*

class AlbumDetailActivity : MediaDetailsActivity<Album>() {

    override fun loadMediaDetails(model: Album) {
        setMediaTitle(model.title)
        val duration = MusicUtil.buildInfoString(MusicUtil.getYearString(model.year), MusicUtil.getReadableDurationString(MusicUtil.getTotalDuration(this, model.songs)))
        val details = MusicUtil.buildInfoString(MusicUtil.getAlbumInfoString(this, model), duration)
        detailsText.text = details
        GlideApp.with(this)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getSongModel(model.safeGetFirstSong()))
            .songOptions(model.safeGetFirstSong())
            .into(object : AbbeyMediaColoredTarget(detailsImage) {
                override fun onColorsReady(background: Int, accent: Int) {
                    setColors(background, accent)
                }
            })
    }

    override var backgroundColor: Int = 0
        set(value) {
            field = value
            bindController()
        }

    companion object {

        const val TAG_EDITOR_REQUEST = 2001

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_album_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_play_next -> {
                MusicPlayerRemote.playNext(mediaItem.songs)
                return true
            }
            R.id.action_add_to_current_playing -> {
                MusicPlayerRemote.enqueue(mediaItem.songs)
                return true
            }
            R.id.action_add_to_playlist -> {
                AddToPlaylistSheet.create(mediaItem.songs).show(supportFragmentManager, "ADD_PLAYLIST")
                return true
            }
            R.id.action_go_to_artist -> {
                NavigationUtil.goToArtist(this, mediaItem.artistId)
                return true
            }
            R.id.action_tag_editor -> {
                val intent = Intent(this, AlbumTagEditorActivity::class.java).apply {
                    putExtra(AbsTagEditorActivity.EXTRA_ID, mediaItem.id)
                }
                startActivityForResult(intent, TAG_EDITOR_REQUEST)
                return true
            }
            R.id.action_delete_from_device -> {
                DeleteSongsDialog.create(mediaItem.songs).show(supportFragmentManager, null)
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAG_EDITOR_REQUEST) {
            // loadContent()
            setResult(Activity.RESULT_OK)
        }
    }

}