package com.paolovalerdi.abbey.helper.menu

import android.content.Context
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.helper.SortOrder
import com.paolovalerdi.abbey.util.extensions.isLandscape

/**
 * @author Paolo Valerdi
 */
object LibraryMenuHelper {

    fun songSortOderMenu(view: View, block: (sortOrder: String) -> Unit) {
        val popupMenu = popupMenu {
            style = getPopUpTheme(view.context)
            dropdownGravity = Gravity.END
            section {
                title = view.resources.getString(R.string.action_sort_order)
                item {
                    label = view.resources.getString(R.string.sort_order_a_z)
                    callback = {
                        block(SortOrder.SongSortOrder.SONG_A_Z)
                    }
                }
                item {
                    label = view.resources.getString(R.string.sort_order_z_a)
                    callback = {
                        block(SortOrder.SongSortOrder.SONG_Z_A)
                    }
                }
                item {
                    label = view.resources.getString(R.string.sort_order_artist)
                    callback = {
                        block(SortOrder.SongSortOrder.SONG_ARTIST)
                    }
                }
                item {
                    label = view.resources.getString(R.string.sort_order_album)
                    callback = {
                        block(SortOrder.SongSortOrder.SONG_ALBUM)
                    }
                }
                item {
                    label = view.resources.getString(R.string.last_added)
                    callback = {
                        block(SortOrder.SongSortOrder.SONG_DATE_ADDED)
                    }
                }
                item {
                    label = view.resources.getString(R.string.sort_order_year)
                    callback = {
                        block(SortOrder.SongSortOrder.SONG_YEAR)
                    }
                }
            }
        }
        popupMenu.show(view.context, view)
    }

    fun albumSortOderMenu(view: View, block: (sortOrder: String) -> Unit) {
        val popupMenu = popupMenu {
            style = getPopUpTheme(view.context)
            dropdownGravity = Gravity.END
            section {
                title = view.resources.getString(R.string.action_sort_order)
                item {
                    label = view.resources.getString(R.string.sort_order_a_z)
                    callback = {
                        block(SortOrder.AlbumSortOrder.ALBUM_A_Z)
                    }
                }
                item {
                    label = view.resources.getString(R.string.sort_order_z_a)
                    callback = {
                        block(SortOrder.AlbumSortOrder.ALBUM_Z_A)
                    }
                }
                item {
                    label = view.resources.getString(R.string.sort_order_artist)
                    callback = {
                        block(SortOrder.AlbumSortOrder.ALBUM_ARTIST)
                    }
                }
                item {
                    label = view.resources.getString(R.string.last_added)
                    callback = {
                        block(SortOrder.AlbumSortOrder.ALBUM_DATE_ADDED)
                    }
                }
                item {
                    label = view.resources.getString(R.string.sort_order_year)
                    callback = {
                        block(SortOrder.AlbumSortOrder.ALBUM_YEAR)
                    }
                }
            }
        }
        popupMenu.show(view.context, view)
    }

    fun artistSortOderMenu(view: View, block: (sortOrder: String) -> Unit) {
        val popupMenu = popupMenu {
            style = getPopUpTheme(view.context)
            dropdownGravity = Gravity.END
            section {
                title = view.resources.getString(R.string.action_sort_order)
                item {
                    label = view.resources.getString(R.string.sort_order_a_z)
                    callback = {
                        block(SortOrder.ArtistSortOrder.ARTIST_A_Z)
                    }
                }
                item {
                    label = view.resources.getString(R.string.sort_order_z_a)
                    callback = {
                        block(SortOrder.ArtistSortOrder.ARTIST_Z_A)
                    }
                }
            }
        }
        popupMenu.show(view.context, view)
    }

