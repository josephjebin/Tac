package com.example.tac.data.calendar

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.ZonedDateTime

data class EventDao(
    override var id: Int,
    override var name: String,
    var busy: Boolean,
    override var start: MutableState<ZonedDateTime>,
    override var end: MutableState<ZonedDateTime>,
    override var duration: Int = Duration.between(start.value, end.value).toMinutes().toInt(),
    override var color: Color = Color.Gray
): Plan(
    id = id,
    name = name,
    start = start,
    end = end,
    duration = duration,
    color = color
)