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

val googleTasksDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")

data class TaskDao(
    val id: String,
    val title: MutableState<String>,
    val notes: MutableState<String?>,
    val completed: MutableState<Boolean> = mutableStateOf(false),
//    val start: MutableState<ZonedDateTime> = mutableStateOf(
//        ZonedDateTime.of(
//            LocalDateTime.MIN,
//            ZoneId.systemDefault()
//        )
//    ),
    val due: MutableState<ZonedDateTime?> = mutableStateOf(null),
    val deleted: MutableState<Boolean> = mutableStateOf(false),
    val parentTaskListId: MutableState<String>,
    val scheduledDuration: MutableIntState = mutableIntStateOf(0),
    val workedDuration: MutableIntState = mutableIntStateOf(0),
    val neededDuration: MutableIntState,
    val priority: Priority = Priority.Priority4,
    val color: MutableState<Color> = mutableStateOf(onSurfaceGray)
) {
    constructor(googleTask: Task, taskListId: String) : this(
        id = googleTask.id,
        title = mutableStateOf(googleTask.title),
        notes = mutableStateOf(googleTask.notes),
        completed = mutableStateOf(googleTask.status.equals("completed")),
        //use googleTask.due as DO date as in when can user start DOing the task.
        //reason for DOing this is so we can call the google tasks api with a filter on due date
        //as opposed to making google tasks respond with lots of data and filtering on our end.
        //For example, if we want to see what tasks a user can start this month, we can filter
        //dueMax = last day of this month.
        //need to implement notes parsing to find actual start time
//        start = try {
//            mutableStateOf(
//                ZonedDateTime.parse(
//                    googleTask.due.toStringRfc3339(),
//                    googleTasksDateTimeFormat
//                )
//            )
//        } catch (e: Exception) {
//            mutableStateOf(
//                ZonedDateTime.of(
//                    LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
//                    ZoneId.systemDefault()
//                )
//            )
//        },
        //TODO: implement notes parsing to find actual end time and durations
        //ZonedDateTime.parse(task.notes.dateTime.toString(), dateTimeFormat)
        due = try {
            mutableStateOf(
                ZonedDateTime.parse(
                    googleTask.due.toStringRfc3339(),
                    googleTasksDateTimeFormat
                )
            )
        } catch (e: Exception) {
            mutableStateOf(null)
        },
        deleted = mutableStateOf(googleTask.deleted?: false),
        parentTaskListId = mutableStateOf(taskListId),
        scheduledDuration = mutableIntStateOf(0),
        workedDuration = mutableIntStateOf(0),
        neededDuration = mutableIntStateOf(60)
    ) {
        //
    }

    constructor() : this(
        id = "STUB",
        title = mutableStateOf("STUB"),
        notes = mutableStateOf("STUB"),
        parentTaskListId = mutableStateOf("STUB"),
        neededDuration = mutableIntStateOf(0)
    )
}