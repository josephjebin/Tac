package com.example.tac.data

import android.content.Context
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File


/**
 * Created by Mathias Seguy - Android2EE on 05/01/2017.
 */
object RetrofitBuilder {
    /***********************************************************
     * Constants
     */
    /**
     * Root URL
     * (always ends with a /)
     */
    const val BASE_URL = "https://www.googleapis.com/"
    /***********************************************************
     * Getting OAuthServerIntf instance using Retrofit creation
     */
    /**
     * A basic client to make unauthenticated calls
     * @param ctx
     * @return OAuthServerIntf instance
     */
    fun getSimpleClient(ctx: Context): OAuthServerInterface {
        //Using Default HttpClient
        val retrofit = Retrofit.Builder()
            .client(getSimpleOkHttpClient(ctx))
            .addConverterFactory(StringConverterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
        return retrofit.create(OAuthServerInterface::class.java)
    }

    /**
     * An autenticated client to make authenticated calls
     * The token is automaticly added in the Header of the request
     * @param ctx
     * @return OAuthServerIntf instance
     */
    fun getOAuthClient(ctx: Context): OAuthServerInterface {
        // now it's using the cach
        // Using my HttpClient
        val raCustom: Retrofit = Builder()
            .client(getOAuthOkHttpClient(ctx))
            .baseUrl(BASE_URL)
            .addConverterFactory(StringConverterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return raCustom.create(OAuthServerInterface::class.java)
    }
    /***********************************************************
     * OkHttp Clients
     */
    /**
     * Return a simple OkHttpClient v:
     * have a cache
     * have a HttpLogger
     */
    @NonNull
    fun getSimpleOkHttpClient(ctx: Context): OkHttpClient {
        // Define the OkHttp Client with its cache!
        // Assigning a CacheDirectory
        val myCacheDir = File(ctx.cacheDir, "OkHttpCache")
        // You should create it...
        val cacheSize = 1024 * 1024
        val cacheDir = Cache(myCacheDir, cacheSize.toLong())
        val httpLogInterceptor = HttpLoggingInterceptor()
        httpLogInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return Builder() //add a cache
            .cache(cacheDir)
            .addInterceptor(httpLogInterceptor)
            .build()
    }

    /**
     * Return a OAuth OkHttpClient v:
     * have a cache
     * have a HttpLogger
     * add automaticly the token in the header of each request because of the oAuthInterceptor
     * @param ctx
     * @return
     */
    @NonNull
    fun getOAuthOkHttpClient(ctx: Context): OkHttpClient {
        // Define the OkHttp Client with its cache!
        // Assigning a CacheDirectory
        val myCacheDir = File(ctx.cacheDir, "OkHttpCache")
        // You should create it...
        val cacheSize = 1024 * 1024
        val cacheDir = Cache(myCacheDir, cacheSize.toLong())
        val oAuthInterceptor: Interceptor = OAuthInterceptor()
        val httpLogInterceptor = HttpLoggingInterceptor()
        httpLogInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return Builder()
            .cache(cacheDir)
            .addInterceptor(oAuthInterceptor)
            .addInterceptor(httpLogInterceptor)
            .build()
    }
}