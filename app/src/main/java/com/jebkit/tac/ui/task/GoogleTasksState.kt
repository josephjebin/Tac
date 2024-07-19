package com.jebkit.tac.ui.task

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskListDao

data class GoogleTasksState(
    //map of id to TaskListDao
    val taskListDaos: SnapshotStateMap<String, TaskListDao> = mutableStateMapOf(),
    //map of id to taskDao
    val overdueTaskDaos: SnapshotStateMap<String, TaskDao> = mutableStateMapOf(),
    val unscheduledTaskDaos: SnapshotStateMap<String, TaskDao> = mutableStateMapOf(),
    val taskDaos: SnapshotStateMap<String, TaskDao> = mutableStateMapOf(),
    val currentSelectedTaskListDao: MutableState<TaskListDao?> = mutableStateOf(null),
    val currentSelectedTaskDao: MutableState<TaskDao?> = mutableStateOf(null)
)