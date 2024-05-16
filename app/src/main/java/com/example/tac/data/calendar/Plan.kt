package com.example.tac.data.calendar

import androidx.compose.ui.graphics.Color
import com.example.tac.ui.theme.onSurfaceGray
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

open class Plan(
    open var name: String = "",
    open var color: Color = onSurfaceGray,
    open var start: ZonedDateTime = ZonedDateTime.now(),
    open var end: ZonedDateTime = ZonedDateTime.now(),
    open var description: String = ""
) {
    constructor(event: GoogleEvent): this() {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz")
        this.name = event.summary
        if(event.start.dateTime.isNotEmpty()) this.start = ZonedDateTime.parse(event.start.dateTime, inputFormat)
        if(event.end.dateTime.isNotEmpty()) this.end = ZonedDateTime.parse(event.end.dateTime, inputFormat)
        this.description = event.description
    }
}
