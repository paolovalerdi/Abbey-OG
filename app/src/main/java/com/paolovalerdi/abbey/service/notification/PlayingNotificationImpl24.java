package com.paolovalerdi.abbey.service.notification;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.request.transition.Transition;
import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension;
import com.paolovalerdi.abbey.glide.AbbeySimpleTarget;
import com.paolovalerdi.abbey.glide.GlideApp;
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteWrapper;
import com.paolovalerdi.abbey.model.Song;
import com.paolovalerdi.abbey.service.MusicService;
import com.paolovalerdi.abbey.ui.activities.MainActivity;
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil;

import static com.paolovalerdi.abbey.service.MusicServiceConstantsKt.ACTION_QUIT;
import static com.paolovalerdi.abbey.service.MusicServiceConstantsKt.ACTION_REWIND;
import static com.paolovalerdi.abbey.service.MusicServiceConstantsKt.ACTION_SKIP;
import static com.paolovalerdi.abbey.service.MusicServiceConstantsKt.ACTION_TOGGLE_PAUSE;

public class PlayingNotificationImpl24 extends PlayingNotification {

    @Override
    public synchronized void update() {
        stopped = false;

        final Song song = service.getCurrentSong();

        final SpannableString sb = new SpannableString(song.albumName);
        sb.setSpan(new StyleSpan(Typeface.BOLD), 0, song.albumName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        final SpannableString sb2 = new SpannableString(song.title);
        sb2.setSpan(new StyleSpan(Typeface.BOLD), 0, song.title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        final boolean isPlaying = service.isPlaying();

        final int playButtonResId = isPlaying
                ? R.drawable.ic_pause : R.drawable.ic_play_arrow;

        Intent action = new Intent(service, MainActivity.class);
        action.putExtra(MainActivity.EXPAND_PLAYER, true);
        action.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent clickIntent = PendingIntent.getActivity(service, 0, action, 0);

        final ComponentName serviceName = new ComponentName(service, MusicService.class);
        Intent intent = new Intent(ACTION_QUIT);
        intent.setComponent(serviceName);
        final PendingIntent deleteIntent = PendingIntent.getService(service, 0, intent, 0);

        final int bigNotificationImageSize = service.getResources().getDimensionPixelSize(R.dimen.notification_big_image_size);
        service.runOnUiThread(() -> GlideApp.with(service)
                .asBitmapPalette()
                .load(AbbeyGlideExtension.getSongModel(song))
                .transition(AbbeyGlideExtension.getDefaultTransition())
                .songOptions(song)
                .into(new AbbeySimpleTarget<BitmapPaletteWrapper>(bigNotificationImageSize, bigNotificationImageSize) {
                    @Override
                    public void onResourceReady(@NonNull BitmapPaletteWrapper resource, Transition<? super BitmapPaletteWrapper> glideAnimation) {
                        Palette palette = resource.getPalette();
                        update(resource.getBitmap(), palette.getVibrantColor(palette.getMutedColor(Color.TRANSPARENT)));
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        update(null, Color.TRANSPARENT);
                    }

                    void update(Bitmap bitmap, int color) {
                        if (bitmap == null)
                            bitmap = BitmapFactory.decodeResource(service.getResources(), R.drawable.default_album_art);
                        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(playButtonResId,
                                service.getString(R.string.action_play_pause),
                                retrievePlaybackAction(ACTION_TOGGLE_PAUSE));
                        NotificationCompat.Action previousAction = new NotificationCompat.Action(R.drawable.ic_skip_previous,
                                service.getString(R.string.action_previous),
                                retrievePlaybackAction(ACTION_REWIND));
                        NotificationCompat.Action nextAction = new NotificationCompat.Action(R.drawable.ic_skip_next,
                                service.getString(R.string.action_next),
                                retrievePlaybackAction(ACTION_SKIP));
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, NOTIFICATION_CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setSubText(sb)
                                .setLargeIcon(bitmap)
                                .setContentIntent(clickIntent)
                                .setDeleteIntent(deleteIntent)
                                .setContentTitle(sb2)
                                .setContentText(song.artistName)
                                .setOngoing(isPlaying)
                                .setShowWhen(false)
                                .addAction(previousAction)
                                .addAction(playPauseAction)
                                .addAction(nextAction);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder.setStyle(new MediaStyle().setMediaSession(service.getMediaSession().getSessionToken()).setShowActionsInCompactView(0, 1, 2))
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O && PreferenceUtil.INSTANCE.getColoredNotification())
                                builder.setColor(color);
                        }

                        if (stopped)
                            return; // notification has been stopped before loading was finished
                        updateNotifyModeAndPostNotification(builder.build());
                    }
                }));
    }

    private PendingIntent retrievePlaybackAction(final String action) {
        final ComponentName serviceName = new ComponentName(service, MusicService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(service, 0, intent, 0);
    }
}
