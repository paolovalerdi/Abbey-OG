package com.paolovalerdi.abbey.glide;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.paolovalerdi.abbey.glide.artistimage.ArtistImage;
import com.paolovalerdi.abbey.glide.artistimage.ArtistImageLoader;
import com.paolovalerdi.abbey.glide.audiocover.AudioFileCover;
import com.paolovalerdi.abbey.glide.audiocover.AudioFileCoverLoader;
import com.paolovalerdi.abbey.glide.collageimage.CollageImage;
import com.paolovalerdi.abbey.glide.collageimage.PlaylistImageLoader;
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteTranscoder;
import com.paolovalerdi.abbey.glide.palette.BitmapPaletteWrapper;

import java.io.InputStream;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */

@GlideModule
public class AbbeyGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                   @NonNull Registry registry) {
        registry.append(AudioFileCover.class, InputStream.class, new AudioFileCoverLoader.Factory());
        registry.append(ArtistImage.class, InputStream.class, new ArtistImageLoader.Factory(context));
        registry.append(CollageImage.class, InputStream.class, new PlaylistImageLoader.Factory(context));
        registry.register(Bitmap.class, BitmapPaletteWrapper.class, new BitmapPaletteTranscoder());
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

}
