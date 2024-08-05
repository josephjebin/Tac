package com.jebkit.tac.data.calendar

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.google.api.services.calendar.model.Event
import com.jebkit.tac.ui.theme.akiflow_lavender
import java.time.Duration
import java.time.ZonedDateTime


data class EventDao(
    override val id: String,
    override val title: MutableState<String>,
    val busy: MutableState<Boolean>,
    override val description: MutableState<String?>,
    override val start: MutableState<ZonedDateTime>,
    override val end: MutableState<ZonedDateTime>,
    override val duration: MutableIntState,
    override val color: MutableState<Color>
): Plan(
    id = id,
    title = title,
    description = description,
    start = start,
    end = end,
    duration = duration,
    color = color
) {
    constructor(googleEvent: Event): this(
        id = googleEvent.id,
        title = mutableStateOf(googleEvent.summary),
        busy = mutableStateOf(googleEvent.transparency == "opaque"),
        description = mutableStateOf(googleEvent.description),
        start = mutableStateOf(ZonedDateTime.parse(googleEvent.start.dateTime.toString(), googleCalendarDateTimeFormat)),
        end =  mutableStateOf(ZonedDateTime.parse(googleEvent.end.dateTime.toString(), googleCalendarDateTimeFormat)),
        duration = mutableIntStateOf(30),
        color = mutableStateOf(akiflow_lavender)
    ) {
        duration.intValue = Duration.between(start.value, end.value).toMinutes().toInt()
    }
}