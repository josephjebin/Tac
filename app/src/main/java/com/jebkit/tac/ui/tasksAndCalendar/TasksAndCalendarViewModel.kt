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
import com.jebkit.tac.constants.Constants.Companion.JSON_HEADER
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.GoogleCalendarService
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.data.calendar.ScheduledTaskJson
import com.jebkit.tac.data.tasks.GoogleTasksService
import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskJson
import com.jebkit.tac.data.tasks.TaskListDao
import com.jebkit.tac.util.toEventDateTime
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
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
            refresh()
        }
    }

    private suspend fun refresh() {
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
                _uiState.value.taskListDaos[googleTaskList.id] =
                    TaskListDao(
                        googleTaskList.id,
                        mutableStateOf(googleTaskList.title)
                    )

                taskJobs.add(async {
                    googleTasksService.getTasks(
                        googleTaskList.id
                    )
                })
            }.also {
                _uiState.value.currentSelectedTaskListDao.value =
                    _uiState.value.taskListDaos.values.toList()[0]
            }

            taskJobs.awaitAll().forEach { (parentTaskListId, googleTasks) ->
                googleTasks.forEach { googleTask ->
                    var taskJson = TaskJson(30, mutableListOf())
//                    val headerStartIndex = googleTask.notes?.indexOf(JSON_HEADER) ?: -1
                    var notes = ""
//                    if (googleTask.notes == null || headerStartIndex == -1) {
//                        googleTasksService.updateTask(
//                            parentTaskListId, googleTask.setNotes(
//                                generateTaskNotes(googleTask.notes, taskJson)
//                            )
//                        )
//                    } else {
//                        try {
//                            parseTaskNotes(googleTask.notes, headerStartIndex).also {
//                                notes = it.first
//                                taskJson = it.second
//                            }
//                        } catch (e: Exception) {
//                            //special case: user tinkered with our json
//                            Log.e(
//                                TAG,
//                                "Couldn't parse task's notes for json. Resetting json in notes."
//                            )
//
//                            googleTasksService.updateTask(
//                                parentTaskListId,
//                                googleTask.setNotes(
//                                    googleTask.notes.substring(0, headerStartIndex.plus(JSON_HEADER.length)) +
//                                            Json.encodeToString(taskJson) + ")"
//                                )
//                            )
//                        }
//                    }

                    _uiState.value.taskDaos[googleTask.id] =
                        TaskDao(googleTask, parentTaskListId, taskJson, notes)
                    _uiState.value.tasks[googleTask.id] = googleTask
                }
            }.also {
                googleCalendarData.await().forEach { googleEvent ->
                    //Event
                    if (googleEvent.description == null || !googleEvent.description.contains(
                            JSON_HEADER
                        )
                    ) {
                        _uiState.value.eventDaos[googleEvent.id] = EventDao(googleEvent)
                    }
                    //ScheduledTask
                    else {
                        var scheduledTaskJson = ScheduledTaskJson("Orphaned", false)
                        val jsonStartIndex =
                            googleEvent.description.indexOf(JSON_HEADER)
                                .plus(JSON_HEADER.length)
                        try {
                            val jsonEndIndex =
                                googleEvent.description.indexOf(")", jsonStartIndex)
                            scheduledTaskJson = Json.decodeFromString<ScheduledTaskJson>(
                                googleEvent.description.substring(
                                    jsonStartIndex,
                                    jsonEndIndex
                                ).trim()
                            )
                        } catch (e: Exception) {
                            //JSON parsing error
                            Log.e(
                                TAG,
                                "Couldn't parse event's description for json. Resetting json in description."
                            )
                            googleCalendarService.updateEvent(
                                googleEvent.setDescription(
                                    googleEvent.description.substring(0, jsonStartIndex) +
                                            scheduledTaskJson + ")"
                                )
                            )
                        }

                        val scheduledTask = ScheduledTask(googleEvent, "", scheduledTaskJson)
                        _uiState.value.scheduledTasks[scheduledTask.id] = scheduledTask

                        val parentTask =
                            _uiState.value.taskDaos[scheduledTask.parentTaskId]
                        //if parent task == null, then this scheduled task is orphaned. We'll let it exist, but it won't update a google task
                        if (parentTask != null) {
                            //if data is malformed, Tac may reset the json and orphan a scheduled task.
                            //this next if block will re-associate this scheduled task with its parent task
//                            if (!parentTask.associatedScheduledTaskIds.contains(scheduledTask.id)) {
//                                parentTask.associatedScheduledTaskIds.add(scheduledTask.id)
//                                _uiState.value.tasks[parentTask.id]?.let { googleTask ->
//                                    googleTask.setNotes(
//                                        (googleTask.notes ?: "") +
//                                                "\n" +
//                                                JSON_HEADER +
//                                                Json.encodeToString(
//                                                    TaskJson(
//                                                        parentTask.neededDuration.intValue,
//                                                        parentTask.associatedScheduledTaskIds.toList()
//                                                    )
//                                                ) + ")"
//                                    )
//                                    val updatedGoogleTask = googleTasksService.updateTask(
//                                        parentTask.taskListId.value, googleTask
//                                    )
//                                    if (updatedGoogleTask != null) _uiState.value.tasks[parentTask.id] = updatedGoogleTask
//                                }
//
//                                //don't need to update _uiState.value.taskDaos[parentTask.id] because we update parentTask directly
//                            }

                            if (scheduledTask.completed.value) {
                                val parentTaskWorkedDuration = parentTask.workedDuration.intValue
                                parentTask.workedDuration.intValue =
                                    parentTaskWorkedDuration + scheduledTask.duration.intValue
                            } else {
                                val parentTaskScheduledDuration =
                                    parentTask.scheduledDuration.intValue
                                parentTask.scheduledDuration.intValue =
                                    parentTaskScheduledDuration + scheduledTask.duration.intValue
                            }
                        }
                    }

                    _uiState.value.googleEvents[googleEvent.id] = googleEvent
                }
            }
        }
    }

    fun addTaskListDao(newTaskListDao: TaskListDao) {
        _uiState.value.taskListDaos[newTaskListDao.id] = newTaskListDao
    }

    fun updateCurrentSelectedTaskListDao(newTaskListDao: TaskListDao) {
        _uiState.value.currentSelectedTaskListDao.value = newTaskListDao
    }

    fun updateTaskListDaoTitle(taskListDaoId: String, newTitle: String) {
        _uiState.value.taskListDaos[taskListDaoId]?.title = mutableStateOf(newTitle)
    }

    fun deleteTaskListDao(taskListDaoId: String) {
        _uiState.value.taskListDaos.remove(taskListDaoId)
    }

    fun addEventDao(newEventDao: EventDao) {
        _uiState.value.eventDaos[newEventDao.id] = newEventDao
    }

    //ToDo
    fun updateEventDao() {}

    fun updateEventDaoTime(eventDao: EventDao, startTime: ZonedDateTime) {
        viewModelScope.launch {
            val event = _uiState.value.googleEvents[eventDao.id]
            if (event != null) {
                val oldStartTime = eventDao.start.value
                eventDao.start.value = startTime
                val newEndTime = startTime.plusMinutes(eventDao.duration.intValue.toLong())
                eventDao.end.value = newEndTime

                try {
                    event.setStart(startTime.toEventDateTime())
                    event.setEnd(newEndTime.toEventDateTime())
                    val response = googleCalendarService.updateEvent(event)
                    if (response != null) _uiState.value.googleEvents[eventDao.id] = response
                    else {
                        Log.e(
                            TAG,
                            "Couldn't update google calendar for event ${event.summary} with id: ${event.id}. Reverting changes."
                        )
                        eventDao.start.value = oldStartTime
                        eventDao.end.value =
                            oldStartTime.plusMinutes(eventDao.duration.intValue.toLong())
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

    fun deleteEventDao(eventDaoId: String) {
        _uiState.value.eventDaos.remove(eventDaoId)
    }

    fun addScheduledTask(newTask: ScheduledTask) {
        viewModelScope.launch {
            val event = Event()
                .setSummary(newTask.title.value)
                .setDescription(
                    generateScheduledTaskDescription(
                        newTask.description.value,
                        newTask.parentTaskId,
                        newTask.completed.value
                    )
                )
                .setStart(newTask.start.value.toEventDateTime())
                .setEnd(newTask.end.value.toEventDateTime())

            try {
                val response = googleCalendarService.addEvent(event)
                if (response != null) {
                    val descriptionAndScheduledTaskJson =
                        parseEventDescription(response.description)
                    val addedScheduledTask = ScheduledTask(
                        response,
                        descriptionAndScheduledTaskJson.first,
                        descriptionAndScheduledTaskJson.second
                    )
                    _uiState.value.scheduledTasks[response.id] = addedScheduledTask
                    _uiState.value.googleEvents[response.id] = response
                    val parentTaskDao = _uiState.value.taskDaos[addedScheduledTask.parentTaskId]
                    if (parentTaskDao != null) {
                        if (newTask.completed.value) {
                            val oldParentTaskWorkedDuration = parentTaskDao.workedDuration.intValue
                            parentTaskDao.workedDuration.intValue =
                                oldParentTaskWorkedDuration + addedScheduledTask.duration.intValue
                        } else {
                            val oldParentScheduledDuration =
                                parentTaskDao.scheduledDuration.intValue
                            parentTaskDao.scheduledDuration.intValue =
                                oldParentScheduledDuration + addedScheduledTask.duration.intValue
                        }
                    }
                } else {
                    Log.e(TAG, "Could not add scheduled task")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Could not add scheduled task")
            }
        }
    }

    //ToDo
    fun updateScheduledTask() {}

    fun updateScheduledTaskTime(scheduledTask: ScheduledTask, newStartTime: ZonedDateTime) {
        viewModelScope.launch {
            val oldStartTime = scheduledTask.start.value
            scheduledTask.start.value = newStartTime
            val newEndTime = newStartTime.plusMinutes(scheduledTask.duration.intValue.toLong())
            scheduledTask.end.value = newEndTime

            var event = _uiState.value.googleEvents[scheduledTask.id]
            if (event != null) {
                event = googleCalendarService.updateEvent(
                    event
                        .setStart(newStartTime.toEventDateTime())
                        .setEnd(newEndTime.toEventDateTime())
                )
                if (event != null) {
                    _uiState.value.googleEvents[scheduledTask.id] = event
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

    fun updateScheduledTaskDuration(scheduledTask: ScheduledTask, newDuration: Int) {
        scheduledTask.duration.intValue = newDuration
        scheduledTask.end.value =
            scheduledTask.start.value.plusMinutes(scheduledTask.duration.intValue.toLong())
        val event = _uiState.value.googleEvents[scheduledTask.id]
        if (event != null) {
            event.setEnd(scheduledTask.end.value.toEventDateTime())
            viewModelScope.launch {
                googleCalendarService.updateEvent(event)
            }
        }
    }

    fun deleteScheduledTask(scheduledTaskId: String) {
        _uiState.value.scheduledTasks.remove(scheduledTaskId)
    }

    @Throws(SerializationException::class)
    private fun parseTaskNotes(notes: String, headerStartIndex: Int): Pair<String, TaskJson> {
        val jsonStartIndex = headerStartIndex.plus(JSON_HEADER.length)
        val jsonEndIndex = notes.indexOf(")", jsonStartIndex)
        val taskJson = Json.decodeFromString<TaskJson>(
            notes.substring(
                jsonStartIndex,
                jsonEndIndex
            ).trim()
        )

        return Pair(notes.substring(0, headerStartIndex), taskJson)
    }

    @Throws(SerializationException::class)
    private fun parseEventDescription(description: String): Pair<String, ScheduledTaskJson> {
        val headerIndex = description.indexOf(JSON_HEADER)
        val jsonStartIndex = headerIndex.plus(JSON_HEADER.length)
        val jsonEndIndex = description.indexOf(")", jsonStartIndex)
        val scheduledTaskJson = Json.decodeFromString<ScheduledTaskJson>(
            description.substring(
                jsonStartIndex,
                jsonEndIndex
            ).trim()
        )

        return Pair(description.substring(0, headerIndex), scheduledTaskJson)
    }

    private fun generateTaskNotes(
        notes: String?,
        neededDuration: Int,
        scheduledTasks: List<String>
    ): String {
        return (notes ?: "") +
                "\n" +
                JSON_HEADER +
                Json.encodeToString(TaskJson(neededDuration, scheduledTasks)) +
                ")"
    }

    private fun generateScheduledTaskDescription(
        description: String?,
        parentTaskId: String,
        completed: Boolean
    ): String {
        return (description ?: "") +
                "\n" +
                JSON_HEADER +
                Json.encodeToString(ScheduledTaskJson(parentTaskId, completed)) +
                ")"
    }

//    fun applyChangesToEvent(plan: Plan): Boolean {
//        val event = _uiState.value.googleEvents[plan.id]
//        var appliedChanges = false
//        if(event != null) {
//            if (plan is EventDao) {
//                if(plan.title.value != event.summary) {
//                    event.setSummary(plan.title.value)
//                    appliedChanges = true
//                }
//
//                val planDescription = (plan.description.value?: "") + "\n" + JSON_HEADER + Json.encodeToString()
//                if()
//
//            } else if (plan is ScheduledTask) {
//
//            }
//        }
//        else {
//            Log.e(TAG, "Tried to apply changes to google event but couldn't find event with event id: ${plan.id}")
//        }
//        return appliedChanges
//    }

    fun addTaskDao(newTaskDao: TaskDao) {
        _uiState.value.taskDaos[newTaskDao.id] = newTaskDao
    }

    //ToDo
//    fun updateTaskDao() {}

    fun deleteTaskDao(taskDaoId: String) {
        _uiState.value.taskDaos.remove(taskDaoId)
    }


}
