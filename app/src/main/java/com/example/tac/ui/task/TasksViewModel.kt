package com.example.tac.ui.task

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tac.TacApplication
import com.example.tac.data.Constants
import com.example.tac.data.tasks.TaskList
import com.example.tac.data.tasks.TasksService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import org.json.JSONException


class TasksViewModel(authState: AuthState, authorizationService : AuthorizationService): ViewModel() {
    val TAG = "TasksViewModel"
    private val _uiState = MutableStateFlow(TasksState(listOf(), listOf()))
    val uiState: StateFlow<TasksState> = _uiState.asStateFlow()
    var tasksService: TasksService

    init {
        Log.e(TAG, "IN THE INIT")
        tasksService = TasksService(authState, authorizationService)
    }

    fun getTaskLists() {
        viewModelScope.launch {
            val newLists = tasksService.getTaskLists()
            updateTaskLists(newLists)
            Log.e(TAG, _uiState.value.taskLists.toString())
        }
    }

    private fun updateTaskLists(newLists: List<TaskList?>?) {
        _uiState.value = TasksState(newLists, listOf())
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TacApplication)

                var authState = AuthState()
                val jsonString = application
                    .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                    .getString(Constants.AUTH_STATE, null)

                if (jsonString != null && !TextUtils.isEmpty(jsonString)) {
                    try { authState = AuthState.jsonDeserialize(jsonString) }
                    catch (jsonException: JSONException) { }
                }

                val appAuthConfiguration = AppAuthConfiguration.Builder()
                    .setBrowserMatcher(
                        BrowserAllowList(
                            VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                            VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
                        )
                    ).build()

                val authorizationService = AuthorizationService(
                    application,
                    appAuthConfiguration)

                TasksViewModel(authState, authorizationService)
            }
        }
    }
}