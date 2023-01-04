package com.example.tac.data.login

import android.content.Context
import android.util.Log
import com.example.tac.data.Constants.CLIENT_ID
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


class GoogleTokenService(private val context: Context) {
    private val TAG = "Google Token Service"
    private val BASE_URL = "https://oauth2.googleapis.com"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit
        .Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    val googleTokenClient by lazy { retrofit.create(GoogleTokenClientInterface::class.java) }

    val oAuthDataStore = OAuthDataStore(context)


    suspend fun callGoogleOAuth2Server(code: String) {
        try {
            Log.e(TAG, "boutta call and exchange code for token")
            val response = googleTokenClient.callGoogleOAuth2Server(code = code, CLIENT_ID)
            Log.e(TAG, "GOT A CODE: ${response.access_token}")
            oAuthDataStore.saveTokenToTokenDataStore(context, response.access_token)
        } catch (e: Exception) {
            Log.e(TAG, "NO BUENO: ${e.localizedMessage}")
        }
    }
}

interface GoogleTokenClientInterface {
    @FormUrlEncoded
    @POST("/token")
    suspend fun callGoogleOAuth2Server(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("redirect_uri") redirectUri: String = "com.example.tac.authorized:/oauth2redirect"
    ): GoogleOAuth2ServerResponse
}