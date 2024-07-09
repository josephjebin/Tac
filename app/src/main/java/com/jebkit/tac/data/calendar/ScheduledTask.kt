package com.jebkit.tac.data.calendar

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.jebkit.tac.ui.theme.onSurfaceGray
import com.google.api.services.calendar.model.Event
import java.time.Duration
import java.time.ZonedDateTime


data class ScheduledTask(
    override val id: String,
    override val title: MutableState<String>,
    val parentTaskId: String? = null,
    override val description: MutableState<String>,
    override val start: MutableState<ZonedDateTime>,
    override val end: MutableState<ZonedDateTime>,
    override val duration: MutableIntState,
    override val color: MutableState<Color>
) : Plan(
    id = id,
    title = title,
    description = description,
    start = start,
    end = end,
    duration = duration,
    color = color
) {
    constructor(googleEvent: Event) : this(
        id = googleEvent.id,
        title = mutableStateOf(googleEvent.summary),
        parentTaskId =
        try {
            val lengthStartIndex = googleEvent.description.indexOf("parentTaskId").plus("parentTaskId".length)
            val lengthEndIndex = googleEvent.description.indexOf(":", lengthStartIndex)
            val length = Integer.parseInt(googleEvent.description.substring(lengthStartIndex, lengthEndIndex))
            googleEvent.description.substring(lengthEndIndex + 1, lengthEndIndex + 1 + length)
        } catch (e: Exception) {
            null
        },
        description = mutableStateOf(googleEvent.description),
        start = mutableStateOf(
            ZonedDateTime.parse(
                googleEvent.start.dateTime.toString(),
                dateTimeFormat
            )
        ),
        end = mutableStateOf(
            ZonedDateTime.parse(
                googleEvent.end.dateTime.toString(),
                dateTimeFormat
            )
        ),
        duration = mutableIntStateOf(30),
        color = mutableStateOf(onSurfaceGray)
    ) {
        duration.intValue = Duration.between(start.value, end.value).toMinutes().toInt()
    }
}
