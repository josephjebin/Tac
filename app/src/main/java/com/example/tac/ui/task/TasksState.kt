package com.example.tac.ui.task

import com.example.tac.data.tasks.TaskDao
import com.example.tac.data.tasks.TaskList


data class TasksState(val taskLists: List<TaskList?>, val tasks:List<TaskDao>)