package com.example.tac.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.tac.data.Constants.API_SCOPE
import com.example.tac.data.Constants.CLIENT_ID
import com.example.tac.data.Constants.CODE
import com.example.tac.data.Constants.REDIRECT_URI
import okhttp3.HttpUrl

class LoginActivity : ComponentActivity() {
    private val TAG = "LoginActivity"

    /**
     * The code returned by the server at the authorization's first step
     */
    private var authorizationCode: String? = null

    /**
     * The error returned by the server at the authorization's first step
     */
    private var authorizationError: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeAuthorizationRequest()
    }

    /***********************************************************
     * Managing Authotization and Token process
     */
    /**
     * Make the Authorization request
     */
    private fun makeAuthorizationRequest() {
        val authorizeUrl = HttpUrl
            .Builder()
            .scheme("https")
            .host("accounts.google.com")
            .addPathSegments("o/oauth2/v2/auth")
            .addQueryParameter("client_id", CLIENT_ID)
            .addQueryParameter("scope", API_SCOPE)
            .addQueryParameter("redirect_uri", REDIRECT_URI)
            .addQueryParameter("response_type", CODE)
            .build()

        val intent = Intent(Intent.ACTION_VIEW).apply {
            Log.e(TAG, "the url is : $authorizeUrl")
            data = Uri.parse(authorizeUrl.toString())
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        applicationContext.startActivity(intent)

        finish()
    }

//    private fun startMainActivity(newtask: Boolean) {
//        val i = Intent(this, MainActivity::class.java)
//        if (newtask) {
//            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        applicationContext.startActivity(i)
//        //you can die so
//        finish()
//    }
}