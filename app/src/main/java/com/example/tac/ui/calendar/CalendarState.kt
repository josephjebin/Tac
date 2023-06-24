package com.example.tac.ui.calendar

import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.GoogleCalendar
import com.example.tac.data.calendar.GoogleEvent
import com.google.api.client.util.DateTime
import java.time.LocalDate
import java.util.*

data class CalendarState (
    val calendars: List<GoogleCalendar>,
    val events: List<EventDao>,
    var selectedDate: DateTime = DateTime(Date()),
    var constantMaxDate: DateTime = DateTime(Date())
)