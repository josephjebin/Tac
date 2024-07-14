package com.jebkit.tac.ui.task

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.jebkit.tac.data.tasks.TaskDao
import com.google.api.services.tasks.model.TaskList

data class TasksState(
    val taskLists: List<TaskList>,
    //map of id to taskDao
    val tasks: SnapshotStateMap<String, TaskDao> = mutableStateMapOf(),
    val currentSelectedTaskList: TaskList,
    val currentSelectedTask: TaskDao
)