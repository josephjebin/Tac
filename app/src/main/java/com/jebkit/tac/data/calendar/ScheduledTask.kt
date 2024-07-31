package com.jebkit.tac.data.calendar

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.jebkit.tac.ui.theme.onSurfaceGray
import com.google.api.services.calendar.model.Event
import com.jebkit.tac.R
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.ZonedDateTime
import kotlinx.serialization.json.*

data class ScheduledTask(
    override val id: String,
    override val title: MutableState<String>,
    val parentTaskId: String,
    override val description: MutableState<String?>,
    override val start: MutableState<ZonedDateTime>,
    override val end: MutableState<ZonedDateTime>,
    override val duration: MutableIntState,
    val completed: MutableState<Boolean> = mutableStateOf(false),
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
    constructor(googleEvent: Event, scheduledTaskJson: ScheduledTaskJson, description: String?) : this(
        id = googleEvent.id,
        title = mutableStateOf(googleEvent.summary),
        parentTaskId = scheduledTaskJson.parentTaskId,
        description = mutableStateOf(description),
        start = mutableStateOf(
            ZonedDateTime.parse(
                googleEvent.start.dateTime.toString(),
                googleCalendarDateTimeFormat
            )
        ),
        end = mutableStateOf(
            ZonedDateTime.parse(
                googleEvent.end.dateTime.toString(),
                googleCalendarDateTimeFormat
            )
        ),
        duration = mutableIntStateOf(30),
        completed = mutableStateOf(scheduledTaskJson.completed),
        color = mutableStateOf(onSurfaceGray)
    ) {
        duration.intValue = Duration.between(start.value, end.value).toMinutes().toInt()
    }

    constructor(googleEvent: Event, parentTaskId: String, completed: Boolean) : this(
        id = googleEvent.id,
        title = mutableStateOf(googleEvent.summary),
        parentTaskId = parentTaskId,
        description = mutableStateOf(googleEvent.description),
        start = mutableStateOf(
            ZonedDateTime.parse(
                googleEvent.start.dateTime.toString(),
                googleCalendarDateTimeFormat
            )
        ),
        end = mutableStateOf(
            ZonedDateTime.parse(
                googleEvent.end.dateTime.toString(),
                googleCalendarDateTimeFormat
            )
        ),
        duration = mutableIntStateOf(30),
        completed = mutableStateOf(completed),
        color = mutableStateOf(onSurfaceGray)
    ) {
        duration.intValue = Duration.between(start.value, end.value).toMinutes().toInt()
    }
}

@Serializable
data class ScheduledTaskJson(
    val parentTaskId: String,
    val completed: Boolean
)