package com.paolovalerdi.abbey.adapter.base

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.views.IconImageView
import de.hdodenhof.circleimageview.CircleImageView

open class MediaEntryViewHolde(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val itemContainer: ConstraintLayout? = itemView.findViewById(R.id.item_container)
    val cardContainer: MaterialCardView? = itemView.findViewById(R.id.card_view)
    val dummySpace: View? = itemView.findViewById(R.id.dummy_space)

    val albumCover: ImageView? = itemView.findViewById(R.id.image)
    val artistImage: CircleImageView? = itemView.findViewById(R.id.circle_image)
    val imageText: TextView? = itemView.findViewById(R.id.image_text)

    val title: TextView? = itemView.findViewById(R.id.title)
    val subtitle: TextView? = itemView.findViewById(R.id.text)

    val overFlowIcon: AppCompatImageView? = itemView.findViewById(R.id.menu)
    val dragIcon: IconImageView? = itemView.findViewById(R.id.drag_view)
    val divider: View? = itemView.findViewById(R.id.divider)

    val shuffleButton: AppCompatImageView? = itemView.findViewById(R.id.shuffleButton)
    val sortOrderButton: AppCompatImageView? = itemView.findViewById(R.id.sortOrderIcon)
    val gridSizeButton: AppCompatImageView? = itemView.findViewById(R.id.gridSizeButton)

    val icon: ImageView? = itemView.findViewById(R.id.cardIcon)
    val footer: View? = itemView.findViewById(R.id.footer)
}