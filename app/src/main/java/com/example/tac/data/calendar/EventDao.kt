package com.example.tac.data.calendar

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.google.api.services.calendar.model.Event
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class EventDao(
    override val id: String,
    override val title: MutableState<String>,
    val busy: MutableState<Boolean>,
    override val description: MutableState<String>,
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
    val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz")

    constructor(googleEvent: Event): this(
        id = googleEvent.id,
        title = mutableStateOf(googleEvent.summary) ,
        busy = mutableStateOf(googleEvent.transparency == "opaque"),
        description = mutableStateOf(googleEvent.description),
        start =  googleEvent.start.dateTime,

    )

}