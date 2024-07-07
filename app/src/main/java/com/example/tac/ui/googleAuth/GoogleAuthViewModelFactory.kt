package com.example.tac.ui.googleAuth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

@Suppress("UNCHECKED_CAST")
class GoogleAuthViewModelFactory(private val googleAccountCredential: GoogleAccountCredential): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GoogleAuthViewModel::class.java))
            return GoogleAuthViewModel(googleAccountCredential) as T
        throw IllegalArgumentException("Google Auth View Model not found.")
    }
}