package com.jebkit.tac.ui.tasksAndCalendar

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.GoogleCalendarService
import com.jebkit.tac.data.calendar.ScheduledTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
//import com.jebkit.tac.data.dummyData.dummyEvents
//import com.jebkit.tac.data.dummyData.dummyScheduledTasks
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.tasks.model.Task
import com.jebkit.tac.R
import com.jebkit.tac.data.calendar.ScheduledTaskJson
import com.jebkit.tac.data.tasks.GoogleTasksService
import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskListDao
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class TasksAndCalendarViewModel(credential: GoogleAccountCredential) : ViewModel() {
    //    var maxLocalDate = LocalDate.of(maxYear, maxMonth, 1)
//        maxLocalDate = maxLocalDate.withDayOfMonth(maxLocalDate.month.length(maxLocalDate.isLeapYear))
    val TAG = "TasksAndCalendarViewModel"
    private val _uiState = MutableStateFlow(TasksAndCalendarState())
    val uiState: StateFlow<TasksAndCalendarState> = _uiState.asStateFlow()

    private val googleCalendarService: GoogleCalendarService = GoogleCalendarService(credential)
    private val googleTasksService: GoogleTasksService = GoogleTasksService(credential)

    init {
        viewModelScope.launch {
            //get tasklists - can release thread
            val taskLists = async {
                googleTasksService.getTaskLists()
            }

            //get all calendar data - can release thread
            val googleCalendarData = async {
                googleCalendarService.getEvents(
                    _uiState.value.minBufferDate.value,
                    _uiState.value.maxBufferDate.value
                )
            }

            //add all tasklists to view model and get all tasks for each tasklist - depends on result of get tasklists
            val taskJobs: MutableList<Deferred<Pair<String, ArrayList<Task>>>> = mutableListOf()
            taskLists.await().forEach { googleTaskList ->
                _uiState.value.googleTasksState.value.taskListDaos[googleTaskList.id] =
                    TaskListDao(
                        googleTaskList.id,
                        mutableStateOf(googleTaskList.title)
                    )

                taskJobs.add(async {
                    googleTasksService.getTasks(
                        googleTaskList.id,
//                        _uiState.value.minBufferDate.value,
//                        _uiState.value.maxBufferDate.value
                    )
                })
            }

            taskJobs.awaitAll().forEach { (parentTaskListId, googleTasks) ->
                googleTasks.forEach { googleTask ->
                    _uiState.value.googleTasksState.value.tasks[googleTask.id] = googleTask

                    _uiState.value.googleTasksState.value.taskDaos[googleTask.id] =
                        TaskDao(googleTask, parentTaskListId)
                }
            }.also {
                googleCalendarData.await().forEach { googleEvent ->
                    //add google event to google event map
                    (_uiState.value.googleCalendarState.value as GoogleCalendarState.Success).googleEvents[googleEvent.id] = googleEvent

                    //ScheduledTask
                    if (googleEvent.description.contains("parentTaskId")) {
                        val jsonStartIndex =
                            googleEvent.description.indexOf(R.string.scheduled_task_json.toString())
                                .plus(R.string.scheduled_task_json.toString().length)
                        //+1 at the end to include the end bracket
                        val jsonEndIndex = googleEvent.description.indexOf("}\n)", jsonStartIndex).plus(1)
                        val scheduledTaskJson = Json.decodeFromString<ScheduledTaskJson>(
                            googleEvent.description.substring(
                                jsonStartIndex,
                                jsonEndIndex.plus(1)
                            )
                        )
//                        val parentTaskIdStartIndex =
//                            googleEvent.description.indexOf("parentTaskId:").plus(13)
//                        val parentTaskIdEndIndex =
//                            googleEvent.description.indexOf(";", parentTaskIdStartIndex)
//                        val parentTaskId = googleEvent.description.substring(
//                            parentTaskIdStartIndex,
//                            parentTaskIdEndIndex
//                        )
                        val scheduledTask = ScheduledTask(googleEvent, scheduledTaskJson)
                        val parentTask =
                            _uiState.value.googleTasksState.value.taskDaos[scheduledTask.parentTaskId]
                        if (parentTask != null) {
                            val parentTaskScheduledDuration = parentTask.scheduledDuration.intValue
                            parentTask.scheduledDuration.intValue =
                                parentTaskScheduledDuration + scheduledTask.duration.intValue
                        } else {
                            //OH NO - ORPHANED SCHEDULED TASKS
                            //todo: how to handle?
                        }
                        //todo: do we wanna do this block of logic for orphaned tasks??
                        var scheduledTaskMap =
                            (_uiState.value.googleCalendarState.value as GoogleCalendarState.Success).scheduledTasks[parentTaskId]
                        if (scheduledTaskMap == null) {
                            scheduledTaskMap =
                                mutableStateMapOf(Pair(googleEvent.id, scheduledTask))
                            (_uiState.value.googleCalendarState.value as GoogleCalendarState.Success).scheduledTasks[parentTaskId] =
                                scheduledTaskMap
                        } else {
                            scheduledTaskMap.put(googleEvent.id, scheduledTask)
                        }
                    } else
                        (_uiState.value.googleCalendarState.value as GoogleCalendarState.Success).eventDaos[googleEvent.id] =
                            EventDao(googleEvent)
                }
            }
        }
    }

//    fun initCalendarsAndEvents() {
//        viewModelScope.launch {
//            val calendars = calendarService.getCalendarList()
//            updateCalendarsState(calendars)
//            val events = mutableListOf<EventDao>()
//            for(calendar in calendars) {
//                calendarService.initEvents(calendar.id, _uiState.value.selectedDate, _uiState.value.constantMaxDate).forEach { events.add(EventDao(it)) }
//            }
//            updateEventsState(events)
//        }
//    }

//    private fun updateCalendarsState(newCalendars: List<GoogleCalendar>) {
//        _uiState.update { calendarState ->
//            calendarState.copy(calendars = newCalendars)
//        }
//    }
//
//    private fun updateEventsState(newEvents: MutableList<EventDao>) {
//        _uiState.update { calendarState ->
//            calendarState.copy(events = newEvents)
//        }
//    }

    fun saveScheduledTask(newTask: ScheduledTask) {
        //call calendar api to add scheduled task event
        //add response to view model because response contains proper id
//        _uiState.value.googleCalendarState.value  += newTask
    }

    fun deleteScheduledTask(scheduledTask: ScheduledTask) {
//        _uiState.value.scheduledTasks.value -= scheduledTask
    }

    fun addEventDao(newEventDao: EventDao) {
//        _uiState.value.events.value += newEventDao
    }

    fun removeEventDao(eventDao: EventDao) {
//        _uiState.value.events.value -= eventDao
    }

//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TacApplication)
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
//                TasksAndCalendarViewModel(authState, authorizationService)
//            }
//        }
//    }
}