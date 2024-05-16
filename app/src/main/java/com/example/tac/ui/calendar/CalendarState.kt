package com.example.tac.ui.calendar

import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.GoogleCalendar
import com.example.tac.data.calendar.ScheduledTask
import java.time.ZonedDateTime

data class CalendarState (
    val calendars: List<GoogleCalendar>,
    val events: List<EventDao>,
    val scheduledTasks: MutableList<ScheduledTask>,
    var selectedDate: ZonedDateTime = ZonedDateTime.now(),
    var dateRangeEnd: ZonedDateTime = selectedDate.plusWeeks(1)
)