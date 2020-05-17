package com.paolovalerdi.abbey.network.deezer

import com.google.gson.annotations.Expose

/**
 * @author Paolo Valerdi
 */
data class DeezerResponse(
    val data: List<Data>,
    @Transient val next: String,
    @Transient val total: Int
)