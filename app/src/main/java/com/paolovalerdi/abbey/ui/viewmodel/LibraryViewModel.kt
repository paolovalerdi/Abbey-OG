package com.paolovalerdi.abbey.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.paolovalerdi.abbey.interfaces.MusicServiceEventListener
import com.paolovalerdi.abbey.model.*
import com.paolovalerdi.abbey.repository.*
import com.paolovalerdi.abbey.util.extensions.toAlbums
import com.paolovalerdi.abbey.util.extensions.toArtists
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author Paolo Valerdi
 */
class LibraryViewModel(app: Application) : AndroidViewModel(app), MusicServiceEventListener {

    companion object {

        const val ALL_SONGS = 101
        const val ALL_ALBUMS = 102
        const val ALL_ARTIST = 103
        const val HOME_CONTENT = 104

    }

    private val contentResolver = getApplication<Application>().contentResolver

    init {
        viewModelScope.launch {
            loadLibraryContent()
        }
    }

    override fun onPlayingMetaChanged() {
        viewModelScope.launch {
            loadHomeContent()
        }
    }

    override fun onMediaStoreChanged() {
        loadLibraryContent()
    }

    fun forceLoad(type: Int) {
        viewModelScope.launch {
            when (type) {
                ALL_SONGS -> {
                    songs.value = loadAllSongsAsync().await()
                }
                ALL_ALBUMS -> {
                    albums.value = loadAllAlbumsAsync().await()
                }
                ALL_ARTIST -> {
                    artists.value = loadAllArtistsAsync().await()
                }
                HOME_CONTENT -> {
                    loadHomeContent()
                }
            }
        }
    }

    fun getAllSongs(): LiveData<List<Song>> = songs

    fun getAllAlbums(): LiveData<List<Album>> = albums

    fun getAllArtists(): LiveData<List<Artist>> = artists

    fun getAllGenres(): LiveData<List<Genre>> = genres

    fun getAllPlaylist(): LiveData<List<Playlist>> = playlist

    fun getHomeContent(): LiveData<HomeContentWrapper> = homeContent

    private val songs = MutableLiveData<List<Song>>()

    private val albums = MutableLiveData<List<Album>>()

    private val artists = MutableLiveData<List<Artist>>()

    private val genres = MutableLiveData<List<Genre>>()

    private val playlist = MutableLiveData<List<Playlist>>()

    private val homeContent = MutableLiveData<HomeContentWrapper>()

    private fun loadLibraryContent() {
        viewModelScope.launch {
            songs.value = loadAllSongsAsync().await()
            albums.value = loadAllAlbumsAsync().await()
            artists.value = loadAllArtistsAsync().await()
            genres.value = loadAllGenresAsync().await()
            playlist.value = loadAllPlaylistsAsync().await()
            loadHomeContent()
        }
    }

    private fun loadAllSongsAsync() = viewModelScope.async(IO) {
        SongRepositoryKT.getAllSongs()
    }

    private fun loadAllAlbumsAsync() = viewModelScope.async(IO) {
        AlbumsRepositoryKT.getAllAlbums()
    }

    private fun loadAllArtistsAsync() = viewModelScope.async(IO) {
        ArtistsRepositoryKT.getAllArtists()
    }

    private fun loadAllGenresAsync() = viewModelScope.async(IO) {
        GenreRepository.getAllGenres(getApplication())
    }

    private fun loadAllPlaylistsAsync() = viewModelScope.async(IO) {
        PlaylistRepository.getAllPlaylists(getApplication())
    }

    private suspend fun loadHomeContent() = withContext(IO) {

        val topFiveArtists = async { TopAndRecentlyPlayedTracksRepository.getTopTracks(getApplication()).toArtists(true).take(5) }

        val lastAddedAlbums = async { LastAddedRepository.getLastAddedSongs(getApplication()).toArtists(true) }

        val recentlyPlayed = async {
            TopAndRecentlyPlayedTracksRepository.getRecentlyPlayedTracks(getApplication()).toAlbums()
        }

        homeContent.postValue(
            HomeContentWrapper(
                topFiveArtists.await(),
                lastAddedAlbums.await(),
                recentlyPlayed.await()
            )
        )
    }

    override fun onServiceConnected() {}
    override fun onServiceDisconnected() {}
    override fun onQueueChanged() {}
    override fun onPlayStateChanged() {}
    override fun onRepeatModeChanged() {}
    override fun onShuffleModeChanged() {}

}