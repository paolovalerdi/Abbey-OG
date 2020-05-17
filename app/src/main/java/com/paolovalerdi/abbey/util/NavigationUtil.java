package com.paolovalerdi.abbey.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.helper.MusicPlayerRemote;
import com.paolovalerdi.abbey.model.Genre;
import com.paolovalerdi.abbey.model.Playlist;
import com.paolovalerdi.abbey.ui.activities.detail.AlbumDetailActivity;
import com.paolovalerdi.abbey.ui.activities.detail.ArtistDetailActivity;
import com.paolovalerdi.abbey.ui.activities.detail.GenreDetailActivity;
import com.paolovalerdi.abbey.ui.activities.detail.MediaDetailsActivity;
import com.paolovalerdi.abbey.ui.activities.detail.PlaylistDetailActivity;
import com.paolovalerdi.abbey.ui.viewmodel.MediaDetailsViewModel;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class NavigationUtil {

    public static void goToArtist(@NonNull final Activity activity, final int artistId) {
        final Intent intent = new Intent(activity, ArtistDetailActivity.class);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_ID, artistId);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_TYPE, MediaDetailsViewModel.TYPE_ARTIST);
        activity.startActivity(intent);
    }

    public static void goToArtist(@NonNull final Activity activity, final String artisName) {
        final Intent intent = new Intent(activity, ArtistDetailActivity.class);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_ID, artisName);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_TYPE, MediaDetailsViewModel.TYPE_ARTIST);
        activity.startActivity(intent);
    }

    public static void goToAlbum(@NonNull final Activity activity, final int albumId) {
        final Intent intent = new Intent(activity, AlbumDetailActivity.class);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_ID, albumId);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_TYPE, MediaDetailsViewModel.TYPE_ALBUM);
        activity.startActivity(intent);
    }

    public static void goToGenre(@NonNull final Activity activity, final Genre genre) {
        final Intent intent = new Intent(activity, GenreDetailActivity.class);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_ID, genre.id);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_TYPE, MediaDetailsViewModel.TYPE_GENRE);
        activity.startActivity(intent);
    }

    public static void goToPlaylist(@NonNull final Activity activity, final Playlist playlist) {
        final Intent intent = new Intent(activity, PlaylistDetailActivity.class);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_ID, playlist.id);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_TYPE, MediaDetailsViewModel.TYPE_PLAYLIST);
        activity.startActivity(intent);
    }

    public static void goToSmartPlaylist(@NonNull final Activity activity, final int smartPlaylistID) {
        final Intent intent = new Intent(activity, PlaylistDetailActivity.class);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_ID, smartPlaylistID);
        intent.putExtra(MediaDetailsActivity.EXTRA_MEDIA_TYPE, MediaDetailsViewModel.TYPE_PLAYLIST);
        activity.startActivity(intent);
    }


    public static void openEqualizer(@NonNull final Activity activity) {
        final int sessionId = MusicPlayerRemote.getAudioSessionId();
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            Toast.makeText(activity, activity.getResources().getString(R.string.no_audio_ID), Toast.LENGTH_LONG).show();
        } else {
            try {
                final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId);
                effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                activity.startActivityForResult(effects, 0);
            } catch (@NonNull final ActivityNotFoundException notFound) {
                Toast.makeText(activity, activity.getResources().getString(R.string.no_equalizer), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
