package com.example.tac.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tac.MainActivity
import com.example.tac.data.login.OAuthToken
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

    companion object {
        private const val TAG = "LoginActivity"
        /***********************************************************
         * Attributes
         */
        /**
         * You client id, you have it from the google console when you register your project
         * https://console.developers.google.com/a
         */
        private const val CLIENT_ID =
            "518856815930-io2di0gci3l6b4o9ft1fvg67qc47c5q6.apps.googleusercontent.com"

        /**
         * The redirect uri you have define in your google console for your project
         */
        private const val REDIRECT_URI =
            "com.example.tac:/oauth2redirect"
        /**
         * The redirect root uri you have define in your google console for your project
         * It is also the scheme your Main Activity will react
         */
        private const val REDIRECT_URI_ROOT = "com.example.tac.platform.oauth.redirect_auth_code"

        /**
         * You are asking to use a code when autorizing
         */
        private const val CODE = "code"

        /**
         * You are receiving an error when autorizing, it's embedded in this field
         */
        private const val ERROR_CODE = "error"

        /**
         * GrantType:You are using a code when retrieveing the token
         */
        private const val GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code"

        /**
         * GrantType:You are using a refresh_token when retrieveing the token
         */
        const val GRANT_TYPE_REFRESH_TOKEN = "refresh_token"

        /**
         * The scope: what do we want to use
         * Here we want to be able to do anything on the user's GDrive
         */
        const val API_SCOPE = "https://www.googleapis.com/auth/tasks"
    }
}