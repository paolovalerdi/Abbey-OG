package com.paolovalerdi.abbey.ui.dialogs

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.transition.Transition
import com.kabouzeid.appthemehelper.ThemeStore
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.kroegerama.imgpicker.ButtonType
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.AbbeySimpleTarget
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.ui.activities.MainActivity
import com.paolovalerdi.abbey.util.ImageUtil
import com.paolovalerdi.abbey.util.extensions.applyAccentColor
import com.paolovalerdi.abbey.util.extensions.makeToast
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.android.synthetic.main.dialog_user_image_chooser.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @author Paolo Valerdi
 */
class UserImageDialog : RoundedBottomSheetDialog(), BottomSheetImagePicker.OnImagesSelectedListener {

    companion object {

        fun create(): UserImageDialog = UserImageDialog()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.dialog_user_image_chooser,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        loadUserImage()
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        val uri = uris.firstOrNull()
        uri?.let { saveImage(it) }
    }

    private fun saveImage(uri: Uri) {
        GlideApp.with(this)
            .asBitmap()
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .override(300, 300)
            .into(object : AbbeySimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    launch(IO) {
                        val appDir = requireContext().applicationContext.filesDir
                        val file = File(appDir, "profile.jpg")
                        var succesful = false
                        try {
                            val os = BufferedOutputStream(FileOutputStream(file))
                            succesful = ImageUtil.resizeBitmap(resource, 2048).compress(Bitmap.CompressFormat.WEBP, 100, os)
                            os.close()
                        } catch (e: IOException) {
                            requireContext().makeToast("Something went wrong")
                            e.printStackTrace()
                        }
                        if (succesful) {
                            withContext(Main) {
                                PreferenceUtil.userImageSignature = System.currentTimeMillis()
                                (requireActivity() as MainActivity).reloadUserImage()
                                loadUserImage()
                            }
                        }
                    }
                }
            })
    }

    private fun loadUserImage() {
        GlideApp.with(this)
            .load(AbbeyGlideExtension.getUserModel())
            .userOptions()
            .into(dialogUserImage)
    }

    private fun setUpViews() {
        dialogUserImage.setOnClickListener {
            BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                .cameraButton(ButtonType.None)
                .galleryButton(ButtonType.Button)
                .navigationBarColor(requireContext().resolveAttrColor(R.attr.colorBackgroundFloating))
                .show(childFragmentManager)
        }

        PreferenceUtil.userName?.let { dialogUserTextInput.setText(it) }

        dialogUserTextInput.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        dialogUserTextInput.setHintTextColor(ThemeStore.textColorSecondary(activity!!))
        dialogUserTextInput.setTextColor(ThemeStore.textColorPrimary(activity!!))

        dialogUserButton.applyAccentColor()

        dialogUserButton.setOnClickListener {
            val userName = dialogUserTextInput.text.toString().trim()
            if (userName.isNotEmpty()) {
                PreferenceUtil.userName = userName
                dismiss()
            } else {
                requireContext().makeToast("Please enter your name")
            }
        }
    }

}