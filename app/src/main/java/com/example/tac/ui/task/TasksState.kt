package com.example.tac.ui.task

import com.example.tac.data.tasks.TaskDao
import com.google.api.services.tasks.model.TaskList


data class TasksState(val taskLists: List<TaskList?>?, val tasks:List<TaskDao>)