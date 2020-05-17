package com.paolovalerdi.abbey.adapter.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.model.Song
import com.paolovalerdi.abbey.ui.fragments.player.NowPlayingScreen
import com.paolovalerdi.abbey.util.extensions.dpToPx
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import com.paolovalerdi.abbey.views.ColoredShadowImageView

/**
 * @author Paolo Valerdi
 * TODO: Color extraction should be handling here.
 *       I tried a couple of ways but none was working as expected.
 */
class NowPlayingCoverAdapter(private var queue: List<Song> = emptyList()) : RecyclerView.Adapter<NowPlayingCoverAdapter.CoverViewHolder>() {

    @LayoutRes
    private val res: Int

    private val isBlurTheme = PreferenceUtil.nowPlayingScreen == NowPlayingScreen.BLUR

    init {
        setHasStableIds(true)
        res = if (PreferenceUtil.nowPlayingScreen == NowPlayingScreen.MATERIAL) R.layout.fragment_album_cover_shadow else R.layout.fragment_album_cover
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CoverViewHolder = CoverViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(
                res,
                parent,
                false
            )
    )

    override fun getItemId(position: Int): Long = queue[position].id.toLong()

    override fun getItemCount(): Int = queue.size

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        val song = queue[position]

        val image = (holder.image ?: holder.imageShadow) ?: return

        when {
            image is ColoredShadowImageView -> {
                image.setPadding(holder.itemView.dpToPx(PreferenceUtil.imagePadding.toFloat()))
            }
            isBlurTheme -> {
                holder.container?.radius = holder.itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_medium).toFloat()
                holder.container?.elevation = 4f
            }
        }

        GlideApp.with(holder.itemView.context)
            .load(AbbeyGlideExtension.getSongModel(song))
            .songOptions(song)
            .roundedCorners(image is ColoredShadowImageView, holder.itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_medium))
            .transition(AbbeyGlideExtension.getDefaultTransition())
            .into(image)
    }

    fun updateQueue(newQueue: List<Song>) {
        queue = newQueue
        notifyDataSetChanged()
    }

    class CoverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val container: MaterialCardView? = itemView.findViewById(R.id.player_image_container)
        val image: AppCompatImageView? = itemView.findViewById(R.id.player_image)
        val imageShadow: ColoredShadowImageView? = itemView.findViewById(R.id.player_image_shadow)

    }

}