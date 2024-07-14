package com.jebkit.tac.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.jebkit.tac.ui.calendar.CalendarViewModel

@Suppress("UNCHECKED_CAST")
class TasksViewModelFactory(private val credential: GoogleAccountCredential): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TasksViewModel::class.java))
            return TasksViewModel(credential) as T
        throw IllegalArgumentException("Tasks View Model not found.")
    }
}