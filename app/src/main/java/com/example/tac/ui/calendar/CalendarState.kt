package com.example.tac.ui.calendar

import com.example.tac.data.calendar.GoogleCalendar
import com.example.tac.data.calendar.GoogleEvent
import java.time.LocalDate

data class CalendarState (
    val calendars: List<GoogleCalendar>,
    val events: List<GoogleEvent>,
    var selectedDate: LocalDate = LocalDate.now()
)