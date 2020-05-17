package com.paolovalerdi.abbey.network.lyrics

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Paolo Valerdi
 */
interface LyricsRestService {

    @GET("/lyrics")
    suspend fun searchLyrics(
        @Query("artist") artist: String,
        @Query("title") title: String
    ): Response<String>

    companion object {

        private const val BASE_URL = "https://makeitpersonal.co"

        private const val CACHE_SIZE = (1024 * 1024).toLong()

        private val cacheHeader = String.format(
            "max-age=%d,max-stale=%d",
            60 * 60 * 24 * 7,
            Integer.valueOf(31536000)
        )

        operator fun invoke(context: Context): LyricsRestService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createClient(context))
            .addConverterFactory(LyricsConverterFactory())
            .build()
            .create()

        private fun createClient(context: Context) = OkHttpClient.Builder()
            .cache(Cache(context.applicationContext.cacheDir, CACHE_SIZE))
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .addHeader(
                            "Cache-Control",
                            cacheHeader
                        ).build()
                )
            }
            .build()

    }

}