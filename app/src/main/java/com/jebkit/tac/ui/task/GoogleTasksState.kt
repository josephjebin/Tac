package com.jebkit.tac.ui.task

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.jebkit.tac.data.tasks.TaskDao
import com.google.api.services.tasks.model.TaskList
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.tasks.TaskListDao

data class GoogleTasksState(
    //map of id to taskList
    val taskLists: SnapshotStateMap<String, TaskList> = mutableStateMapOf(),
    //map of id to taskDao
    val overdueTasks: SnapshotStateMap<String, TaskDao> = mutableStateMapOf(),
    val unscheduledTasks: SnapshotStateMap<String, TaskDao> = mutableStateMapOf(),
    val tasks: SnapshotStateMap<String, TaskDao> = mutableStateMapOf(),
    val currentSelectedTaskList: MutableState<TaskListDao?> = mutableStateOf(null),
    val currentSelectedTask: MutableState<TaskDao?>
)