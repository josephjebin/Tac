package com.example.tac.ui.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.services.tasks.model.TaskList
import com.example.tac.data.tasks.TasksService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService



class TasksViewModel(val authState: AuthState, val authorizationService : AuthorizationService): ViewModel() {
    val TAG = "TasksViewModel"
    var _uiState = MutableStateFlow(TasksState(listOf(), listOf()))
    val uiState: StateFlow<TasksState> = _uiState.asStateFlow()
    var tasksService: TasksService

    init {
        Log.e(TAG, "IN THE INIT")
        tasksService = TasksService(authState, authorizationService)
    }

    fun getTaskLists() {
        viewModelScope.launch {
            updateTaskLists(tasksService.getTaskLists())
        }
    }

    fun updateTaskLists(newLists: List<TaskList?>?) {
        _uiState.update { currentState ->
            currentState.copy(taskLists = newLists)
        }
    }
}