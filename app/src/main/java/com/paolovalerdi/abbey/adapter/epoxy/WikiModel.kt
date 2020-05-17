package com.paolovalerdi.abbey.adapter.epoxy

import android.text.Spanned
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.util.extensions.secondaryTextColorFor
import java.lang.Integer.MAX_VALUE

@EpoxyModelClass(layout = R.layout.item_wiki)
abstract class WikiModel : EpoxyModelWithHolder<WikiModel.WikiViewHolder>() {

    @EpoxyAttribute
    var content: Spanned? = null

    @EpoxyAttribute
    var backgroundColor = 0

    override fun bind(holder: WikiViewHolder) {
        holder.body.text = content
        holder.body.secondaryTextColorFor(backgroundColor)
        holder.itemView.setOnClickListener {
            val maxLines = if (holder.body.maxLines == 4) MAX_VALUE else 4
            holder.body.maxLines = maxLines
        }
    }

    override fun unbind(holder: WikiViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

    class WikiViewHolder : KotlinEpoxyHolder() {
        val body by bind<AppCompatTextView>(R.id.text)
    }

}