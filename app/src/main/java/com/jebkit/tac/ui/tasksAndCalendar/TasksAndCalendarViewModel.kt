package com.jebkit.tac.ui.tasksAndCalendar

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jebkit.tac.R
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.GoogleCalendarService
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.data.calendar.ScheduledTaskJson
import com.jebkit.tac.data.tasks.GoogleTasksService
import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskListDao
import com.jebkit.tac.util.toEventDateTime
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.tasks.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.TimeZone


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
                        googleTaskList.id
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
                    _uiState.value.googleCalendarState.value.googleEvents[googleEvent.id] =
                        googleEvent

                    //ScheduledTask
                    if (googleEvent.description.contains("parentTaskId")) {
                        try {
                            val jsonStartIndex =
                                googleEvent.description.indexOf(R.string.scheduled_task_json.toString())
                                    .plus(R.string.scheduled_task_json.toString().length)
                            //+1 at the end to include the end bracket
                            val jsonEndIndex =
                                googleEvent.description.indexOf("}", jsonStartIndex).plus(1)
                            val scheduledTaskJson = Json.decodeFromString<ScheduledTaskJson>(
                                googleEvent.description.substring(
                                    jsonStartIndex,
                                    jsonEndIndex
                                ).trim()
                            )

                            val scheduledTask = ScheduledTask(googleEvent, scheduledTaskJson)
                            _uiState.value.googleCalendarState.value.scheduledTasks[scheduledTask.id] =
                                scheduledTask

                            val parentTask =
                                _uiState.value.googleTasksState.value.taskDaos[scheduledTask.parentTaskId]
                            if (parentTask != null) {
                                val parentTaskScheduledDuration =
                                    parentTask.scheduledDuration.intValue
                                parentTask.scheduledDuration.intValue =
                                    parentTaskScheduledDuration + scheduledTask.duration.intValue
                            } else {
                                //OH NO - ORPHANED SCHEDULED TASKS
                                //todo: how to handle?
                            }
                        } catch (e: Exception) {
                            //JSON parsing error
                            Log.e(TAG, "error making scheduled task")
                            val scheduledTask =
                                ScheduledTask(googleEvent, "DefaultParentTaskId", false)
                            _uiState.value.googleCalendarState.value.scheduledTasks[scheduledTask.id] =
                                scheduledTask
                        }
                    } else
                        _uiState.value.googleCalendarState.value.eventDaos[googleEvent.id] =
                            EventDao(googleEvent)
                }
            }
        }
    }

    fun addScheduledTask(newTask: ScheduledTask) {
        val event = Event()
            .setSummary(newTask.title.value)
            .setDescription(newTask.description.value +
                    "\n" +

                    Json.encodeToString(ScheduledTaskJson(newTask.parentTaskId, newTask.completed.value)
                )
            )
            .setStart()
            .setEnd()
    }

    fun updateScheduledTaskTime(scheduledTaskId: String, newStartTime: ZonedDateTime) {
        val event = _uiState.value.googleCalendarState.value.googleEvents[scheduledTaskId]
        if (event != null) {
            val scheduledTask =
                _uiState.value.googleCalendarState.value.scheduledTasks[scheduledTaskId]
            if (scheduledTask != null) {
                val oldStartTime = scheduledTask.start.value
                scheduledTask.start.value = newStartTime
                scheduledTask.end.value =
                    newStartTime.plusMinutes(scheduledTask.duration.intValue.toLong())

                try {
                    viewModelScope.launch {
                        _uiState.value.googleCalendarState.value.googleEvents[scheduledTaskId] =
                            googleCalendarService.updateEvent(event)
                    }
                } catch (ioException: IOException) {
                    Log.e(
                        TAG,
                        "Couldn't update google calendar for event ${event.summary} with id: ${event.id}. Reverting changes."
                    )
                    scheduledTask.start.value = oldStartTime
                    scheduledTask.end.value =
                        oldStartTime.plusMinutes(scheduledTask.duration.intValue.toLong())
                }
            } else {
                Log.e(TAG, "Could not find scheduled task with id $scheduledTaskId")
            }
        } else {
            Log.e(TAG, "Could not find event with id $scheduledTaskId")
        }
    }

    fun updateScheduledTask(scheduledTaskId: String, newScheduledTask: ScheduledTask) {
        val oldScheduledTask = _uiState.value.googleCalendarState.value
    }

    fun deleteScheduledTask(scheduledTask: ScheduledTask) {
//        _uiState.value.scheduledTasks.value -= scheduledTask
    }

    fun addEventDao(newEventDao: EventDao) {
//        _uiState.value.events.value += newEventDao
    }

    fun updateEventDaoTime(eventDao: EventDao, newStartZonedDateTime: ZonedDateTime) {
        val event = _uiState.value.googleCalendarState.value.googleEvents[eventDao.id]
        if (event != null) {
            val oldStartTime = eventDao.start.value
            eventDao.start.value = newStartZonedDateTime
            val newEndTime = newStartZonedDateTime.plusMinutes(eventDao.duration.intValue.toLong())
            eventDao.end.value = newEndTime

            try {
                viewModelScope.launch {
                    event.setStart(newStartZonedDateTime.toEventDateTime())
                    event.setEnd(newEndTime.toEventDateTime())
                    _uiState.value.googleCalendarState.value.googleEvents[eventDao.id] =
                        googleCalendarService.updateEvent(event)
                }
            } catch (ioException: IOException) {
                Log.e(
                    TAG,
                    "Couldn't update google calendar for event ${event.summary} with id: ${event.id}. Reverting changes."
                )
                eventDao.start.value = oldStartTime
                eventDao.end.value =
                    oldStartTime.plusMinutes(eventDao.duration.intValue.toLong())
            }
        } else {
            Log.e(TAG, "Could not find event with id ${eventDao.id}")
        }
    }

    fun deleteEventDao(eventDao: EventDao) {
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