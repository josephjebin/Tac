package com.example.tac.data.calendar

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.example.tac.ui.theme.onSurfaceGray
import java.time.Duration
import java.time.ZonedDateTime

abstract class Plan(
    open var id: Int,
    open var name: String = "",
    open var start: MutableState<ZonedDateTime> = mutableStateOf(ZonedDateTime.now()),
    open var end: MutableState<ZonedDateTime> = mutableStateOf(ZonedDateTime.now().plusMinutes(30)),
    open var duration: Int = Duration.between(start.value, end.value).toMinutes().toInt(),
    open var color: Color = onSurfaceGray,
    open var description: String = ""
) {
//    constructor(event: GoogleEvent): this() {
//        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz")
//        this.name = event.summary
//        if(event.start.dateTime.isNotEmpty()) this.start = ZonedDateTime.parse(event.start.dateTime, inputFormat)
//        if(event.end.dateTime.isNotEmpty()) this.end = ZonedDateTime.parse(event.end.dateTime, inputFormat)
//        this.description = event.description
//    }
}