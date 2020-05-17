package com.paolovalerdi.abbey.util.preferences

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_WIFI
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.LabelVisibilityMode.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.paolovalerdi.abbey.App
import com.paolovalerdi.abbey.R
import com.paolovalerdi.abbey.helper.SortOrder
import com.paolovalerdi.abbey.model.CategoryInfo
import com.paolovalerdi.abbey.ui.fragments.mainactivity.folders.FoldersFragment
import com.paolovalerdi.abbey.ui.fragments.player.NowPlayingScreen
import com.paolovalerdi.abbey.util.CalendarUtil
import com.paolovalerdi.abbey.util.FileUtil
import java.io.File

/**
 * @author Paolo Valerdi
 */
object PreferenceUtil {

    private val sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(App.staticContext)

    val generalTheme: Int
        get() = getThemeResFromValue(
            sharedPreferences.getString(
                GENERAL_THEME, "" +
                "light"
            )
        )

    val defaultCategories = listOf(
        CategoryInfo(CategoryInfo.Category.HOME, true),
        CategoryInfo(CategoryInfo.Category.SONGS, true),
        CategoryInfo(CategoryInfo.Category.ALBUMS, true),
        CategoryInfo(CategoryInfo.Category.ARTISTS, true),
        CategoryInfo(CategoryInfo.Category.GENRES, false),
        CategoryInfo(CategoryInfo.Category.PLAYLISTS, true)
    )

    var libraryCategories: List<CategoryInfo>
        get() {
            val data = sharedPreferences.getString(LIBRARY_CATEGORIES, null)
            var result: List<CategoryInfo>? = null
            data?.let {
                val gson = Gson()
                val collectionType = object : TypeToken<List<CategoryInfo>>() {}.type
                try {
                    result = gson.fromJson(data, collectionType)
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }
            }
            return result ?: defaultCategories
        }
        set(value) {
            val gson = Gson()
            val collectionType = object : TypeToken<List<CategoryInfo>>() {}.type
            sharedPreferences.edit {
                putString(LIBRARY_CATEGORIES, gson.toJson(value, collectionType))
            }
        }

    var coloredAppShortcuts: Boolean
        get() = sharedPreferences.getBoolean(
            COLORED_APP_SHORTCUTS,
            false
        )
        set(value) {
            sharedPreferences.edit {
                putBoolean(COLORED_APP_SHORTCUTS, value)
            }
        }

    val parallaxEffect: Boolean
        get() = sharedPreferences.getBoolean(
            PARALLAX_EFFECT,
            true
        )

    val loadGenres: Boolean
        get() = sharedPreferences.getBoolean(
            EXPAND_PLAYER,
            false
        )

    val bottomBarLabelMode: Int
        get() {
            val mode = sharedPreferences.getString(
                BOTTOM_BAR_LABEL_MODE,
                "labeled"
            )
            return when (mode) {
                "auto" -> LABEL_VISIBILITY_AUTO
                "unlabeled" -> LABEL_VISIBILITY_UNLABELED
                "selected" -> LABEL_VISIBILITY_SELECTED
                else -> LABEL_VISIBILITY_LABELED
            }
        }

    val coloredMediaDetails: Boolean
        get() = sharedPreferences.getBoolean(
            COLORED_MEDIA_DETAILS,
            true
        )

    val playOnHomeItemLongClick: Boolean
        get() = when (sharedPreferences.getString(HOME_ITEM_LONG_CLICK, "play")) {
            "play" -> true
            else -> false
        }

    var userImageSignature: Long
        get() = sharedPreferences.getLong(USER_IMAGE_SIGNATURE, 0L)
        set(value) {
            sharedPreferences.edit {
                putLong(USER_IMAGE_SIGNATURE, value)
            }
        }

    var userName: String?
        get() = sharedPreferences.getString(
            USER_NAME,
            null
        )
        set(value) {
            sharedPreferences.edit {
                putString(USER_NAME, value)
            }
        }

    var lastPage: Int
        get() = sharedPreferences.getInt(
            LAST_PAGE,
            1
        )
        set(value) {
            sharedPreferences.edit {
                putInt(LAST_PAGE, value)
            }
        }

    var lastMusicChooser: Int
        get() = sharedPreferences.getInt(
            LAST_MUSIC_CHOOSER,
            0
        )
        set(value) {
            sharedPreferences.edit {
                putInt(LAST_MUSIC_CHOOSER, value)
            }
        }

