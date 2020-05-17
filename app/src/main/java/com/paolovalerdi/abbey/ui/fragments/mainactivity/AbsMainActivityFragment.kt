package com.paolovalerdi.abbey.ui.fragments.mainactivity

import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.ui.activities.MainActivity
import com.paolovalerdi.abbey.ui.fragments.AbsCoroutineFragment
import com.paolovalerdi.abbey.ui.viewmodel.LibraryViewModel
import de.hdodenhof.circleimageview.CircleImageView

/**
 * @author Paolo Valerdi
 */
abstract class AbsMainActivityFragment : AbsCoroutineFragment() {

    val viewModel: LibraryViewModel
        get() = mainActivity.mainViewModel

    val mainActivity: MainActivity
        get() = requireActivity() as MainActivity

    protected fun loadUserImageInto(imageView: CircleImageView) {
        GlideApp.with(this)
            .load(AbbeyGlideExtension.getUserModel())
            .userOptions()
            .into(imageView)
    }

}
