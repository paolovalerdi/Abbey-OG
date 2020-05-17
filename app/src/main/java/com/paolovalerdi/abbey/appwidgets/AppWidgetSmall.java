package com.paolovalerdi.abbey.appwidgets;

import android.text.TextUtils;
import android.widget.RemoteViews;

import com.paolovalerdi.abbey.R;
import com.paolovalerdi.abbey.appwidgets.base.BaseAppWidget;
import com.paolovalerdi.abbey.model.Song;
import com.paolovalerdi.abbey.service.MusicService;

public class AppWidgetSmall extends BaseAppWidget {
    public static final String NAME = "app_widget_small";

    private static AppWidgetSmall mInstance;

    public static synchronized AppWidgetSmall getInstance() {
        if (mInstance == null) {
            mInstance = new AppWidgetSmall();
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

        final Song song = service.getCurrentSong();

        if (!(TextUtils.isEmpty(song.title) && TextUtils.isEmpty(song.artistName)) &&
                TextUtils.isEmpty(song.title) || TextUtils.isEmpty(song.artistName)) {
            appWidgetView.setTextViewText(R.id.text_separator, "");
        } else {
            appWidgetView.setTextViewText(R.id.text_separator, "•");
        }

        // Link actions buttons to intents
        linkButtons(service);

        // Load the album cover async and push the update on completion
        loadAlbumCover(service, appWidgetIds);
    }

    public int getLayout() {
        return R.layout.app_widget_small;
    }

    public int getId() {
        return R.id.app_widget_small;
    }

    public int getImageSize(final MusicService service) {
        return service.getResources().getDimensionPixelSize(R.dimen.app_widget_small_image_size);
    }

    public float getCardRadius(final MusicService service) {
        return service.getResources().getDimension(R.dimen.app_widget_card_radius);
    }
}