    var songSortOrder: String
        get() = sharedPreferences.getString(
            SONG_SORT_ORDER,
            SortOrder.SongSortOrder.SONG_A_Z
        )!!
        set(value) {
            sharedPreferences.edit {
                putString(SONG_SORT_ORDER, value)
            }
        }

    var artistSortOrder: String
        get() = sharedPreferences.getString(
            ARTIST_SORT_ORDER,
            SortOrder.ArtistSortOrder.ARTIST_A_Z
        )!!
        set(value) {
            sharedPreferences.edit {
                putString(ARTIST_SORT_ORDER, value)
            }
        }

    var albumSortOrder: String
        get() = sharedPreferences.getString(
            ALBUM_SORT_ORDER,
            SortOrder.AlbumSortOrder.ALBUM_A_Z
        )!!
        set(value) {
            sharedPreferences.edit {
                putString(ALBUM_SORT_ORDER, value)
            }
        }

    val artistAlbumSortOrder: String
        get() = sharedPreferences.getString(
            ARTIST_ALBUM_SORT_ORDER,
            SortOrder.ArtistAlbumSortOrder.ALBUM_YEAR
        )!!

    val albumSongSortOrder: String
        get() = sharedPreferences.getString(
            ALBUM_SONG_SORT_ORDER,
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST
        )!!

    val genreSortOder: String
        get() = sharedPreferences.getString(
            GENRE_SORT_ORDER,
            SortOrder.GenreSortOrder.GENRE_A_Z
        )!!

    var songGridSize: Int
        get() = sharedPreferences.getInt(
            SONG_GRID_SIZE,
            App.staticContext.resources.getInteger(R.integer.default_list_columns)
        )
        set(value) {
            sharedPreferences.edit {
                putInt(SONG_GRID_SIZE, value)
            }
        }

    var songGridSizeLand: Int
        get() = sharedPreferences.getInt(
            SONG_GRID_SIZE_LAND,
            App.staticContext.resources.getInteger(R.integer.default_list_columns_land)
        )
        set(value) {
            sharedPreferences.edit {
                putInt(SONG_GRID_SIZE_LAND, value)
            }
        }

    var genreGridSize: Int
        get() = sharedPreferences.getInt(
            GENRE_GRID_SIZE,
            App.staticContext.resources.getInteger(R.integer.default_list_columns)
        )
        set(value) {
            sharedPreferences.edit {
                putInt(SONG_GRID_SIZE, value)
            }
        }

    var genreGridSizeLand: Int
        get() = sharedPreferences.getInt(
            GENRE_GRID_SIZE_LAND,
            App.staticContext.resources.getInteger(R.integer.default_list_columns_land)
        )
        set(value) {
            sharedPreferences.edit {
                putInt(SONG_GRID_SIZE_LAND, value)
            }
        }

    var albumGridSize: Int
        get() = sharedPreferences.getInt(
            ALBUM_GRID_SIZE,
            App.staticContext.resources.getInteger(R.integer.default_grid_columns)
        )
        set(value) {
            sharedPreferences.edit {
                putInt(ALBUM_GRID_SIZE, value)
            }
        }

    var albumGridSizeLand: Int
        get() = sharedPreferences.getInt(
            ALBUM_GRID_SIZE_LAND,
            App.staticContext.resources.getInteger(R.integer.default_grid_columns_land)
        )
        set(value) {
            sharedPreferences.edit {
                putInt(ALBUM_GRID_SIZE_LAND, value)
            }
        }

    var artistGridSize: Int
        get() = sharedPreferences.getInt(
            ARTIST_GRID_SIZE,
            App.staticContext.resources.getInteger(R.integer.default_grid_columns)
        )
        set(value) {
            sharedPreferences.edit {
                putInt(ARTIST_GRID_SIZE, value)
            }
        }

    var artistGridSizeLand: Int
        get() = sharedPreferences.getInt(
            ARTIST_GRID_SIZE_LAND,
            App.staticContext.resources.getInteger(R.integer.default_grid_columns_land)
        )
        set(value) {
            sharedPreferences.edit {
                putInt(ARTIST_GRID_SIZE_LAND, value)
            }
        }

    var playlistGridSize: Int
        get() = sharedPreferences.getInt(
            PLAYLIST_GRID_SIZE,
            App.staticContext.resources.getInteger(R.integer.default_list_columns)
        )
        set(value) {
            sharedPreferences.edit {
                putInt(PLAYLIST_GRID_SIZE, value)
            }
        }

