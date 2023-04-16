package com.example.tac.data.calendar

import androidx.compose.ui.graphics.Color
import com.example.tac.ui.theme.onSurfaceGray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EventDao (
    var name: String = "",
    val color: Color = onSurfaceGray,
    var start: LocalDateTime = LocalDateTime.now(),
    var end: LocalDateTime = LocalDateTime.now(),
    var description: String = ""
) {
    constructor(event: GoogleEvent): this() {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz")
        this.name = event.summary
        if(event.start.dateTime.isNotEmpty()) this.start = LocalDateTime.parse(event.start.dateTime, inputFormat)
        if(event.end.dateTime.isNotEmpty()) this.end = LocalDateTime.parse(event.end.dateTime, inputFormat)
        this.description = event.description
    }
}