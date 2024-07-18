package com.jebkit.tac.ui.calendar

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.ui.task.GoogleTasksState
import java.time.LocalDate

//Tasks and calendar data are stored together because they both are dependent on the selected dates
data class TasksAndCalendarState(
    val minSelectedDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now()),
    val maxSelectedDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now()),
    val minBufferDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now()),
    //maxBufferDate will also represent maxTasksDate
    val maxBufferDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now()),
    val minTasksAndEventsDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now()),
    val maxEventsDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now()),
    val googleCalendarState: MutableState<GoogleCalendarState> = mutableStateOf(GoogleCalendarState.Success()),
    val googleTasksState: MutableState<GoogleTasksState> = mutableStateOf(GoogleTasksState())
)

sealed interface GoogleCalendarState {
    data class Success(
        //need to add Calendar data type. stub with event dao for now
        val calendars: SnapshotStateList<EventDao> = mutableStateListOf(),
        val events: SnapshotStateList<EventDao> = mutableStateListOf(),
        val scheduledTasks: SnapshotStateList<ScheduledTask> = mutableStateListOf()
    ) : GoogleCalendarState

    data class Error(val exception: Exception) : GoogleCalendarState
}