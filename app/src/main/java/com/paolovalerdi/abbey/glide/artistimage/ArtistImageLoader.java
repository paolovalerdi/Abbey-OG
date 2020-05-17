package com.paolovalerdi.abbey.glide.artistimage;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;
import com.paolovalerdi.abbey.network.deezer.DeezerRestService;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */

public class ArtistImageLoader implements ModelLoader<ArtistImage, InputStream> {
    // we need these very low values to make sure our artist image loading calls doesn't block the image loading queue
    private static final int TIMEOUT = 500;

    private Context context;
    private DeezerRestService deezerApiService;
    private OkHttpClient okhttp;

    private ArtistImageLoader(Context context, DeezerRestService deezerApiService, OkHttpClient okhttp) {
        this.context = context;
        this.deezerApiService = deezerApiService;
        this.okhttp = okhttp;
    }

    @Override
    public LoadData<InputStream> buildLoadData(@NonNull ArtistImage model, int width, int height,
                                               @NonNull Options options) {
        return new LoadData<>(new ObjectKey(model.artistName), new ArtistImageFetcher(context, deezerApiService, okhttp, model));
    }

    @Override
    public boolean handles(@NonNull ArtistImage model) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<ArtistImage, InputStream> {
        private DeezerRestService deezerApiClient;
        private Context context;
        private OkHttpClient okHttp;

        public Factory(Context context) {
            this.context = context;
            okHttp = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();

            deezerApiClient = DeezerRestService.Companion.invoke(
                    DeezerRestService.Companion.createDefaultOkHttpClient(context)
                            .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                            .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                            .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                            .build()
            );
        }

        @Override
        @NonNull
        public ModelLoader<ArtistImage, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ArtistImageLoader(context, deezerApiClient, okHttp);
        }

        @Override
        public void teardown() {
        }
    }
}

