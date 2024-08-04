package com.jebkit.tac.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.EventDateTime
import java.time.ZonedDateTime
import java.util.TimeZone

fun ZonedDateTime.toEventDateTime(): EventDateTime {
    val calendar = java.util.Calendar.getInstance()
    calendar.clear()
    calendar.set(
        this.year,
        this.monthValue.minus(1),
        this.dayOfMonth,
        this.hour,
        this.minute,
        this.second
    )

    val dateTime = DateTime(calendar.time, TimeZone.getDefault())
    return EventDateTime().setDateTime(dateTime)
}