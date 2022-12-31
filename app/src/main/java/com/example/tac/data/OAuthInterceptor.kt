package com.example.tac.data

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.example.tac.MainActivity
import com.example.tac.data.OAuthToken.Factory.create
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


/**
 * Created by Mathias Seguy - Android2EE on 05/01/2017.
 * This class aims to add automaticly in the Header the OAuth token
 */
class OAuthInterceptor : Interceptor {
    private var accessToken: String? = null
    private var accessTokenType: String? = null

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //find the token
        val oauthToken = create()
        accessToken = oauthToken!!.accessToken
        accessTokenType = oauthToken.tokenType
        //add it to the request
        val builder: Builder = chain.request().newBuilder()
        if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(accessTokenType)) {
            Log.e(
                TAG,
                "In the interceptor adding the header authorization with : $accessTokenType $accessToken"
            )
            builder.header("Authorization", "$accessTokenType $accessToken")
        } else {
            Log.e(
                TAG,
                "In the interceptor there is a fuck with : $accessTokenType $accessToken"
            )
            //you should launch the loginActivity to fix that:
            val i = Intent(MyApplication.instance, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            MyApplication.instance.startActivity(i)
        }
        //proceed to the call
        return chain.proceed(builder.build())
    }

    companion object {
        private const val TAG = "OAuthInterceptor"
    }
}