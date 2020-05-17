package com.paolovalerdi.abbey.lastfm.rest.model


import com.google.gson.annotations.SerializedName

data class LastFmSimilarArtist(
    @SerializedName("similarartists")
    val similarartists: Similarartists
)

data class Similarartists(
    @SerializedName("artist")
    val artist: List<Artist>
)

data class Artist(
    @SerializedName("name")
    val name: String
)

