package com.paolovalerdi.abbey.ui.activities.detail

import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyTouchHelper
import com.kabouzeid.appthemehelper.ThemeStore
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.kroegerama.imgpicker.ButtonType
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.adapter.epoxy.SongDetailModel
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.AbbeyMediaColoredTarget
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteWrapper
import com.paolovalerdi.abbey.helper.menu.PlaylistMenuHelper
import com.paolovalerdi.abbey.model.Playlist
import com.paolovalerdi.abbey.model.smartplaylist.AbsSmartPlaylist
import com.paolovalerdi.abbey.repository.PlaylistSongRepository
import com.paolovalerdi.abbey.util.MusicUtil
import com.paolovalerdi.abbey.util.PlaylistsUtil
import com.paolovalerdi.abbey.util.cimages.PlaylistImageUtil
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.android.synthetic.main.activity_media_detail.*

/**
 * @author Paolo Valerdi
 */
class PlaylistDetailActivity : MediaDetailsActivity<Playlist>(), BottomSheetImagePicker.OnImagesSelectedListener {

    override var backgroundColor: Int = 0
        set(value) {
            field = value
            bindController()
        }

    override fun loadMediaDetails(model: Playlist) {
        setMediaTitle(model.name)
        if (model is AbsSmartPlaylist) {
            detailsText.text = model.getInfoString(this)
        } else {
            detailsText.text = model.getInfoString(this)
        }
        GlideApp.with(this)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getPlaylistModel(model))
            .transition(AbbeyGlideExtension.getDefaultTransition<BitmapPaletteWrapper>())
            .playlistOptions(model)
            .into(object : AbbeyMediaColoredTarget(detailsImage) {

                override fun onColorsReady(background: Int, accent: Int) {
                    if (PreferenceUtil.coloredMediaDetails) setColors(background, accent)
                }

            })
        setSwipeAndDrag(controller)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_playlist_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_shuffle_playlist -> {
                BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                    .cameraButton(ButtonType.None)
                    .galleryButton(ButtonType.Tile)
                    .navigationBarColor(ThemeStore.primaryColorDark(this))
                    .requestTag("single")
                    .show(supportFragmentManager)
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return PlaylistMenuHelper.handleMenuClick(this, mediaItem, item!!)
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        PlaylistImageUtil.getInstance().setImageAndSave(mediaItem, uris.first())
    }

    override fun setSwipeAndDrag(controller: EpoxyController?) {
        if (mediaItem !is AbsSmartPlaylist) {
            controller?.let {
                val modelClass = SongDetailModel::class.java
                EpoxyTouchHelper.initDragging(it)
                    .withRecyclerView(detailsRecyclerView)
                    .forVerticalList()
                    .withTarget(modelClass)
                    .andCallbacks(object : EpoxyTouchHelper.DragCallbacks<SongDetailModel>() {
                        override fun onModelMoved(
                            fromPosition: Int,
                            toPosition: Int,
                            modelBeingMoved: SongDetailModel?,
                            itemView: View?
                        ) {
                            PlaylistsUtil.moveItem(this@PlaylistDetailActivity, mediaItem.id, fromPosition, toPosition)
                            onMediaStoreChanged()
                        }
                    })

                EpoxyTouchHelper.initSwiping(detailsRecyclerView)
                    .leftAndRight()
                    .withTarget(modelClass)
                    .andCallbacks(object : EpoxyTouchHelper.SwipeCallbacks<SongDetailModel>() {
                        override fun onSwipeCompleted(
                            model: SongDetailModel?,
                            itemView: View?,
                            position: Int,
                            direction: Int
                        ) {
                            val song = content.songs[position]
                            PlaylistsUtil.removeFromPlaylist(this@PlaylistDetailActivity, song, mediaItem.id)
                            onMediaStoreChanged()
                        }
                    })
            }
        }
    }

}