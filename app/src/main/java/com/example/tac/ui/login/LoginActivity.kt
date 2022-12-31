package com.example.tac.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import com.example.tac.MainActivity
import com.example.tac.data.OAuthServerInterface
import com.example.tac.data.OAuthToken
import com.example.tac.data.RetrofitBuilder
import okhttp3.HttpUrl

class LoginActivity : ComponentActivity() {
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
        //You can be created either because your user start your application
        //        <intent-filter>
        //            <action android:name="android.intent.action.MAIN" />
        //            <category android:name="android.intent.category.LAUNCHER" />
        //        </intent-filter>
        //either because the callBack of the Authorisation is called :
        //        <intent-filter>
        //            <action android:name="android.intent.action.VIEW" />
        //            <category android:name="android.intent.category.BROWSABLE" />
        //            <category android:name="android.intent.category.DEFAULT" />
        //            <data android:scheme="com.renaultnissan.acms.platform.oauth.githubsample" />
        //        </intent-filter>

        val data = Intent.getIntentOld(null).data

        //Manage the callback case:
        if (data != null && !TextUtils.isEmpty(data.scheme)) {
            if (REDIRECT_URI_ROOT == data.scheme) {
                authorizationCode = data.getQueryParameter(CODE)
                authorizationError = data.getQueryParameter(ERROR_CODE)
                Log.e(
                    TAG,
                    "onCreate: handle result of authorization with code :$authorizationCode"
                )
                if (!TextUtils.isEmpty(authorizationCode)) {
                    tokenFormUrl
                }
                if (!TextUtils.isEmpty(authorizationError)) {
                    //a problem occurs, the user reject our granting request or something like that
                    Toast.makeText(this, "WHY DID YOU CANCEL AUTHORIZING", Toast.LENGTH_LONG)
                        .show()
                    Log.e(
                        TAG,
                        "onCreate: handle result of authorization with error :$authorizationError"
                    )
                    //then die
                    finish()
                }
            }
        }

        //Manage the start application case:
        else {
            //If you don't have a token yet or if your token has expired, ask for it
            val oauthToken: OAuthToken? = OAuthToken.Factory.create()
            if (oauthToken == null || oauthToken.accessToken == null) {
                //first case==first token request
                if (oauthToken == null || oauthToken.refreshToken == null) {
                    Log.e(TAG, "onCreate: Launching authorization (first step)")
                    //first step of OAUth: the authorization step
                    MakeAuthorizationRequest()
                } else {
                    Log.e(
                        TAG,
                        "onCreate: refreshing the token :$oauthToken"
                    )
                    //refresh token case
                    refreshTokenFormUrl(oauthToken)
                }
            } else {
                Log.e(TAG, "onCreate: Token available, just launch MainActivity")
                startMainActivity(false)
            }
        }
    }
    /***********************************************************
     * Managing Authotization and Token process
     */
    /**
     * Make the Authorization request
     */
    @Composable
    fun MakeAuthorizationRequest() {
        val authorizeUrl = HttpUrl
            .Builder()
            .host("https://accounts.google.com/o/oauth2/v2/auth")
            .addQueryParameter("client_id", CLIENT_ID)
            .addQueryParameter("scope", API_SCOPE)
            .addQueryParameter("redirect_uri", REDIRECT_URI)
            .addQueryParameter("response_type", CODE)
            .build()

        Intent(Intent.ACTION_VIEW).apply {
            Log.e(TAG, "the url is : ${authorizeUrl.toString()}")
            data = Uri.parse(authorizeUrl.toString())
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            LocalContext.current.startActivity(this)
        }

        finish()
    }

    /**
     * Refresh the OAuth token
     */
    private fun refreshTokenFormUrl(oauthToken: OAuthToken) {
        val oAuthServer: OAuthServerInterface = RetrofitBuilder.getSimpleClient(this)
        val refreshTokenFormCall: Call<OAuthToken> = oAuthServer.refreshTokenForm(
            oauthToken.getRefreshToken(),
            CLIENT_ID,
            GRANT_TYPE_REFRESH_TOKEN
        )
        refreshTokenFormCall.enqueue(object : Callback<OAuthToken?>() {
            fun onResponse(call: Call<OAuthToken?>?, response: Response<OAuthToken?>) {
                Log.e(TAG, "===============New Call==========================")
                Log.e(TAG,
                    "The call refreshTokenFormUrl succeed with code=" + response.code()
                        .toString() + " and has body = " + response.body()
                )
                //ok we have the token
                response.body().save()
                startMainActivity(true)
            }

            fun onFailure(call: Call<OAuthToken?>?, t: Throwable?) {
                Log.e(TAG, "===============New Call==========================")
                Log.e(TAG, "The call refreshTokenFormCall failed", t)
            }
        })
    }//ok we have the token

    /**
     * Retrieve the OAuth token
     */
    private val tokenFormUrl: Unit
        private get() {
            val oAuthServer: OAuthServerInterface = RetrofitBuilder.getSimpleClient(this)
            val getRequestTokenFormCall: Call<OAuthToken> = oAuthServer.requestTokenForm(
                authorizationCode,
                CLIENT_ID,
                REDIRECT_URI,
                GRANT_TYPE_AUTHORIZATION_CODE
            )
            getRequestTokenFormCall.enqueue(object : Callback<OAuthToken?>() {
                fun onResponse(call: Call<OAuthToken?>?, response: Response<OAuthToken?>) {
                    Log.e(TAG, "===============New Call==========================")
                    Log.e(TAG,
                        "The call getRequestTokenFormCall succeed with code=" + response.code()
                            .toString() + " and has body = " + response.body()
                    )
                    //ok we have the token
                    response.body().save()
                    startMainActivity(true)
                }

                fun onFailure(call: Call<OAuthToken?>?, t: Throwable?) {
                    Log.e(TAG, "===============New Call==========================")
                    Log.e(TAG, "The call getRequestTokenFormCall failed", t)
                }
            })
        }
    /***********************************************************
     * Others business methods
     */
    /**
     * Start the next activity
     */
    private fun startMainActivity(newtask: Boolean) {
        val i = Intent(this, MainActivity::class.java)
        if (newtask) {
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        ContextCompat.startActivity(i)
        //you can die so
        finish()
    }

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
            "1020597890643-d74gsb0vrp27l3sau8ia0u57lc2i9v5r.apps.googleusercontent.com"

        /**
         * The redirect uri you have define in your google console for your project
         */
        private const val REDIRECT_URI =
            "com.renaultnissan.acms.platform.oauth.githubsample:/oauth2redirect"

        /**
         * The redirect root uri you have define in your google console for your project
         * It is also the scheme your Main Activity will react
         */
        private const val REDIRECT_URI_ROOT = "com.renaultnissan.acms.platform.oauth.githubsample"

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
        const val API_SCOPE = "https://www.googleapis.com/auth/drive"
    }
}