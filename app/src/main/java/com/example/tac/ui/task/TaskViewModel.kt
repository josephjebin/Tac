package com.example.tac.ui.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tac.data.TaskService
import com.google.api.services.tasks.model.TaskList

class TaskViewModel: ViewModel() {
    var _state: TasksState by mutableStateOf(TasksState(mutableListOf(), mutableListOf()))
        private set

    init {
        updateListsAndTasks()
    }

    fun updateListsAndTasks() {
        _state.taskLists = TaskService().getTaskLists()
    }

    fun getLists(): MutableList<TaskList> {
        return _state.taskLists
    }
}