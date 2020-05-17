package com.paolovalerdi.abbey.glide;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.bumptech.glide.signature.ObjectKey;
import com.paolovalerdi.abbey.App;
import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.glide.audiocover.AudioFileCover;
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteWrapper;
import com.paolovalerdi.abbey.model.Artist;
import com.paolovalerdi.abbey.model.Genre;
import com.paolovalerdi.abbey.model.Playlist;
import com.paolovalerdi.abbey.model.Song;
import com.paolovalerdi.abbey.model.smartplaylist.NotRecentlyPlayedPlaylist;
import com.paolovalerdi.abbey.util.ArtistSignatureUtil;
import com.paolovalerdi.abbey.util.CustomArtistImageUtil;
import com.paolovalerdi.abbey.util.MusicUtil;
import com.paolovalerdi.abbey.util.cimages.GenreImageUtil;
import com.paolovalerdi.abbey.util.cimages.GenreSignatureUtil;
import com.paolovalerdi.abbey.util.cimages.PlaylistImageUtil;
import com.paolovalerdi.abbey.util.cimages.PlaylistSignatureUtil;
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil;

import java.io.File;

@GlideExtension
public final class AbbeyGlideExtension {

    private AbbeyGlideExtension() {
    }

    @NonNull
    @GlideType(BitmapPaletteWrapper.class)
    static RequestBuilder<BitmapPaletteWrapper> asBitmapPalette(
            RequestBuilder<BitmapPaletteWrapper> requestBuilder
    ) {
        return requestBuilder;
    }


    @NonNull
    @GlideOption
    static BaseRequestOptions<?> artistOptions(
            BaseRequestOptions<?> options,
            Artist artist
    ) {
        return options
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.default_artist_image)
                .placeholder(R.drawable.default_artist_image)
                .priority(Priority.LOW)
                .signature(createSignature(artist));
    }

    @NonNull
    @GlideOption
    static BaseRequestOptions<?> userOptions(
            BaseRequestOptions<?> options
    ) {
        return options
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.default_avatar)
                .signature(new ObjectKey(String.valueOf(PreferenceUtil.INSTANCE.getUserImageSignature())));
    }

    @NonNull
    @GlideOption
    static BaseRequestOptions<?> songOptions(
            BaseRequestOptions<?> options,
            Song song
    ) {
        return options
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.default_album_art)
                .signature(createSignature(song));
    }

    @NonNull
    @GlideOption
    static BaseRequestOptions<?> playlistOptions(
            BaseRequestOptions<?> options,
            Playlist playlist
    ) {
        return options
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.default_playlist)
                .signature(createSignature(playlist));
    }

    @NonNull
    @GlideOption
    static BaseRequestOptions<?> genreOptions(
            BaseRequestOptions<?> options,
            Genre genre
    ) {
        return options
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.default_playlist)
                .signature(createSignature(genre));
    }

    @NonNull
    @GlideOption
    static BaseRequestOptions<?> roundedCorners(
            BaseRequestOptions<?> options,
            final boolean shouldApply,
            final int size
    ) {
        return shouldApply ? options.transform(new RoundedCorners(size)) : options;
    }


    public static <TranscodeType> GenericTransitionOptions<TranscodeType> getDefaultTransition() {
        return new GenericTransitionOptions<TranscodeType>().transition(android.R.anim.fade_in);
    }

    public static Object getArtistModel(Artist artist) {
        return getArtistModel(
                artist,
                CustomArtistImageUtil.getInstance(App.Companion.getInstance()).hasCustomArtistImage(artist),
                false
        );
    }

    public static Object getArtistModel(Artist artist, boolean forceDownload) {
        return getArtistModel(
                artist,
                CustomArtistImageUtil.getInstance(App.Companion.getInstance()).hasCustomArtistImage(artist),
                forceDownload
        );
    }

    public static Object getSongModel(Song song) {
        return getSongModel(
                song,
                PreferenceUtil.INSTANCE.getIgnoreMediaStoreArtwork()
        );
    }

    public static Object getSongModel(Song song, boolean ignoreMediaStore) {
        if (ignoreMediaStore) {
            return new AudioFileCover(song.data);
        } else {
            return MusicUtil.getMediaStoreAlbumCoverUri(song.albumId);
        }
    }

    public static Object getPlaylistModel(Playlist playlist) {
        PlaylistImageUtil instance = PlaylistImageUtil.Companion.getInstance();
        if (instance.hasCustomPlaylistImage(playlist)) {
            return PlaylistImageUtil.Companion.getFile(playlist);
        } else {
            if (playlist instanceof NotRecentlyPlayedPlaylist) {
                instance.setImageAndSave(
                        playlist,
                        Uri.parse("https://mir-s3-cdn-cf.behance.net/project_modules/max_1200/3a0ae366335957.5b12df45e2ab8.jpg")
                );
            } else {
                instance.generateImageAndSave(playlist);
            }
        }
        return PlaylistImageUtil.Companion.getFile(playlist);
    }

    public static Object getUserModel() {
        File dir = App.Companion.getStaticContext().getFilesDir();
        return new File(dir, "profile.jpg");
    }

    public static Object getGenreModel(Genre genre) {
        GenreImageUtil instance = GenreImageUtil.Companion.getInstance();
        if (instance.hasCustomPlaylistImage(genre)) {
            return GenreImageUtil.Companion.getFile(genre);
        } else {
            instance.generateImageAndSave(genre);
        }
        return GenreImageUtil.Companion.getFile(genre);
    }

    private static Object getArtistModel(Artist artist, boolean hasCustomImage, boolean forceDownload) {
        if (!hasCustomImage) {
            CustomArtistImageUtil customArtistImageUtil = CustomArtistImageUtil.getInstance(App.Companion.getInstance());
            customArtistImageUtil.fetchAndSave(artist, forceDownload);
        }
        return CustomArtistImageUtil.getFile(artist);
    }

    private static Key createSignature(Artist artist) {
        return ArtistSignatureUtil.getInstance().getArtistSignature(artist.getName());
    }

    private static Key createSignature(Song song) {
        return new MediaStoreSignature("", song.dateModified, 0);
    }

    private static Key createSignature(Playlist playlist) {
        return PlaylistSignatureUtil.Companion.getInstance().getPlaylistSignature(playlist.id);
    }

    private static Key createSignature(Genre genre) {
        return GenreSignatureUtil.Companion.getInstance().getGenreSignature(genre.id);
    }

}
