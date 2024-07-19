package com.jebkit.tac.ui.task
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
//import com.google.api.services.tasks.model.TaskList
//import com.jebkit.tac.data.dummyData.dummyDataTaskLists
//import com.jebkit.tac.data.dummyData.dummyDataTasks
//import com.jebkit.tac.data.tasks.GoogleTasksService
//import com.jebkit.tac.data.tasks.TaskDao
//import com.jebkit.tac.data.tasks.TaskListDao
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import java.time.LocalDate
//
//
////class TasksViewModel(authState: AuthState, authorizationService : AuthorizationService): ViewModel() {
//class TasksViewModel(credential: GoogleAccountCredential): ViewModel() {
//    val TAG = "TasksViewModel"
//    private val _uiState = MutableStateFlow(
//        GoogleTasksState(
//            taskLists = dummyDataTaskLists(),
//            tasks = dummyDataTasks(),
//            currentSelectedTaskList = dummyDataTaskLists()[0],
//            currentSelectedTask = TaskDao()
//        )
//    )
//    val uiState: StateFlow<GoogleTasksState> = _uiState.asStateFlow()
//    private var tasksService: GoogleTasksService = GoogleTasksService(credential)
//
//    init {
//        getTaskListsAndTasks(
//            LocalDate.now().plusMonths(2).year,
//            LocalDate.now().plusMonths(2).monthValue
//            LocalDate.now().plusMonths(2).year,
//            LocalDate.now().plusMonths(2).monthValue)
//    }
//
//    fun getTaskListsAndTasks(
//        minYear: Int,
//        minMonth: Int,
//        maxYear: Int,
//        maxMonth: Int
//    ) {
//        viewModelScope.launch {
//            val taskLists = tasksService.getTaskLists()
//            updateTaskLists(taskLists)
//            for (taskList in taskLists) {
//                val tasksInProject = tasksService.getTasksForSpecificYearAndMonth(
//                    taskList.title,
//                    minYear,
//                    minMonth,
//                    maxYear,
//                    maxMonth
//                )
//                for(task in tasksInProject) {
//                    tasks.add(TaskDao(task, taskList.title))
//                }
//            }
////            updateTasks(tasks)
//        }
//    }
//
//    private fun updateTaskLists(newLists: List<TaskList>) {
//        _uiState.update {currentState ->
//            currentState.copy(taskLists = newLists)
//        }
//    }
//
//    private fun updateTasks(newTasks: List<TaskDao>) {
//        _uiState.update {currentState ->
//            currentState.copy(tasks = newTasks)
//        }
//    }
//
//    fun updateCurrentSelectedTaskList(currentSelectedTaskListDao: TaskListDao) {
//        _uiState.update { currentState ->
//            currentState.copy(currentSelectedTaskList = currentSelectedTaskListDao)
//        }
//    }
//
//    fun updateCurrentSelectedTask(currentSelectedTask: TaskDao) {
//        _uiState.update { currentState ->
//            currentState.copy(currentSelectedTask = currentSelectedTask)
//        }
//    }
//
//    fun modifyCurrentSelectedTask(currentSelectedTask: TaskDao) {
//
//    }
//
////    companion object {
////        val Factory: ViewModelProvider.Factory = viewModelFactory {
////            initializer {
////                val application = (this[APPLICATION_KEY] as TacApplication)
////
////                var authState = AuthState()
////                val jsonString = application
////                    .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
////                    .getString(Constants.AUTH_STATE, null)
////
////                if (jsonString != null && !TextUtils.isEmpty(jsonString)) {
////                    try { authState = AuthState.jsonDeserialize(jsonString) }
////                    catch (jsonException: JSONException) { }
////                }
////
////                val appAuthConfiguration = AppAuthConfiguration.Builder()
////                    .setBrowserMatcher(
////                        BrowserAllowList(
////                            VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
////                            VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
////                        )
////                    ).build()
////
////                val authorizationService = AuthorizationService(
////                    application,
////                    appAuthConfiguration)
////
////                TasksViewModel(authState, authorizationService)
////            }
////        }
////    }
//}