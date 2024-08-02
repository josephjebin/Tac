package com.jebkit.tac.ui.calendar

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.jebkit.tac.ui.tasksAndCalendar.TasksAndCalendarViewModel

@Suppress("UNCHECKED_CAST")
class CalendarViewModelFactory(
    private val credential: GoogleAccountCredential,
    private val userRecoverableLauncher: ActivityResultLauncher<Intent>
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TasksAndCalendarViewModel::class.java))
            return TasksAndCalendarViewModel(credential, userRecoverableLauncher) as T
        throw IllegalArgumentException("Calendar View Model not found.")
    }
}