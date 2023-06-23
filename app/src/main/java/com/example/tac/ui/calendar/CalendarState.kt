package com.example.tac.ui.calendar

import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.GoogleCalendar
import com.example.tac.data.calendar.GoogleEvent
import com.google.api.services.calendar.model.Event
import java.time.LocalDate

data class CalendarState (
    val calendars: List<GoogleCalendar>,
    val events: List<Event>,
    var selectedDate: LocalDate = LocalDate.now()
)