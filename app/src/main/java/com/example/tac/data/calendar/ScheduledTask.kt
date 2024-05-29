package com.example.tac.data.calendar

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.ZonedDateTime


data class ScheduledTask(
    override var id: Int,
    override var name: String,
    val parentTaskId: String,
    override var start: MutableState<ZonedDateTime>,
    override var end: MutableState<ZonedDateTime>,
    var scheduledDuration: Int = Duration.between(start.value, end.value).toMinutes().toInt(),
    var workedDuration: Int = 0,
    override var color: Color = Color.Gray
): Plan(
    id = id,
    name = name,
    start = start,
    end = end,
    duration = scheduledDuration,
    color = color
)
