package com.paolovalerdi.abbey.ui.fragments.player.base

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.AbbeyNowPlayingColoredTarget
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.util.extensions.convertDpToPixels
import com.paolovalerdi.abbey.util.preferences.MATERIAL_NOW_PLAYING_PADDING
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.android.synthetic.main.fragment_player_album_cover_static.*

/**
 * @author Paolo Valerdi
 */
class MaterialAlbumCoverFragment : AbsPlayerAlbumCoverFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val cornerRadius: Int
        get() = resources.getDimensionPixelSize(R.dimen.corner_radius_medium)

    override val layoutRes: Int = R.layout.fragment_player_album_cover_static

    override fun onPlayingQueueChange(newPlayingQueue: List<Song>) {}

    override fun onCurrentQueuePostionChange(newPosition: Int) {}

    override fun loadColor(song: Song) {
        GlideApp.with(this)
            .asBitmapPalette()
            .load(AbbeyGlideExtension.getSongModel(song))
            .songOptions(song)
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .roundedCorners(true, cornerRadius)
            .into(object : AbbeyNowPlayingColoredTarget(image) {

                override fun onColorsReady(color: Int, topIsLight: Boolean) {
                    nowPlayingColorCallback?.onColorChanged(color, topIsLight)
                }

            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateMargins()
        PreferenceUtil.registerOnSharedPreferenceChangedListener(this)
    }

    override fun onDestroyView() {
        PreferenceUtil.unregisterOnSharedPreferenceChangedListener(this)
        super.onDestroyView()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == MATERIAL_NOW_PLAYING_PADDING) {
            updateMargins()
        }
    }

    private fun updateMargins() {
        val padding = resources.convertDpToPixels(PreferenceUtil.imagePadding.toFloat())
        image.setPadding(padding.toInt())
        playerAlbumLyricsContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(padding.toInt())
        }
    }

}