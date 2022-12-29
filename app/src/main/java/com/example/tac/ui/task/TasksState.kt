package com.example.tac.ui.task

import com.google.api.services.tasks.model.Task
import com.google.api.services.tasks.model.TaskList

data class TasksState(val taskLists: List<TaskList>, val tasks: List<Task>)