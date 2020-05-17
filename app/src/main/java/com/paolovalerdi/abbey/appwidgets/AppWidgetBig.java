package com.paolovalerdi.abbey.appwidgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.transition.Transition;
import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.appwidgets.base.BaseAppWidget;
import com.paolovalerdi.abbey.glide.AbbeyGlideExtension;
import com.paolovalerdi.abbey.glide.AbbeySimpleTarget;
import com.paolovalerdi.abbey.glide.GlideApp;
import com.paolovalerdi.abbey.model.Song;
import com.paolovalerdi.abbey.service.MusicService;
import com.paolovalerdi.abbey.util.Util;

public class AppWidgetBig extends BaseAppWidget {
    public static final String NAME = "app_widget_big";

    private static AppWidgetBig mInstance;

    public static synchronized AppWidgetBig getInstance() {
        if (mInstance == null) {
            mInstance = new AppWidgetBig();
        }
        return mInstance;
    }

    /**
     * Update all active widget instances by pushing changes
     */
    public void performUpdate(final MusicService service, final int[] appWidgetIds) {
        appWidgetView = new RemoteViews(service.getPackageName(), getLayout());

        // Set the titles and artwork
        setTitlesArtwork(service);

        // Set the buttons
        setButtons(service);

        // Link actions buttons to intents
        linkButtons(service);

        // Load the album cover async and push the update on completion
        Point p = Util.getScreenSize(service);

        final int widgetImageSize = Math.min(p.x, p.y);

        final Context appContext = service.getApplicationContext();

        service.runOnUiThread(() -> {
            if (target != null) {
                GlideApp.with(appContext).clear(target);
            }
            final Song song = service.getCurrentSong();
            GlideApp.with(appContext)
                    .asBitmap()
                    .load(AbbeyGlideExtension.getSongModel(song))
                    .transition(AbbeyGlideExtension.getDefaultTransition())
                    .songOptions(song)
                    .into(new AbbeySimpleTarget<Bitmap>(widgetImageSize, widgetImageSize) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> transition) {
                            update(bitmap);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            update(null);
                        }

                        private void update(@Nullable Bitmap bitmap) {
                            if (bitmap == null) {
                                appWidgetView.setImageViewResource(R.id.image, R.drawable.default_album_art);
                            } else {
                                appWidgetView.setImageViewBitmap(R.id.image, bitmap);
                            }
                            pushUpdate(appContext, appWidgetIds);
                        }
                    });
        });
    }

    public int getLayout() {
        return R.layout.app_widget_big;
    }

    public int getId() {
        return R.id.app_widget_big;
    }

    public int getImageSize(MusicService service) {
        return 0;
    }

    public float getCardRadius(MusicService service) {
        return 0;
    }
}
