package com.example.tac.data.calendar

import androidx.compose.ui.graphics.Color
import com.example.tac.ui.theme.onSurfaceGray
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class EventDao (
    var name: String = "",
    var color: Color = onSurfaceGray,
    var start: ZonedDateTime = ZonedDateTime.now(),
    var end: ZonedDateTime = ZonedDateTime.now(),
    var description: String = ""
) {
    constructor(event: GoogleEvent): this() {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz")
        this.name = event.summary
        this.color = Color(0xFFAFBBF2)
        if(event.start.dateTime.isNotEmpty()) this.start = ZonedDateTime.parse(event.start.dateTime, inputFormat)
        if(event.end.dateTime.isNotEmpty()) this.end = ZonedDateTime.parse(event.end.dateTime, inputFormat)
        this.description = event.description
    }
}