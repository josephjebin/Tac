package com.jebkit.tac.ui.calendar

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.ScheduledTask
import java.time.LocalDate

//data class CalendarState(
//    val calendars: List<GoogleCalendar> = listOf(),
//    val events: MutableState<List<EventDao>> = mutableStateOf(listOf()),
//    val scheduledTasks: MutableState<List<ScheduledTask>> = mutableStateOf(listOf()),
//    var selectedDate: LocalDate = LocalDate.now(),
//    var dateRangeEnd: LocalDate = selectedDate.plusWeeks(1),
//)

data class CalendarState(
    var selectedDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now()),
    val dateRangeEnd: MutableState<LocalDate> = mutableStateOf(LocalDate.now().plusWeeks(1)),

    val googleCalendarState: MutableState<GoogleCalendarState> = mutableStateOf(GoogleCalendarState.Success())
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