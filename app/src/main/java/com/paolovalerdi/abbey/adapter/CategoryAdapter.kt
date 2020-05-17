package com.paolovalerdi.abbey.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.model.CategoryInfo
import com.paolovalerdi.abbey.util.extensions.makeToast
import com.thesurix.gesturerecycler.GestureAdapter
import com.thesurix.gesturerecycler.GestureViewHolder

class CategoryAdapter : GestureAdapter<CategoryInfo, CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(
                R.layout.preference_dialog_library_categories_listitem,
                parent,
                false
            )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val categoryInfo = data[position]
        holder.checkBox?.isChecked = categoryInfo.visible
        holder.title?.text = holder.itemView.resources.getString(categoryInfo.category.stringRes)
        holder.itemView.setOnClickListener {
            when (canAddCategories(categoryInfo)) {
                CategoryResult.OK -> {
                    categoryInfo.visible = !categoryInfo.visible
                    holder.checkBox.isChecked = categoryInfo.visible
                }
                CategoryResult.ONE -> {
                    holder.itemView.context.makeToast(R.string.you_have_to_select_at_least_one_category)

                }
                CategoryResult.FIVE -> {
                    holder.itemView.context.makeToast("You can only select five categories")
                }
            }
        }
    }

    private fun canAddCategories(categoryInfo: CategoryInfo): CategoryResult {
        if (categoryInfo.visible) {
            var counter = 1
            data.forEach { c ->
                if (c.visible) counter++
            }
            return if (counter > 2) CategoryResult.OK else CategoryResult.ONE
        } else {
            var counter = 0
            data.forEach { c ->
                if (c.visible) counter++
            }
            return if (counter < 5) CategoryResult.OK else CategoryResult.FIVE
        }
    }

    private enum class CategoryResult {
        OK, ONE, FIVE
    }

    inner class ViewHolder(itemView: View) : GestureViewHolder(itemView) {

        val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox)
        val title = itemView.findViewById<AppCompatTextView>(R.id.title)
        val dragView = itemView.findViewById<AppCompatImageView>(R.id.drag_view)

        override fun canDrag() = true

        override fun canSwipe() = false

        override val draggableView: View? = dragView

    }

}
