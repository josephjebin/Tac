package com.jebkit.tac.data.tasks

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.jebkit.tac.ui.theme.onSurfaceGray
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.api.services.tasks.model.Task
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

data class TaskDao(
    val id: String,
    val title: MutableState<String>,
    val notes: MutableState<String>,
    val completed: MutableState<Boolean> = mutableStateOf(false),
    val start: MutableState<ZonedDateTime> = mutableStateOf(
        ZonedDateTime.of(
            LocalDateTime.MIN,
            ZoneId.systemDefault()
        )
    ),
    val end: MutableState<ZonedDateTime> = mutableStateOf(
        ZonedDateTime.of(
            LocalDateTime.MIN,
            ZoneId.systemDefault()
        )
    ),
    val deleted: MutableState<Boolean> = mutableStateOf(false),
    val taskList: MutableState<String>,
    val scheduledDuration: MutableIntState = mutableIntStateOf(0),
    val workedDuration: MutableIntState = mutableIntStateOf(0),
    val neededDuration: MutableIntState,
    val priority: Priority = Priority.Priority4,
    val color: MutableState<Color> = mutableStateOf(onSurfaceGray)
) {
    constructor(googleTask: Task, taskList: String) : this(
        id = googleTask.id,
        title = mutableStateOf(googleTask.title),
        notes = mutableStateOf(googleTask.notes),
        completed = mutableStateOf(googleTask.status.equals("completed")),
        //need to implement notes parsing to find actual start date time
        //ZonedDateTime.parse(task.notes.dateTime.toString(), dateTimeFormat)
        start = mutableStateOf(
            ZonedDateTime.of(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                ZoneId.systemDefault()
            )
        ),
        //need to implement notes parsing to find actual end time and durations
        end = mutableStateOf(ZonedDateTime.now()),
        deleted = mutableStateOf(googleTask.deleted),
        taskList = mutableStateOf(taskList),
        scheduledDuration = mutableIntStateOf(0),
        workedDuration = mutableIntStateOf(0),
        neededDuration = mutableIntStateOf(60)
        ) {
        if (googleTask.due != null) ZonedDateTime.parse(googleTask.due.toString(), inputFormat)
    }

    constructor() : this(
        id = "STUB",
        title = mutableStateOf("STUB"),
        notes = mutableStateOf("STUB"),
        taskList = mutableStateOf("STUB"),
        neededDuration = mutableIntStateOf(0)
    )
}