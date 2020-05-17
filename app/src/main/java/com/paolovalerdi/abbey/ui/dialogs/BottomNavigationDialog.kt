package com.paolovalerdi.abbey.ui.dialogs

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.kabouzeid.appthemehelper.util.NavigationViewUtil
import com.kabouzeid.appthemehelper.util.TintHelper
import com.paolovalerdi.abbey.App
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension
import com.paolovalerdi.abbey.glide.GlideApp
import com.paolovalerdi.abbey.ui.activities.MainActivity
import com.paolovalerdi.abbey.ui.activities.MainActivity.Companion.FOLDER
import com.paolovalerdi.abbey.ui.activities.preferences.PreferencesActivity
import com.paolovalerdi.abbey.util.extensions.primaryTextColorFor
import com.paolovalerdi.abbey.util.extensions.resolveAttrColor
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_bottom_navigation.*
import java.util.*
import java.util.Calendar.HOUR_OF_DAY

/**
 * @author Paolo Valerdi
 */
class BottomNavigationDialog : RoundedBottomSheetDialog() {

    companion object {

        fun create(): BottomNavigationDialog = BottomNavigationDialog()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is MainActivity) {
            throw RuntimeException("Must be attached to ${MainActivity::javaClass.name}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_bottom_navigation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMenu()
        initHeader()
        setUpSupportButton()
    }

    private fun initMenu() {
        val normalColor = requireContext().resolveAttrColor(R.attr.colorControlNormal)
        val selectedColor = ThemeStore.accentColor(requireContext())
        NavigationViewUtil.setItemIconColors(mainNavigationView, normalColor, selectedColor)
        NavigationViewUtil.setItemTextColors(mainNavigationView, requireContext().resolveAttrColor(android.R.attr.textColorPrimary), selectedColor)
        mainNavigationView.setCheckedItem(getSelectedItem(PreferenceUtil.lastMusicChooser))
        val iconSl = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_checked),
                intArrayOf()
            ),
            intArrayOf(
                ColorUtil.withAlpha(selectedColor, 0.10f),
                ColorUtil.withAlpha(selectedColor, 0.20f),
                Color.TRANSPARENT
            ))
        val drawable = TintHelper.createTintedDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.navigation_view_item_background), iconSl)
        mainNavigationView.itemBackground = drawable
        mainNavigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_library -> {
                    if (PreferenceUtil.lastMusicChooser != MainActivity.LIBRARY) {
                        mainNavigationView.setCheckedItem(item.itemId)
                        setMusicChooser(MainActivity.LIBRARY)
                    }
                }
                R.id.action_folders -> {
                    if (PreferenceUtil.lastMusicChooser != FOLDER) {
                        mainNavigationView.setCheckedItem(item.itemId)
                        setMusicChooser(FOLDER)
                    }
                }
                R.id.action_settings -> startActivity(Intent(requireActivity(), PreferencesActivity::class.java))
            }
            dismiss()
            true
        }
    }

    private fun initHeader() {
      val headerView = mainNavigationView.getHeaderView(0)
        val userImageView = headerView.findViewById<CircleImageView>(R.id.userImage)
        val userName = headerView.findViewById<AppCompatTextView>(R.id.userName)
        val us = PreferenceUtil.userName ?: ""
        val userGreetings: String = when (Calendar.getInstance().get(HOUR_OF_DAY)) {
            in 1..3 -> getString(R.string.still_awake)
            in 6..11 -> String.format(getString(R.string.good_morning_x), us)
            in 12..17 -> String.format(getString(R.string.good_after_noon_x), us)
            in 18..19 -> String.format(getString(R.string.good_evening_x), us)
            else -> String.format(getString(R.string.good_night_x), us)
        }
        userName.text = userGreetings
        GlideApp.with(this)
            .load(AbbeyGlideExtension.getUserModel())
            .userOptions()
            .into(userImageView)
        userImageView.setOnClickListener {
            UserImageDialog.create().show(fragmentManager!!, null)
            dismiss()
        }
    }

    private fun setMusicChooser(key: Int) {
        (requireActivity() as MainActivity).setMusicChooser(key)
    }

    private fun setUpSupportButton() {
        if (!App.isProVersion) {
            val accentColor = ThemeStore.accentColor(requireContext())
            supportDevelopmentButton.apply {
                setBackgroundColor(accentColor)
                primaryTextColorFor(accentColor)
                setOnClickListener {
                    PurchaseDialog.newInstance().show(fragmentManager!!, null)
                    dismiss()
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setNavigationBarColor(accentColor)
            }
        } else {
            supportDevelopmentButton.isVisible = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setNavigationBarColor(requireContext().resolveAttrColor(R.attr.colorBackgroundFloating))
            }
        }

    }

    private fun getSelectedItem(lastMusicChooser: Int): Int = when (lastMusicChooser) {
        FOLDER -> {
            R.id.action_folders
        }
        else -> {
            R.id.action_library
        }
    }

}
