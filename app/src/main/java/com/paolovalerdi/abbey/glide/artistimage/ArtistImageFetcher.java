package com.paolovalerdi.abbey.glide.artistimage;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.paolovalerdi.abbey.network.deezer.DeezerResponse;
import com.paolovalerdi.abbey.network.deezer.DeezerRestService;
import com.paolovalerdi.abbey.util.MusicUtil;
import com.paolovalerdi.abbey.util.preferences.PreferenceUtil;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class ArtistImageFetcher implements DataFetcher<InputStream> {

    public static final String TAG = ArtistImageFetcher.class.getSimpleName();
    private Context context;
    private final DeezerRestService deezerApiService;
    private final ArtistImage model;
    private volatile boolean isCancelled;
    private Call<DeezerResponse> call;
    private OkHttpClient okhttp;
    private OkHttpStreamFetcher streamFetcher;

    ArtistImageFetcher(Context context, DeezerRestService lastFMRestClient, OkHttpClient okhttp, ArtistImage model) {
        this.context = context;
        this.deezerApiService = lastFMRestClient;
        this.okhttp = okhttp;
        this.model = model;
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        try {
            if (!MusicUtil.isArtistNameUnknown(model.artistName) && PreferenceUtil.INSTANCE.isAllowedToDownloadMetaData()) {
                call = deezerApiService.getArtistImage(model.artistName);
                call.enqueue(new Callback<DeezerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<DeezerResponse> call, @NonNull Response<DeezerResponse> response) {
                        if (isCancelled) {
                            callback.onDataReady(null);
                            return;
                        }

                        try {
                            DeezerResponse deezerResponse = response.body();
                            String url = deezerResponse.getData().get(0).getPictureXl();
                            streamFetcher = new OkHttpStreamFetcher(okhttp, new GlideUrl(url));
                            streamFetcher.loadData(priority, callback);
                        } catch (Exception e) {
                            callback.onLoadFailed(new Exception("No artist image url found"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DeezerResponse> call, @NonNull Throwable throwable) {
                        callback.onLoadFailed(new Exception(throwable));
                    }
                });
            }
        } catch (Exception e) {
            callback.onLoadFailed(e);
        }
    }

    @Override
    public void cleanup() {
        if (streamFetcher != null) {
            streamFetcher.cleanup();
        }
    }

    @Override
    public void cancel() {
        isCancelled = true;
        if (call != null) {
            call.cancel();
        }
        if (streamFetcher != null) {
            streamFetcher.cancel();
        }
    }

}
