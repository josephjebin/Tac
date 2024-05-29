package com.example.tac.data.calendar

import androidx.compose.ui.graphics.Color
import java.time.ZonedDateTime

data class EventDao(
    override var id: Int,
    override var name: String,
    var busy: Boolean,
    override var start: ZonedDateTime,
    override var end: ZonedDateTime,
    override var color: Color = Color.Gray
): Plan(
    id = id,
    name = name,
    start = start,
    end = end,
    color = color
)