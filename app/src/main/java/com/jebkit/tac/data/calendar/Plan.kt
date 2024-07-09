package com.jebkit.tac.data.calendar

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.jebkit.tac.ui.theme.onSurfaceGray
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz")

abstract class Plan(
    open val id: String,
    open val title: MutableState<String> = mutableStateOf(""),
    open val description: MutableState<String> = mutableStateOf(""),
    open val start: MutableState<ZonedDateTime> = mutableStateOf(ZonedDateTime.now()),
    open val end: MutableState<ZonedDateTime> = mutableStateOf(ZonedDateTime.now().plusMinutes(30)),
    open val duration: MutableIntState = mutableIntStateOf(30),
    open val color: MutableState<Color> = mutableStateOf(onSurfaceGray)
) {

//    constructor(event: GoogleEvent): this() {
//        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz")
//        this.name = event.summary
//        if(event.start.dateTime.isNotEmpty()) this.start = ZonedDateTime.parse(event.start.dateTime, inputFormat)
//        if(event.end.dateTime.isNotEmpty()) this.end = ZonedDateTime.parse(event.end.dateTime, inputFormat)
//        this.description = event.description
//    }
}