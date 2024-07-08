package com.example.tac.data.calendar

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.google.api.services.calendar.model.Event
import java.time.Duration
import java.time.ZonedDateTime


data class ScheduledTask(
    override val id: String,
    override var title: String,
    val parentTaskId: String,
    override var start: MutableState<ZonedDateTime>,
    override var end: MutableState<ZonedDateTime>,
    var scheduledDuration: Int = Duration.between(start.value, end.value).toMinutes().toInt(),
    var workedDuration: Int = 0,
    override var color: Color = Color.Gray
): Plan(
    id = id,
    title = title,
    start = start,
    end = end,
    duration = scheduledDuration,
    color = color
) {
    constructor(id: String): this(
        id,
        "nice",
        "nice",
        start = mutableStateOf(ZonedDateTime.now()),
        end = mutableStateOf(ZonedDateTime.now()),
        0
    )

    constructor(googleEvent: Event) : this(
        id = googleEvent.id,

    )
}
