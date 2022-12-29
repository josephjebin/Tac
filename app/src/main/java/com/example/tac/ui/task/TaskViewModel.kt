package com.example.tac.ui.task

import androidx.lifecycle.ViewModel
import com.google.api.services.tasks.model.TaskList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskViewModel: ViewModel() {
    var _uiState = MutableStateFlow(TasksState(listOf(), listOf()))
    val uiState: StateFlow<TasksState> = _uiState.asStateFlow()

//    init {
//        updateListsAndTasks()
//    }
//
    fun updateTaskLists(newLists: List<TaskList>) {
        _uiState.update { currentState ->
            currentState.copy(taskLists = newLists)
        }
    }
}