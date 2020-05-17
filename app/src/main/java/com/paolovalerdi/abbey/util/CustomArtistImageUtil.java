package com.paolovalerdi.abbey.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;
import com.paolovalerdi.abbey.App;
import com.paolovalerdi.abbey.glide.AbbeySimpleTarget;
import com.paolovalerdi.abbey.glide.GlideApp;
import com.paolovalerdi.abbey.glide.artistimage.ArtistImage;
import com.paolovalerdi.abbey.model.Artist;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */

public class CustomArtistImageUtil {
    private static final String CUSTOM_ARTIST_IMAGE_PREFS = "custom_artist_image";
    private static final String FOLDER_NAME = "/custom_artist_images/";

    private static CustomArtistImageUtil sInstance;

    private final SharedPreferences mPreferences;

    private CustomArtistImageUtil(@NonNull final Context context) {
        mPreferences = context.getApplicationContext().getSharedPreferences(CUSTOM_ARTIST_IMAGE_PREFS, Context.MODE_PRIVATE);
    }

    public static CustomArtistImageUtil getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new CustomArtistImageUtil(context.getApplicationContext());
        }
        return sInstance;
    }

    public void fetchAndSave(final Artist artist, final boolean forceDownload) {
        GlideApp.with(App.Companion.getInstance())
                .asBitmap()
                .load(new ArtistImage(artist.getName(), forceDownload))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)
                .into(new AbbeySimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        new AsyncTask<Void, Void, Void>() {
                            @SuppressLint("ApplySharedPref")
                            @Override
                            protected Void doInBackground(Void... params) {
                                File dir = new File(App.Companion.getInstance().getFilesDir(), FOLDER_NAME);
                                if (!dir.exists()) {
                                    if (!dir.mkdirs()) { // create the folder
                                        return null;
                                    }
                                }
                                File file = new File(dir, getFileName(artist));

                                boolean succesful = false;
                                try {
                                    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                                    succesful = ImageUtil.resizeBitmap(resource, 2048).compress(Bitmap.CompressFormat.JPEG, 100, os);
                                    os.close();
                                } catch (IOException e) {
                                    Toast.makeText(App.Companion.getInstance(), e.toString(), Toast.LENGTH_LONG).show();
                                }

                                if (succesful) {
                                    mPreferences.edit().putBoolean(getFileName(artist), true).commit();
                                    ArtistSignatureUtil.getInstance().updateArtistSignature(artist.getName());
                                    App.Companion.getInstance().getContentResolver().notifyChange(Uri.parse("content://media"), null); // trigger media store changed to force artist image forceLoad
                                }
                                return null;
                            }
                        }.execute();
                    }
                });
    }


    public void setCustomArtistImage(final Artist artist, Uri uri) {
        GlideApp.with(App.Companion.getInstance())
                .asBitmap()
                .load(uri)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .into(new AbbeySimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull final Bitmap resource, Transition<? super Bitmap> glideAnimation) {
                        new AsyncTask<Void, Void, Void>() {
                            @SuppressLint("ApplySharedPref")
                            @Override
                            protected Void doInBackground(Void... params) {
                                File dir = new File(App.Companion.getInstance().getFilesDir(), FOLDER_NAME);
                                if (!dir.exists()) {
                                    if (!dir.mkdirs()) { // create the folder
                                        return null;
                                    }
                                }
                                File file = new File(dir, getFileName(artist));

                                boolean succesful = false;
                                try {
                                    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                                    succesful = ImageUtil.resizeBitmap(resource, 2048).compress(Bitmap.CompressFormat.JPEG, 100, os);
                                    os.close();
                                } catch (IOException e) {
                                    Toast.makeText(App.Companion.getInstance(), e.toString(), Toast.LENGTH_LONG).show();
                                }

                                if (succesful) {
                                    mPreferences.edit().putBoolean(getFileName(artist), true).commit();
                                    ArtistSignatureUtil.getInstance().updateArtistSignature(artist.getName());
                                    App.Companion.getInstance().getContentResolver().notifyChange(Uri.parse("content://media"), null); // trigger media store changed to force artist image forceLoad
                                }
                                return null;
                            }
                        }.execute();
                    }
                });
    }

    public void resetCustomArtistImage(final Artist artist) {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("ApplySharedPref")
            @Override
            protected Void doInBackground(Void... params) {
                mPreferences.edit().putBoolean(getFileName(artist), false).commit();
                ArtistSignatureUtil.getInstance().updateArtistSignature(artist.getName());
                App.Companion.getInstance().getContentResolver().notifyChange(Uri.parse("content://media"), null); // trigger media store changed to force artist image forceLoad

                File file = getFile(artist);
                if (!file.exists()) {
                    return null;
                } else {
                    file.delete();
                }
                return null;
            }
        }.execute();
    }

    // shared prefs saves us many IO operations
    public boolean hasCustomArtistImage(Artist artist) {
        return mPreferences.getBoolean(getFileName(artist), false);
    }

    private static String getFileName(Artist artist) {
        String artistName = artist.getName();
        if (artistName == null)
            artistName = "";
        // replace everything that is not a letter or a number with _
        artistName = artistName.replaceAll("[^a-zA-Z0-9]", "_");
        return String.format(Locale.US, "#%d#%s.jpeg", artist.getId(), artistName);
    }

    public static File getFile(Artist artist) {
        File dir = new File(App.Companion.getInstance().getFilesDir(), FOLDER_NAME);
        return new File(dir, getFileName(artist));
    }
}
