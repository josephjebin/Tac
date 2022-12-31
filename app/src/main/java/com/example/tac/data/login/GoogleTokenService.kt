package com.example.tac.data.login

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


class GoogleTokenService {
    private val BASE_URL = "https://oauth2.googleapis.com/token"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val googleTokenClient by lazy { retrofit.create(GoogleTokenClientInterface::class.java)}

    fun getToken(): AccessToken {
        try {
            val accessToken = googleTokenClient.getAccessToken()

        } catch (e: Exception) {

        }
    }
}

interface GoogleTokenClientInterface {
    @GET
    fun getAccessToken(): AccessToken
}

