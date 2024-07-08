package com.example.tac.ui.calendar

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.GoogleCalendar
import com.example.tac.data.calendar.ScheduledTask
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

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
        val experiment = mutableStateListOf<EventDao>()
        val calendars: MutableState<List<GoogleCalendar>> = mutableStateOf(listOf()),
        var events: MutableState<List<EventDao>> = mutableStateOf(listOf()),
        val scheduledTasks: MutableState<List<ScheduledTask>> = mutableStateOf(listOf()),
    ) : GoogleCalendarState

    data class Error(val exception: Exception) : GoogleCalendarState
}