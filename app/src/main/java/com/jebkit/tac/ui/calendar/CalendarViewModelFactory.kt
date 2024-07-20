package com.jebkit.tac.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.jebkit.tac.ui.tasksAndCalendar.TasksAndCalendarViewModel

@Suppress("UNCHECKED_CAST")
class CalendarViewModelFactory(private val credential: GoogleAccountCredential): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TasksAndCalendarViewModel::class.java))
            return TasksAndCalendarViewModel(credential) as T
        throw IllegalArgumentException("Calendar View Model not found.")
    }
}