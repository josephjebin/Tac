package com.example.tac.ui.task

import com.google.api.services.tasks.model.Task
import com.google.api.services.tasks.model.TaskList

data class TasksState(var taskLists: MutableList<TaskList>, var tasks: MutableList<Task>)