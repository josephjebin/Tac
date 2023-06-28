package com.example.tac.ui.calendar

import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.GoogleCalendar
import java.time.ZonedDateTime

data class CalendarState (
    val calendars: List<GoogleCalendar>,
    val events: List<EventDao>,
    var selectedDate: ZonedDateTime = ZonedDateTime.now(),
    var constantMaxDate: ZonedDateTime = selectedDate.plusWeeks(1)
)