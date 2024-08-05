package com.jebkit.tac.data.calendar

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.jebkit.tac.ui.theme.akiflow_lavender
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val googleCalendarDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX")


abstract class Plan(
    open val id: String,
    open val title: MutableState<String> = mutableStateOf(""),
    open val description: MutableState<String?> = mutableStateOf(null),
    open val start: MutableState<ZonedDateTime> = mutableStateOf(ZonedDateTime.now()),
    open val end: MutableState<ZonedDateTime> = mutableStateOf(ZonedDateTime.now().plusMinutes(30)),
    open val duration: MutableIntState = mutableIntStateOf(30),
    open val color: MutableState<Color> = mutableStateOf(akiflow_lavender)
)