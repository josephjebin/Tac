package com.example.tac.data.login

import android.content.Context
import com.example.tac.data.Constants.CLIENT_ID
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


class GoogleTokenService(private val context: Context) {
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

    val oAuthDataStore = OAuthDataStore(context)

    suspend fun callGoogleOAuth2Server(code: String) {
        try {
            val oAuth2ServerResponse = googleTokenClient.callGoogleOAuth2Server(code, CLIENT_ID)
            oAuthDataStore.saveTokenToTokenDataStore(context, oAuth2ServerResponse.access_token)
        } catch (e: Exception) {  }
    }
}

interface GoogleTokenClientInterface {
    @FormUrlEncoded
    @POST
    fun callGoogleOAuth2Server(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("redirect_uri") redirectUri: String = "com.example.tac.authorized:/oauth2redirect"
    ): GoogleOAuth2ServerResponse
}