    var playlistGridSizeLand: Int
        get() = sharedPreferences.getInt(
            PLAYLIST_GRID_SIZE_LAND,
            App.staticContext.resources.getInteger(R.integer.default_list_columns_land)
        )
        set(value) {
            sharedPreferences.edit {
                putInt(PLAYLIST_GRID_SIZE_LAND, value)
            }
        }

    var lastSleepTimerValue: Int
        get() = sharedPreferences.getInt(
            LAST_SLEEP_TIMER_VALUE,
            30
        )
        set(value) {
            sharedPreferences.edit {
                putInt(LAST_SLEEP_TIMER_VALUE, value)
            }
        }

    var nextSleepTimerElapsedRealTime: Long
        get() = sharedPreferences.getLong(
            NEXT_SLEEP_TIMER_ELAPSED_REALTIME,
            -1
        )
        set(value) {
            sharedPreferences.edit {
                putLong(LAST_SLEEP_TIMER_VALUE, value)
            }
        }

    var sleepTimerFinishLastSong: Boolean
        get() = sharedPreferences.getBoolean(
            SLEEP_TIMER_FINISH_SONG,
            false
        )
        set(value) {
            sharedPreferences.edit {
                putBoolean(SLEEP_TIMER_FINISH_SONG, value)
            }
        }

    var lastChangelogVersion: Int
        get() = sharedPreferences.getInt(
            LAST_CHANGELOG_VERSION,
            -1
        )
        set(value) {
            sharedPreferences.edit {
                putInt(LAST_CHANGELOG_VERSION, value)
            }
        }

    var SAFSDCardUri: String
        get() = sharedPreferences.getString(
            SAF_SDCARD_URI,
            ""
        ) ?: ""
        set(value) {
            sharedPreferences.edit {
                putString(SAF_SDCARD_URI, value)
            }
        }

    var hasBeenCheckedForPiracyApp: Boolean
        get() = sharedPreferences.getBoolean(
            PIRACY_CHECK,
            false
        )
        set(value) {
            sharedPreferences.edit {
                putBoolean(PIRACY_CHECK, value)
            }
        }

    var isPirateApp: Boolean
        get() = sharedPreferences.getBoolean(
            CHECKED,
            true
        )
        set(value) {
            sharedPreferences.edit {
                putBoolean(CHECKED, value)
            }
        }

    var startDirectory: File
        get() = File(
            sharedPreferences.getString(
                START_DIRECTORY,
                FoldersFragment.getDefaultStartDirectory().path
            )
        )
        set(value) {
            sharedPreferences.edit {
                putString(START_DIRECTORY, FileUtil.safeGetCanonicalPath(value))
            }
        }

    var nowPlayingScreen: NowPlayingScreen
        get() {
            val id = sharedPreferences.getInt(NOW_PLAYING_SCREEN_ID, 0)
            for (np in NowPlayingScreen.values()) {
                if (np.id == id) return np
            }
            return NowPlayingScreen.CARD
        }
        set(value) {
            sharedPreferences.edit {
                putInt(NOW_PLAYING_SCREEN_ID, value.id)
            }
        }

    var imagePadding: Int
        get() = sharedPreferences.getInt(
            MATERIAL_NOW_PLAYING_PADDING,
            25
        )
        set(value) {
            sharedPreferences.edit {
                putInt(MATERIAL_NOW_PLAYING_PADDING, value)
            }
        }

    val showSynchronizedLyrics: Boolean
        get() = sharedPreferences.getBoolean(
            SYNCHRONIZED_LYRICS_SHOW,
            true
        )

    val showVolumeSlider: Boolean
        get() = sharedPreferences.getBoolean(
            SHOW_VOLUME_SLIDER,
            true
        )

    val coloredNotification: Boolean
        get() = sharedPreferences.getBoolean(
            COLORED_NOTIFICATION,
            true
        )

    val classicNotification: Boolean
        get() = sharedPreferences.getBoolean(
            CLASSIC_NOTIFICATION,
            false
        )

    val ignoreMediaStoreArtwork: Boolean
        get() = sharedPreferences.getBoolean(
            IGNORE_MEDIA_STORE_ARTWORK,
            false
        )

    val albumArtOnLockScreen: Boolean
        get() = sharedPreferences.getBoolean(
            ALBUM_ART_ON_LOCKSCREEN,
            true
        )

