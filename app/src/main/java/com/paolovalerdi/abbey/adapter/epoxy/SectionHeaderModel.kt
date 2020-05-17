package com.paolovalerdi.abbey.adapter.epoxy

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.paolovalerdi.abbey.R

@EpoxyModelClass(layout = R.layout.item_header_text)
abstract class SectionHeaderModel : EpoxyModelWithHolder<SectionHeaderModel.TopArtistViewHolder>() {

    @EpoxyAttribute
    var title: String? = null

    @EpoxyAttribute
    var description: String? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: View.OnClickListener? = null

    override fun bind(holder: TopArtistViewHolder) {
        if (title == null) holder.sectionTitle.isGone = true
        if (description == null) holder.sectionDescription.isGone = true
        holder.itemView.setOnClickListener(clickListener)
        holder.sectionTitle.text = title
        holder.sectionDescription.text = description
    }

    override fun unbind(holder: TopArtistViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

    class TopArtistViewHolder : KotlinEpoxyHolder() {
        val sectionTitle by bind<AppCompatTextView>(R.id.title)
        val sectionDescription by bind<AppCompatTextView>(R.id.text)
    }

}