package com.jebkit.tac.ui.tasks

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.google.api.services.tasks.model.Task
import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskListDao

data class GoogleTasksState(
    //map of id to TaskListDao
    val taskListDaos: SnapshotStateMap<String, TaskListDao> = mutableStateMapOf(),
    val tasks: SnapshotStateMap<String, Task> = mutableStateMapOf(),
    //map of id to taskDao
    val taskDaos: SnapshotStateMap<String, TaskDao> = mutableStateMapOf(),
    val currentSelectedTaskListDao: MutableState<TaskListDao?> = mutableStateOf(null),
    val currentSelectedTaskDao: MutableState<TaskDao?> = mutableStateOf(null)
)