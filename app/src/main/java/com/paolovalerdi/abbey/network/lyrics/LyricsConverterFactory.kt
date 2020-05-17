package com.paolovalerdi.abbey.network.lyrics

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class LyricsConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, String>? = if (String::class.java == type) {
        Converter { value -> value.string() }
    } else null

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? = if (String::class.java == type) {
        Converter<String, RequestBody> { value -> RequestBody.create(MediaType.parse("text/plain"), value) }
    } else null

}