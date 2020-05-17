package com.paolovalerdi.abbey.ui.fragments.player.material

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.updateLayoutParams
import com.kabouzeid.appthemehelper.ThemeStore
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.ui.dialogs.LyricsDialog
import com.paolovalerdi.abbey.ui.activities.base.AbsSlidingMusicPanelActivity
import com.paolovalerdi.abbey.ui.fragments.player.base.AbsPlayerFragment
import com.paolovalerdi.abbey.ui.fragments.player.base.PagedAlbumCoverFragment
import com.paolovalerdi.abbey.util.extensions.convertDpToPixels
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.extensions.screenWidth
import kotlinx.android.synthetic.main.fragment_material_player.*
import kotlinx.android.synthetic.main.fragment_material_player_playback_controls.*

/**
 * @author Paolo Valerdi
 */
class MaterialPlayerFragment : AbsPlayerFragment<MaterialPlayerPlaybackControlsFragment, PagedAlbumCoverFragment>() {

    @ColorInt
    private var colorControlNormal: Int = -1

    override val layoutRes: Int = R.layout.fragment_material_player

    override var lastColor: Int = -1

    override val navigationBarColor: Int
        get() = requireContext().resolveAttrColor(R.attr.colorSurface)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colorControlNormal = requireContext().resolveAttrColor(R.attr.colorControlNormal)
        playbackControls.favoriteIcon?.setOnClickListener { toggleFavorite() }
        playbackControls.moreIcon?.setOnClickListener { LyricsDialog.create(lyrics).show(childFragmentManager, null) }
        playerOptions?.setOnClickListener {
            PopupMenu(requireContext(), it).apply {
                gravity = Gravity.END
                inflate(R.menu.menu_player_no_toolbar)
                setOnMenuItemClickListener {
                    onMenuItemClick(it)
                }
            }.show()
        }
    }

    override fun onFavoriteUpdated(drawable: Drawable) {
        drawable.mutate().setTint(colorControlNormal)
        playbackControls.favoriteIcon?.setImageDrawable(drawable)
    }

    override fun onPanelSlide(p0: View?, offset: Float) {
        playerArrow.rotation = (180 * offset)
        playerOptions?.alpha = offset
    }

    override fun getPaletteColor(): Int = ThemeStore.primaryColor(activity as AppCompatActivity)

    override fun setUpPanelHeight() {

        val panelHeight: Int = ((resources.convertDpToPixels(48f) + navigationBarHeight).toInt())
        playerSlidingPanelLayout.panelHeight = panelHeight

        card_content.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = navigationBarHeight
        }

        val playbackControlsHeight = playerSlidingPanelLayout.height - (requireActivity().screenWidth() + panelHeight)
        playbackControls.view?.updateLayoutParams {
            height = playbackControlsHeight
        }

        playerSlidingPanelLayout.setAntiDragView(draggable_area)
        (activity as AbsSlidingMusicPanelActivity).setAntiDragView(playerSlidingPanelLayout.findViewById(R.id.player_panel))
    }

}
