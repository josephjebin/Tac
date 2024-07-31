package com.jebkit.tac.ui.tasksAndCalendar

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.calendar.model.Event
import com.google.api.services.tasks.model.Task
import com.jebkit.tac.constants.Constants.Companion.SCHEDULEDTASK_JSON_HEADER
import com.jebkit.tac.constants.Constants.Companion.TASK_JSON_HEADER
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.GoogleCalendarService
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.data.calendar.ScheduledTaskJson
import com.jebkit.tac.data.tasks.GoogleTasksService
import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskListDao
import com.jebkit.tac.util.toEventDateTime
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.ZonedDateTime


class TasksAndCalendarViewModel(
    credential: GoogleAccountCredential,
    userRecoverableLauncher: ActivityResultLauncher<Intent>
) : ViewModel() {
    val TAG = "TasksAndCalendarViewModel"
    private val _uiState = MutableStateFlow(TasksAndCalendarState())
    val uiState: StateFlow<TasksAndCalendarState> = _uiState.asStateFlow()

    private val googleCalendarService: GoogleCalendarService = GoogleCalendarService(credential)
    private val googleTasksService: GoogleTasksService =
        GoogleTasksService(credential, userRecoverableLauncher)

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

            val addJsonToTasks: MutableList<Deferred<Task>> = mutableListOf()
            taskJobs.awaitAll().forEach { (parentTaskListId, googleTasks) ->
                googleTasks.forEach { googleTask ->
                    if (googleTask.notes == null || !googleTask.notes.contains(TASK_JSON_HEADER)) {

                    }
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
                                googleEvent.description.indexOf(SCHEDULEDTASK_JSON_HEADER)
                                    .plus(SCHEDULEDTASK_JSON_HEADER.length)
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

    fun refresh() {

    }

    fun addScheduledTask(newTask: ScheduledTask) {
        viewModelScope.launch {
            val json = ScheduledTaskJson(newTask.parentTaskId, newTask.completed.value)
            val event = Event()
                .setSummary(newTask.title.value)
                .setDescription(
                    newTask.description.value +
                            "\n" +
                            SCHEDULEDTASK_JSON_HEADER +
                            "\n" +
                            Json.encodeToString(json)
                )
                .setStart(newTask.start.value.toEventDateTime())
                .setEnd(newTask.end.value.toEventDateTime())

            try {
                val response = googleCalendarService.addEvent(event)
                if (response != null) {
                    _uiState.value.googleCalendarState.value.scheduledTasks[response.id] =
                        ScheduledTask(response, json, newTask.description.value)
                    _uiState.value.googleCalendarState.value.googleEvents[response.id] = response

                } else {
                    Log.e(TAG, "Could not add scheduled task")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Could not add scheduled task")
            }
        }
    }

    fun updateScheduledTaskTime(scheduledTask: ScheduledTask, newStartTime: ZonedDateTime) {
        viewModelScope.launch {
            var event = _uiState.value.googleCalendarState.value.googleEvents[scheduledTask.id]
            if (event != null) {
                val oldStartTime = scheduledTask.start.value
                scheduledTask.start.value = newStartTime
                scheduledTask.end.value =
                    newStartTime.plusMinutes(scheduledTask.duration.intValue.toLong())

                event = googleCalendarService.updateEvent(event)
                if (event != null) {
                    _uiState.value.googleCalendarState.value.googleEvents[scheduledTask.id] = event
                } else {
                    Log.e(
                        TAG,
                        "Couldn't update scheduled task time. Reverting changes."
                    )
                    scheduledTask.start.value = oldStartTime
                    scheduledTask.end.value =
                        oldStartTime.plusMinutes(scheduledTask.duration.intValue.toLong())
                }
            } else {
                Log.e(TAG, "Could not find event with id ${scheduledTask.id}")
            }
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

    fun updateEventDaoTime(eventDao: EventDao, startTime: ZonedDateTime) {
        viewModelScope.launch {
            val event = _uiState.value.googleCalendarState.value.googleEvents[eventDao.id]
            if (event != null) {
                val oldStartTime = eventDao.start.value
                eventDao.start.value = startTime
                val newEndTime = startTime.plusMinutes(eventDao.duration.intValue.toLong())
                eventDao.end.value = newEndTime

                try {
                    event.setStart(startTime.toEventDateTime())
                    event.setEnd(newEndTime.toEventDateTime())
                    val response = googleCalendarService.updateEvent(event)
                    if(response != null) _uiState.value.googleCalendarState.value.googleEvents[eventDao.id] = response
                    else {
                        Log.e(
                            TAG,
                            "Couldn't update google calendar for event ${event.summary} with id: ${event.id}. Reverting changes."
                        )
                        eventDao.start.value = oldStartTime
                        eventDao.end.value = oldStartTime.plusMinutes(eventDao.duration.intValue.toLong())
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
    }

    fun updateEventDao(eventDao: EventDao) {

    }

    fun deleteEventDao(eventDao: EventDao) {
//        _uiState.value.events.value -= eventDao
    }
}
