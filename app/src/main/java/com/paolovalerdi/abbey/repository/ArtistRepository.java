package com.paolovalerdi.abbey.repository;

import android.content.Context;
import android.provider.MediaStore.Audio.AudioColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paolovalerdi.abbey.model.Album;
import com.paolovalerdi.abbey.model.Artist;
import com.paolovalerdi.abbey.model.Song;
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.paolovalerdi.abbey.repository.SongRepository.AUDIO_COLUMNS_ALBUM_ARTIST;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class ArtistRepository {

    private static String getSongLoaderSortOrder(Context context) {
        return PreferenceUtil.INSTANCE.getArtistSortOrder() + ", " + PreferenceUtil.INSTANCE.getArtistAlbumSortOrder() + ", " + PreferenceUtil.INSTANCE.getAlbumSongSortOrder();
    }

    public static ArrayList<Artist> getAllAlbumArtists(@NonNull final Context context) {
        ArrayList<Song> songs = SongRepository.getSongs(
                SongRepository.makeSongCursor(
                        context,
                        null,
                        null,
                        getSongLoaderSortOrder(context)
                )
        );
        return splitIntoArtistss(songs);
    }

    @NonNull
    public static ArrayList<Artist> getAllArtists(@NonNull final Context context) {
        ArrayList<Song> songs = SongRepository.getSongs(
                SongRepository.makeSongCursor(
                        context,
                        null,
                        null,
                        getSongLoaderSortOrder(context)
                )
        );
        return splitIntoArtists(AlbumRepository.splitIntoAlbums(songs));
    }


    public static ArrayList<Artist> splitIntoArtists(final List<Song> songs) {
        HashMap<String, Album> albumHashMap = new HashMap<>();
        for (Song song : songs) {
            String albulmID = song.artistName;
            Album album = albumHashMap.get(albulmID) == null ? new Album() : albumHashMap.get(albulmID);
            album.songs.add(song);
            albumHashMap.put(albulmID, album);
        }

        HashMap<String, Artist> artistHashMap = new HashMap<>();
        List<Album> albums = new ArrayList<>(albumHashMap.values());
        for (Album album : albums) {
            String artistName = album.getArtistName();
            Artist artist = artistHashMap.get(artistName) == null ? new Artist() : artistHashMap.get(artistName);
            artist.albums.add(album);
            artistHashMap.put(artistName, artist);
        }

        return new ArrayList<Artist>(artistHashMap.values());

    }

    @NonNull
    public static ArrayList<Artist> getArtists(@NonNull final Context context, String query) {
        ArrayList<Song> songs = SongRepository.getSongs(SongRepository.makeSongCursor(
                context,
                AudioColumns.ARTIST + " LIKE ?",
                new String[]{"%" + query + "%"},
                getSongLoaderSortOrder(context))
        );
        return splitIntoArtists(AlbumRepository.splitIntoAlbums(songs));
    }

    @NonNull
    public static Artist getArtist(@NonNull final Context context, int artistId) {
        ArrayList<Song> songs = SongRepository.getSongs(SongRepository.makeSongCursor(
                context,
                AudioColumns.ARTIST_ID + "=?",
                new String[]{String.valueOf(artistId)},
                getSongLoaderSortOrder(context))
        );
        return new Artist(AlbumRepository.splitIntoAlbums(songs));
    }

    @NonNull
    public static Artist getAlbumArtist(@NonNull final Context context, String name) {
        ArrayList<Song> songs = SongRepository.getSongs(SongRepository.makeSongCursor(
                context,
                AUDIO_COLUMNS_ALBUM_ARTIST + "=?",
                new String[]{name},
                getSongLoaderSortOrder(context))
        );
        return new Artist(AlbumRepository.splitIntoAlbums(songs));
    }

    @NonNull
    public static Artist getArtist(@NonNull final Context context, String artistName) {
        ArrayList<Song> songs = SongRepository.getSongs(SongRepository.makeSongCursor(
                context,
                AudioColumns.ARTIST + "=?",
                new String[]{artistName},
                getSongLoaderSortOrder(context))
        );
        return new Artist(AlbumRepository.splitIntoAlbums(songs));
    }

    public static ArrayList<Artist> splitIntoAlbumArtist(final ArrayList<Album> albums) {
        HashMap<String, Artist> map = new HashMap<>();
        for (Album album : albums) {
            String key = album.getAlbumArtistName();
            if (key != null) {
                Artist artist = map.get(key) != null ? map.get(key) : new Artist();
                artist.albums.add(album);
                map.put(key, artist);
            }
        }

        return new ArrayList(map.values());
    }

    public static ArrayList<Artist> splitIntoArtistss(final ArrayList<Song> songs) {
        // First group the songs in albums by filtering each artist name
        HashMap<String, Album> amap = new HashMap<>();
        for (Song song : songs) {
            String key = song.artistName;
            if (key != null) {
                Album album = amap.get(key) != null ? amap.get(key) : new Album();
                album.songs.add(song);
                amap.put(key, album);
            }
        }

        ArrayList<Album> albums = new ArrayList<>(amap.values());
        HashMap<String, Artist> map = new HashMap<>();
        for (Album album : albums) {
            String key = album.getArtistName();
            if (key != null) {
                Artist artist = map.get(key) != null ? map.get(key) : new Artist();
                artist.albums.add(album);
                map.put(key, artist);
            }
        }

        return new ArrayList(map.values());
    }

    @NonNull
    public static ArrayList<Artist> splitIntoArtists(@Nullable final ArrayList<Album> albums) {
        ArrayList<Artist> artists = new ArrayList<>();
        if (albums != null) {
            for (Album album : albums) {
                getOrCreateArtist(artists, album.getArtistId()).albums.add(album);
            }
        }
        return artists;
    }

    private static Artist getOrCreateArtist(ArrayList<Artist> artists, int artistId) {
        for (Artist artist : artists) {
            if (!artist.albums.isEmpty() && !artist.albums.get(0).songs.isEmpty() && artist.albums.get(0).songs.get(0).artistId == artistId) {
                return artist;
            }
        }
        Artist artist = new Artist();
        artists.add(artist);
        return artist;
    }
}
