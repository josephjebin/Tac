package com.jebkit.tac.data.tasks

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.jebkit.tac.ui.theme.onSurfaceGray
import java.time.format.DateTimeFormatter
import com.google.api.services.tasks.model.Task
import kotlinx.serialization.Serializable
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
    val taskListId: MutableState<String>,
    val associatedScheduledTaskIds: SnapshotStateList<String>,
    val scheduledDuration: MutableIntState = mutableIntStateOf(0),
    val workedDuration: MutableIntState = mutableIntStateOf(0),
    val neededDuration: MutableIntState,
    val priority: Priority = Priority.Priority4,
    val color: MutableState<Color> = mutableStateOf(onSurfaceGray)
) {
    constructor(googleTask: Task, taskListId: String, taskJson: TaskJson, notes: String) : this(
        id = googleTask.id,
        title = mutableStateOf(googleTask.title),
        notes = mutableStateOf(notes),
        completed = mutableStateOf(googleTask.status.equals("completed")),
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
        taskListId = mutableStateOf(taskListId),
        associatedScheduledTaskIds = mutableStateListOf(),
        scheduledDuration = mutableIntStateOf(0),
        workedDuration = mutableIntStateOf(0),
        neededDuration = mutableIntStateOf(taskJson.neededDuration)
    ) {
        associatedScheduledTaskIds.addAll(taskJson.scheduledTasks)
    }
}

@Serializable
data class TaskJson(
    val neededDuration: Int,
    val scheduledTasks: List<String>
)