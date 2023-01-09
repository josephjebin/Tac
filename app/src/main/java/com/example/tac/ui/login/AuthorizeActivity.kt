package com.example.tac.ui.login

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tac.MainActivity
import com.example.tac.data.login.GoogleTokenService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthorizeActivity : ComponentActivity() {
    private val TAG = "Authorize Activity"

    /**
     * The code returned by the server at the authorization's first step
     */
    private var code: String? = null

    /**
     * The error returned by the server at the authorization's first step
     */
    private var error: String? = null

    private lateinit var googleTokenService: GoogleTokenService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AUTHORIZED")

        val data: Uri? = intent.data

//        if (data != null && !TextUtils.isEmpty(data.scheme)) {
//            if (REDIRECT_URI_ROOT == data.scheme) {
//                code = data.getQueryParameter(CODE);
//                error=data.getQueryParameter(ERROR_CODE);
//                Log.e(TAG, "onCreate: handle result of authorization with code :$code");
//                if (!TextUtils.isEmpty(code)) {
//                    googleTokenService = GoogleTokenService(this)
//                    CoroutineScope(Dispatchers.IO).launch {
//                        code?.let { googleTokenService.callGoogleOAuth2Server(it) }
//                    }
////                    val intent = Intent(this, MainActivity::class.java)
////                    startActivity(intent)
//                }
//                if(!TextUtils.isEmpty(error)) {
//                    //a problem occurs, the user reject our granting request or something like that
//                    Toast.makeText(this, "ERROR AUTHORIZING",Toast.LENGTH_LONG).show();
//                    Log.e(TAG, "onCreate: handle result of authorization with error :$error");
//                    //then die
//                    finish()
//                }
//            }
//        }


        setContent {
            val i = Intent(this, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            i.addFlags(FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(i)
            //you can die so
            finish()
        }
    }
}