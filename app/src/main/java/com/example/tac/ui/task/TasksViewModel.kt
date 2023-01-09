package com.example.tac.ui.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tac.data.Constants
import com.example.tac.data.tasks.TasksService
import com.google.api.services.tasks.model.Task
import com.google.api.services.tasks.model.TaskList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class TasksViewModel(val authState: AuthState, val authorizationService : AuthorizationService): ViewModel() {
    val TAG = "TasksViewModel"
    var _uiState = MutableStateFlow(TasksState(listOf(), listOf()))
    val uiState: StateFlow<TasksState> = _uiState.asStateFlow()
    private lateinit var tasksService: TasksService

    init {
        Log.e(TAG, "IN THE INIT")
        getTaskLists()
    }

    fun updateTaskListsAndTasks(newLists: List<TaskList>, newTasks: List<Task>) {
        _uiState.update { currentState ->
            currentState.copy(taskLists = newLists, tasks = newTasks)
        }
    }

    private fun getTaskLists() {
        authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            Log.e(TAG, "trying to make call with token: ${authState.accessToken}")
            viewModelScope.launch {
                async(Dispatchers.IO) {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .get()
                        .url(Constants.URL_TASKS + "/tasks/v1/users/@me/lists")
                        .addHeader("Authorization", "Bearer " + authState.accessToken)
                        .build()

                    try {
                        val response = client.newCall(request).execute()
                        val jsonBody = response.body?.string() ?: ""
                        Log.e(TAG, JSONObject(jsonBody).toString())
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }
}