package com.example.tac.ui.task

import androidx.lifecycle.ViewModel
import com.example.tac.data.dummyData.dummyDataTaskLists
import com.example.tac.data.dummyData.dummyDataTasks
import com.example.tac.data.tasks.TaskDao
import com.example.tac.data.tasks.TaskList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


//class TasksViewModel(authState: AuthState, authorizationService : AuthorizationService): ViewModel() {
class TasksViewModel: ViewModel() {
    val TAG = "TasksViewModel"
    private val _uiState = MutableStateFlow(
        TasksState(
            taskLists = dummyDataTaskLists(),
            tasks = dummyDataTasks(),
            currentSelectedTaskList = dummyDataTaskLists()[0],
            currentSelectedTask = TaskDao()
        )
    )
    val uiState: StateFlow<TasksState> = _uiState.asStateFlow()
//    var tasksService: TasksService

//    init {
//        tasksService = TasksService(authState, authorizationService)
//    }

//    fun getTaskListsAndTasks() {
//        viewModelScope.launch {
//            val taskLists = tasksService.getTaskLists()
//            updateTaskLists(taskLists)
//            val tasks = mutableListOf<TaskDao>()
//            for (taskList in taskLists) {
//                val tasksInProject = tasksService.getTasks(taskList.id)
//                for(taskInProject in tasksInProject) {
//                    tasks.add(TaskDao(taskInProject, taskList.title))
//                }
//            }
//            updateTasks(tasks)
//        }
//    }

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

//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application = (this[APPLICATION_KEY] as TacApplication)
//
//                var authState = AuthState()
//                val jsonString = application
//                    .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
//                    .getString(Constants.AUTH_STATE, null)
//
//                if (jsonString != null && !TextUtils.isEmpty(jsonString)) {
//                    try { authState = AuthState.jsonDeserialize(jsonString) }
//                    catch (jsonException: JSONException) { }
//                }
//
//                val appAuthConfiguration = AppAuthConfiguration.Builder()
//                    .setBrowserMatcher(
//                        BrowserAllowList(
//                            VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
//                            VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
//                        )
//                    ).build()
//
//                val authorizationService = AuthorizationService(
//                    application,
//                    appAuthConfiguration)
//
//                TasksViewModel(authState, authorizationService)
//            }
//        }
//    }
}