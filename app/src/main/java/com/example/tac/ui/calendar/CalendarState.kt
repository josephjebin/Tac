package com.example.tac.ui.calendar

import androidx.compose.runtime.MutableState
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
//    var scheduleWidth: Dp = 0.dp
//)