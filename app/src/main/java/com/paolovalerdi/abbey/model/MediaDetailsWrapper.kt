package com.paolovalerdi.abbey.model

import android.text.Spanned

data class MediaDetailsWrapper(
    val songs: List<Song>,
    val albums: List<Album>? = null,
    val headerText: Spanned? = null
)