    val blurredAlbumArt: Boolean
        get() = sharedPreferences.getBoolean(
            BLURRED_ALBUM_ART,
            false
        )

    val audioDucking: Boolean
        get() = sharedPreferences.getBoolean(
            AUDIO_DUCKING,
            true
        )

    val gaplessPlayback: Boolean
        get() = sharedPreferences.getBoolean(
            GAPLESS_PLAYBACK,
            true
        )

    val rememberShuffle: Boolean
        get() = sharedPreferences.getBoolean(
            REMEMBER_SHUFFLE,
            true
        )

    val replayGainPreampWithTag: Float
        get() = sharedPreferences.getFloat(
            RG_PREAMP_WITH_TAG,
            0f
        )

    val replayGainPreampWithoutTag: Float
        get() = sharedPreferences.getFloat(
            RG_PREAMP_WITHOUT_TAG,
            0f
        )

    val lastAddedCutoffText: String
        get() = getCutoffText(LAST_ADDED_CUTOFF, App.staticContext)

    val recentlyPlayedCutoffText: String
        get() = getCutoffText(RECENTLY_PLAYED_CUTOFF, App.staticContext)

    val lastAddedCutoffTimeMillis: Long
        get() = getCutoffTimeMillis(LAST_ADDED_CUTOFF)

    val recentlyPlayedCutoffTimeMillis: Long
        get() = getCutoffTimeMillis(RECENTLY_PLAYED_CUTOFF)

    private val autoDownloadImagesPolicty: String
        get() = sharedPreferences.getString(
            AUTO_DOWNLOAD_DATA_POLICY,
            "only_wifi"
        ) ?: "only_wifi"

    var isBlacklistInitialized: Boolean
        get() = sharedPreferences.getBoolean(
            INITIALIZED_BLACKLIST,
            false
        )
        set(value) {
            sharedPreferences.edit {
                putBoolean(INITIALIZED_BLACKLIST, value)
            }
        }

    fun registerOnSharedPreferenceChangedListener(sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    fun unregisterOnSharedPreferenceChangedListener(sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    fun isAllowedToDownloadMetaData(): Boolean = when (autoDownloadImagesPolicty) {
        "always" -> true
        "only_wifi" -> {
            val connectivityManager = App.staticContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            netInfo?.let {
                return (it.type == TYPE_WIFI) and it.isConnected
            }
            false
        }
        else -> false
    }

    fun getReplayGainSourceMode(): Byte = when (sharedPreferences.getString(RG_SOURCE_MODE, "none")) {
        "track" -> RG_SOURCE_MODE_TRACK
        "album" -> RG_SOURCE_MODE_ALBUM
        else -> RG_SOURCE_MODE_NONE
    }

    fun setReplayGainPreamp(with: Float, without: Float) {
        sharedPreferences.edit {
            putFloat(RG_PREAMP_WITH_TAG, with)
            putFloat(RG_PREAMP_WITHOUT_TAG, without)
        }
    }

    fun getThemeResFromValue(string: String?): Int = when (string) {
        "dark" -> R.style.Theme_Abbey
        "black" -> R.style.Theme_Abbey_Black
        else -> R.style.Theme_Abbey_Light
    }

    private fun getCutoffText(cutoff: String, context: Context): String = when (sharedPreferences.getString(cutoff, "")) {
        "today" -> context.getString(R.string.today)
        "this_week" -> context.getString(R.string.this_week)
        "past_seven_days" -> context.getString(R.string.past_seven_days)
        "past_three_months" -> context.getString(R.string.past_three_months)
        "this_year" -> context.getString(R.string.this_year)
        "this_month" -> context.getString(R.string.this_month)
        else -> context.getString(R.string.this_month)
    }

    private fun getCutoffTimeMillis(cutoff: String): Long {
        val calendarUtil = CalendarUtil()
        val interval: Long

        interval = when (sharedPreferences.getString(cutoff, "")) {
            "today" -> calendarUtil.elapsedToday
            "this_week" -> calendarUtil.elapsedWeek
            "past_seven_days" -> calendarUtil.getElapsedDays(7)
            "past_three_months" -> calendarUtil.getElapsedMonths(3)
            "this_year" -> calendarUtil.elapsedYear
            "this_month" -> calendarUtil.elapsedMonth
            else -> calendarUtil.elapsedMonth
        }

        return System.currentTimeMillis() - interval
    }

}