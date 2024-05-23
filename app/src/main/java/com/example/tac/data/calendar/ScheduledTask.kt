package com.example.tac.data.calendar

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.ZonedDateTime


data class ScheduledTask(
    override var name: String,
    val parentTaskId: String,
    override var start: ZonedDateTime,
    override var end: ZonedDateTime,
    var scheduledDuration: Int = Duration.between(start, end).toMinutes().toInt(),
    var workedDuration: Int = 0,
    override var color: Color = Color.Gray
): Plan(
    name = name,
    start = start,
    end = end,
    color = color
)
