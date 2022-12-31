package com.example.tac.ui.login

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tac.MainActivity
import android.util.Log

class AuthorizeActivity : ComponentActivity() {
    private val TAG = "Authorize Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AUTHORIZED")
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