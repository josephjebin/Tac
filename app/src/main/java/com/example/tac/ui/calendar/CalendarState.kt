package com.example.tac.ui.calendar

import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.GoogleCalendar
import com.example.tac.data.calendar.ScheduledTask
import java.time.LocalDate

data class CalendarState(
    val calendars: List<GoogleCalendar>,
    val events: MutableState<MutableList<EventDao>>,
    val scheduledTasks: MutableList<ScheduledTask>,
    var selectedDate: LocalDate = LocalDate.now(),
    var dateRangeEnd: LocalDate = selectedDate.plusWeeks(1),
    var scheduleWidth: Dp = 0.dp
)