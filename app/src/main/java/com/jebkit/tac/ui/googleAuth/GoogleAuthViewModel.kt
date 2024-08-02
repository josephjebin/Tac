package com.jebkit.tac.ui.googleAuth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

class GoogleAuthViewModel(googleAccountCredential: GoogleAccountCredential): ViewModel() {
    var googleAccountCredential: GoogleAccountCredential by mutableStateOf(googleAccountCredential)
        private set

    var gmail: String by mutableStateOf("user")

    fun setAccountName(accountName: String) {
        googleAccountCredential.setSelectedAccountName(accountName)
        gmail = accountName
    }

    fun logout(defaultGoogleCredential: GoogleAccountCredential) {
        googleAccountCredential = defaultGoogleCredential
        gmail = "user"
    }
}