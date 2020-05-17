package com.paolovalerdi.abbey.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paolovalerdi.abbey.interfaces.MusicServiceEventListener
import com.paolovalerdi.abbey.lastfm.rest.LastFMRestClient
import com.paolovalerdi.abbey.lastfm.rest.model.LastFmAlbum
import com.paolovalerdi.abbey.lastfm.rest.model.LastFmArtist
import com.paolovalerdi.abbey.model.MediaDetailsWrapper
import com.paolovalerdi.abbey.model.smartplaylist.HistoryPlaylist
import com.paolovalerdi.abbey.model.smartplaylist.LastAddedPlaylist
import com.paolovalerdi.abbey.model.smartplaylist.MyTopTracksPlaylist
import com.paolovalerdi.abbey.model.smartplaylist.NotRecentlyPlayedPlaylist
import com.paolovalerdi.abbey.repository.AlbumRepository
import com.paolovalerdi.abbey.repository.ArtistRepository
import com.paolovalerdi.abbey.repository.GenreRepository
import com.paolovalerdi.abbey.repository.PlaylistRepository
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.await
import java.util.*

/**
 * @author Paolo Valerdi
 */
class MediaDetailsViewModel(app: Application) : AndroidViewModel(app), MusicServiceEventListener {

    companion object {

        const val TYPE_ALBUM = 101
        const val TYPE_ARTIST = 102
        const val TYPE_PLAYLIST = 103
        const val TYPE_GENRE = 104

        const val SMART_PLAYLIST_LAST_ADDED_ID = 1901
        const val SMART_PLAYLIST_TOP_TRACKS_ID = 1902
        const val SMART_PLAYLIST_NOT_RECENTLY_PLAYED_ID = 1903
        const val SMART_PLAYLIST_HISTORY_ID = 1904

    }

    private val lastFMRestClient = LastFMRestClient(app)

    override fun onMediaStoreChanged() {
        loadData(mediaType, mediaId)
    }

    fun setUpMedia(mediaType: Int, mediaId: Any) {
        this.mediaType = mediaType
        this.mediaId = mediaId
        loadData(mediaType, mediaId)
    }

    fun getData(): LiveData<MediaDetailsWrapper> = mediaContent

    fun getMediaModel(): LiveData<Any> = mediaModel

    private fun loadData(mediaType: Int, mediaId: Any) {
        viewModelScope.launch(IO) {
            when (mediaType) {
                TYPE_ALBUM -> {
                    val album = loadAlbumAsync(mediaId as Int).await()
                    mediaContent.postValue(MediaDetailsWrapper(album.songs))
                    mediaModel.postValue(album)
                    fetchWiki(Locale.getDefault().language, album.title, album.artistName)

                }
                TYPE_ARTIST -> {
                    val artist = loadArtistAsync(mediaId as String).await()
                    mediaContent.postValue(MediaDetailsWrapper(artist.songs, artist.albums))
                    mediaModel.postValue(artist)
                    fetchBiography(Locale.getDefault().language, artist.name)

                }
                TYPE_GENRE -> {
                    val genre = loadGenreAsync(mediaId as Int).await()
                    val songs = async { GenreRepository.getSongs(getApplication(), genre.id) }
                    mediaContent.postValue(MediaDetailsWrapper(songs.await()))
                    mediaModel.postValue(genre)
                }
                TYPE_PLAYLIST -> {
                    val genre = loadPlaylistAsync(mediaId as Int).await()
                    val songs = async { genre.getSongs(getApplication()) }
                    mediaContent.postValue(MediaDetailsWrapper(songs.await()))
                    mediaModel.postValue(genre)
                }
            }
        }
    }


    private fun fetchWiki(lang: String, album: String, artistName: String) {
        Log.d("VIEWMODEL", PreferenceUtil.isAllowedToDownloadMetaData().toString())
        if (PreferenceUtil.isAllowedToDownloadMetaData()) {
            viewModelScope.launch(IO) {
                try {
                    val albumInfo: LastFmAlbum? = lastFMRestClient.apiService.getAlbumInfo(
                        album,
                        artistName,
                        lang
                    ).await()

                    albumInfo?.album?.wiki?.content?.let { content ->
                        Log.d("VIEWMODEL", content)
                        if (content.trim().isNotEmpty()) {
                            val loadedSongs = mediaContent.value?.songs!!
                            mediaContent.postValue(
                                MediaDetailsWrapper(
                                    loadedSongs,
                                    headerText = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT)
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun fetchBiography(lang: String, artistName: String) {
        Log.d("VIEWMODEL", PreferenceUtil.isAllowedToDownloadMetaData().toString())
        if (PreferenceUtil.isAllowedToDownloadMetaData()) {
            viewModelScope.launch(IO) {
                try {
                    val artistInfo: LastFmArtist? = lastFMRestClient.apiService.getArtistInfo(
                        artistName,
                        lang,
                        null
                    ).await()

                    artistInfo?.artist?.bio?.content?.let { content ->
                        if (content.trim().isNotEmpty()) {
                            val loadedSongs = mediaContent.value?.songs!!
                            val loadedAlbums = mediaContent.value?.albums!!
                            mediaContent.postValue(
                                MediaDetailsWrapper(
                                    loadedSongs,
                                    loadedAlbums,
                                    HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT)
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private var mediaType: Int = -1
    private var mediaId: Any = Any()

    private val mediaContent = MutableLiveData<MediaDetailsWrapper>()

    private val mediaModel = MutableLiveData<Any>()

    private fun loadArtistAsync(mediaId: Int) = viewModelScope.async(IO) { ArtistRepository.getArtist(getApplication(), mediaId) }

    private fun loadArtistAsync(name: String) = viewModelScope.async(IO) { ArtistRepository.getAlbumArtist(getApplication(), name) }

    private fun loadAlbumAsync(mediaId: Int) = viewModelScope.async(IO) { AlbumRepository.getAlbum(getApplication(), mediaId) }

    private fun loadPlaylistAsync(mediaId: Int) = viewModelScope.async(IO) {
        when (mediaId) {
            SMART_PLAYLIST_TOP_TRACKS_ID -> MyTopTracksPlaylist(getApplication())
            SMART_PLAYLIST_HISTORY_ID -> HistoryPlaylist(getApplication())
            SMART_PLAYLIST_LAST_ADDED_ID -> LastAddedPlaylist(getApplication())
            SMART_PLAYLIST_NOT_RECENTLY_PLAYED_ID -> NotRecentlyPlayedPlaylist(getApplication())
            else -> PlaylistRepository.getPlaylist(getApplication(), mediaId)
        }
    }

    private fun loadGenreAsync(mediaId: Int) = viewModelScope.async(IO) { GenreRepository.getGenre(getApplication(), mediaId) }

    override fun onServiceConnected() {}
    override fun onServiceDisconnected() {}
    override fun onQueueChanged() {}
    override fun onPlayingMetaChanged() {}
    override fun onPlayStateChanged() {}
    override fun onRepeatModeChanged() {}
    override fun onShuffleModeChanged() {}

}