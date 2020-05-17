package com.paolovalerdi.abbey.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.bumptech.glide.signature.ObjectKey;
import com.paolovalerdi.abbey.App;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class ArtistSignatureUtil {
    private static final String ARTIST_SIGNATURE_PREFS = "artist_signatures";

    private static ArtistSignatureUtil sInstance;

    private final SharedPreferences mPreferences;

    private ArtistSignatureUtil() {
        mPreferences = App.Companion.getStaticContext().getSharedPreferences(ARTIST_SIGNATURE_PREFS, Context.MODE_PRIVATE);
    }

    public static ArtistSignatureUtil getInstance() {
        if (sInstance == null) {
            sInstance = new ArtistSignatureUtil();
        }
        return sInstance;
    }

    void updateArtistSignature(String artistName) {
        mPreferences.edit().putLong(artistName, System.currentTimeMillis()).apply();
    }

    private long getArtistSignatureRaw(String artistName) {
        return mPreferences.getLong(artistName, 0);
    }

    public ObjectKey getArtistSignature(String artistName) {
        return new ObjectKey(String.valueOf(getArtistSignatureRaw(artistName)));
    }

}
