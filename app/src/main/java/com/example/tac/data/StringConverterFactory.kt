package com.example.tac.data

import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


/**
 * Created by Mathias Seguy - Android2EE on 05/01/2017.
 * Updated by Jebin Joseph on 12/30/2022
 */
class StringConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (String::class.java == type) {
            Converter<ResponseBody, Any> { responseBody -> responseBody.string() }
        } else null
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return if (String::class.java == type) {
            Converter<String?, RequestBody> { value ->
                value.toRequestBody("text/plain".toMediaTypeOrNull())
            }
        } else null
    }
}