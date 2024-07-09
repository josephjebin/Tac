package com.jebkit.tac.ui.task

import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskList


data class TasksState(
    val taskLists: List<TaskList>,
    val tasks: List<TaskDao>,
    val currentSelectedTaskList: TaskList,
    val currentSelectedTask: TaskDao
)