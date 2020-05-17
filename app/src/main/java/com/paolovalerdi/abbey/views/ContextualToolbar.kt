package com.paolovalerdi.abbey.views

import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialcab.MaterialCab
import com.kabouzeid.appthemehelper.util.MaterialValueHelper
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper
import com.paolovalerdi.abbey.util.extensions.isLight

/**
 * @author Paolo Valerdi
 */
class ContextualToolbar(context: AppCompatActivity, attacherId: Int) : MaterialCab(context, attacherId) {

    var mContext: AppCompatActivity = context

    override fun setBackgroundColor(color: Int): MaterialCab {
        toolbar?.apply {
            val textColor = MaterialValueHelper.getPrimaryTextColor(mContext, color.isLight)
            val contentColor = MaterialValueHelper.getSecondaryTextColor(mContext, color.isLight)
            ToolbarContentTintHelper.setToolbarContentColor(mContext, this, contentColor, contentColor, contentColor, contentColor)
            setTitleTextColor(textColor)
        }
        return super.setBackgroundColor(color)
    }

}
