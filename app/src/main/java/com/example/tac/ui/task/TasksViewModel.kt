package com.example.tac.ui.task

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tac.TacApplication
import com.example.tac.data.Constants
import com.example.tac.data.calendar.CalendarService
import com.example.tac.data.tasks.TaskDao
import com.example.tac.data.tasks.TaskList
import com.example.tac.data.tasks.TasksService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import org.json.JSONException


class TasksViewModel(authState: AuthState, authorizationService : AuthorizationService): ViewModel() {
    val TAG = "TasksViewModel"
    private val _uiState = MutableStateFlow(TasksState(listOf(), listOf(), TaskList(), TaskDao()))
    val uiState: StateFlow<TasksState> = _uiState.asStateFlow()
    var tasksService: TasksService
    var calendarService: CalendarService

    init {
        tasksService = TasksService(authState, authorizationService)
        calendarService = CalendarService(authState, authorizationService)
    }

    fun getTaskListsAndTasks() {
        viewModelScope.launch {
            calendarService.getCalendarList()
            val taskLists = tasksService.getTaskLists()
            updateTaskLists(taskLists)
            val tasks = mutableListOf<TaskDao>()
            for (taskList in taskLists) {
                val tasksInProject = tasksService.getTasks(taskList.id)
                for(taskInProject in tasksInProject) {
                    tasks.add(TaskDao(taskInProject, taskList.title))
                }
            }
            updateTasks(tasks)
        }
    }

    private fun updateTaskLists(newLists: List<TaskList>) {
        _uiState.update {currentState ->
            currentState.copy(taskLists = newLists)
        }
    }

    private fun updateTasks(newTasks: List<TaskDao>) {
        _uiState.update {currentState ->
            currentState.copy(tasks = newTasks)
        }
    }

    fun updateCurrentSelectedTaskList(currentSelectedTaskList: TaskList) {
        _uiState.update { currentState ->
            currentState.copy(currentSelectedTaskList = currentSelectedTaskList)
        }
    }

    fun updateCurrentSelectedTask(currentSelectedTask: TaskDao) {
        _uiState.update { currentState ->
            currentState.copy(currentSelectedTask = currentSelectedTask)
        }
    }

    fun modifyCurrentSelectedTask(currentSelectedTask: TaskDao) {

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