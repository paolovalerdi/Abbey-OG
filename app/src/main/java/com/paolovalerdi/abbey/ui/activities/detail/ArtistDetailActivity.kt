package com.paolovalerdi.abbey.ui.activities.detail

import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import com.kabouzeid.appthemehelper.ThemeStore
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.kroegerama.imgpicker.ButtonType
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.ui.dialogs.AddToPlaylistSheet
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.AbbeyMediaColoredTarget
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.helper.MusicPlayerRemote
import com.paolovalerdi.abbey.model.Artist
import com.paolovalerdi.abbey.util.CustomArtistImageUtil
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.extensions.makeToast
import kotlinx.android.synthetic.main.activity_media_detail.*

class ArtistDetailActivity : MediaDetailsActivity<Artist>(), BottomSheetImagePicker.OnImagesSelectedListener {

    override var backgroundColor: Int = 0
        set(value) {
            field = value
            bindController()
        }

    override fun loadMediaDetails(model: Artist) {
        setMediaTitle(model.name)
        detailsText.text = MusicUtil.getArtistInfoString(this, model)
        GlideApp.with(this)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getArtistModel(model))
            .artistOptions(model)
            .into(object : AbbeyMediaColoredTarget(detailsImage) {
                override fun onColorsReady(background: Int, accent: Int) {
                    setColors(background, accent)
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_artist_detail, menu)
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
            R.id.action_set_artist_image -> {
                BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                    .cameraButton(ButtonType.None)
                    .navigationBarColor(ThemeStore.primaryColorDark(this))
                    .galleryButton(ButtonType.Button)
                    .requestTag("single")
                    .show(supportFragmentManager)
                return true
            }
            R.id.action_reset_artist_image -> {
                makeToast(resources.getString(R.string.updating))
                CustomArtistImageUtil.getInstance(this).resetCustomArtistImage(mediaItem)
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        CustomArtistImageUtil.getInstance(this).setCustomArtistImage(mediaItem, uris.first())
    }

}