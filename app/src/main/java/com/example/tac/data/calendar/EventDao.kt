package com.example.tac.data.calendar

import androidx.compose.ui.graphics.Color
import java.time.ZonedDateTime

data class EventDao(
    var busy: Boolean,
    override var name: String,
    override var start: ZonedDateTime,
    override var end: ZonedDateTime,
    override var color: Color = Color.Gray
): Plan(
    name = name,
    start = start,
    end = end,
    color = color
)