    fun gridSize(
        view: View,
        currentGridSize: Int,
        maxGridSize: Int,
        block: (selectedGridSize: Int) -> Unit
    ) {
        val context = view.context
        val iconSl = ColorStateList(
            arrayOf(intArrayOf(-16842912), intArrayOf(16842912)),
            intArrayOf(ThemeStore.textColorSecondary(context), ThemeStore.accentColor(context)))

        val popupMenu = popupMenu {
            style = getPopUpTheme(context)
            dropdownGravity = Gravity.END
            section {
                title = context.getString(getGridSizeTitle(context.resources.isLandscape))
                customItem {
                    layoutResId = R.layout.item_menu_checkable
                    viewBoundCallback = {
                        val radioButton: RadioButton = it.findViewById(R.id.customItemCheckbox)
                        val text: TextView = it.findViewById(R.id.title)
                        text.text = context.getText(R.string.grid_size_1)
                        radioButton.buttonTintList = iconSl
                        radioButton.isChecked = currentGridSize == 1
                    }
                    callback = {
                        block(1)
                    }
                }
                customItem {
                    layoutResId = R.layout.item_menu_checkable
                    viewBoundCallback = {
                        val radioButton: RadioButton = it.findViewById(R.id.customItemCheckbox)
                        val text: TextView = it.findViewById(R.id.title)
                        text.text = context.getText(R.string.grid_size_2)
                        radioButton.buttonTintList = iconSl
                        radioButton.isChecked = currentGridSize == 2
                    }
                    callback = {
                        block(2)
                    }
                }
                if (maxGridSize > 2) {
                    customItem {
                        layoutResId = R.layout.item_menu_checkable
                        viewBoundCallback = {
                            val radioButton: RadioButton = it.findViewById(R.id.customItemCheckbox)
                            val text: TextView = it.findViewById(R.id.title)
                            text.text = context.getText(R.string.grid_size_3)
                            radioButton.buttonTintList = iconSl
                            radioButton.isChecked = currentGridSize == 3
                        }
                        callback = {
                            block(3)
                        }
                    }
                }
                if (maxGridSize > 3) {
                    customItem {
                        layoutResId = R.layout.item_menu_checkable
                        viewBoundCallback = {
                            val radioButton: RadioButton = it.findViewById(R.id.customItemCheckbox)
                            val text: TextView = it.findViewById(R.id.title)
                            text.text = context.getText(R.string.grid_size_4)
                            radioButton.buttonTintList = iconSl
                            radioButton.isChecked = currentGridSize == 4
                        }
                        callback = {
                            block(4)
                        }
                    }
                }
                if (maxGridSize > 4) {
                    customItem {
                        layoutResId = R.layout.item_menu_checkable
                        viewBoundCallback = {
                            val radioButton: RadioButton = it.findViewById(R.id.customItemCheckbox)
                            val text: TextView = it.findViewById(R.id.title)
                            text.text = context.getText(R.string.grid_size_5)
                            radioButton.buttonTintList = iconSl
                            radioButton.isChecked = currentGridSize == 5
                        }
                        callback = {
                            block(5)
                        }
                    }
                }
                if (maxGridSize > 5) {
                    customItem {
                        layoutResId = R.layout.item_menu_checkable
                        viewBoundCallback = {
                            val radioButton: RadioButton = it.findViewById(R.id.customItemCheckbox)
                            val text: TextView = it.findViewById(R.id.title)
                            text.text = context.getText(R.string.grid_size_6)
                            radioButton.buttonTintList = iconSl
                            radioButton.isChecked = currentGridSize == 6
                        }
                        callback = {
                            block(6)
                        }
                    }
                }
                if (maxGridSize > 6) {
                    customItem {
                        layoutResId = R.layout.item_menu_checkable
                        viewBoundCallback = {
                            val radioButton: RadioButton = it.findViewById(R.id.customItemCheckbox)
                            val text: TextView = it.findViewById(R.id.title)
                            text.text = context.getText(R.string.grid_size_7)
                            radioButton.buttonTintList = iconSl
                            radioButton.isChecked = currentGridSize == 7
                        }
                        callback = {
                            block(7)
                        }
                    }
                }
                if (maxGridSize > 7) {
                    customItem {
                        layoutResId = R.layout.item_menu_checkable
                        viewBoundCallback = {
                            val radioButton: RadioButton = it.findViewById(R.id.customItemCheckbox)
                            val text: TextView = it.findViewById(R.id.title)
                            text.text = context.getText(R.string.grid_size_8)
                            radioButton.buttonTintList = iconSl
                            radioButton.isChecked = currentGridSize == 8
                        }
                        callback = {
                            block(8)
                        }
                    }
                }
            }
        }
        popupMenu.show(view.context, view)
    }


    private fun getGridSizeTitle(isLandScape: Boolean): Int = if (isLandScape)
        R.string.action_grid_size_land
    else
        R.string.action_grid_size

    private fun getPopUpTheme(context: Context): Int = if (ColorUtil.isColorLight(ThemeStore.primaryColor(context)))
        R.style.Widget_MPM_Menu_RoundedPopUpMenuTheme
    else
        R.style.Widget_MPM_Menu_Dark_RoundedPopUpMenuTheme
}