package com.jebkit.tac.ui.tasksAndCalendar

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.google.api.services.calendar.model.Event
import com.google.api.services.tasks.model.Task
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskListDao
import java.time.LocalDate

//Tasks and calendar data are stored together because they both are dependent on the selected dates
data class TasksAndCalendarState(
    val calendarLayout: MutableState<CalendarLayout> = mutableStateOf(CalendarLayout.ONE_DAY),
    val minSelectedDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now()),
    val minBufferDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now().minusWeeks(1)),
    val maxBufferDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now().plusWeeks(1)),

    //CALENDAR DATA
    val calendars: SnapshotStateList<EventDao> = mutableStateListOf(),
    //used to interact with Google Calendar API
    //map of EventId to Google Event
    val googleEvents: SnapshotStateMap<String, Event> = mutableStateMapOf(),
    //map of EventDaos shown in Tac's calendar
    //map of EventId to EventDaos
    val eventDaos: SnapshotStateMap<String, EventDao> = mutableStateMapOf(),
    //map of a map... used for associating Google Task Id with ScheduledTasks
    //map of ParentTaskId to map of EventId to ScheduledTask
    val scheduledTasks: SnapshotStateMap<String, ScheduledTask> = mutableStateMapOf(),

    //TASKS DATA
    //map of id to TaskListDao
    val taskListDaos: SnapshotStateMap<String, TaskListDao> = mutableStateMapOf(),
    val tasks: SnapshotStateMap<String, Task> = mutableStateMapOf(),
    //map of id to taskDao
    val taskDaos: SnapshotStateMap<String, TaskDao> = mutableStateMapOf(),
    val currentSelectedTaskListDao: MutableState<TaskListDao?> = mutableStateOf(null),
    val currentSelectedTaskDao: MutableState<TaskDao?> = mutableStateOf(null)
)

enum class CalendarLayout {
    ONE_DAY,
    THREE_DAYS,
    FIVE_DAYS,
    ONE_WEEK,
    TWO_WEEKS,
    ONE_MONTH
}
