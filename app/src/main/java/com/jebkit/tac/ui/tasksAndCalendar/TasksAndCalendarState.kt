package com.jebkit.tac.ui.tasksAndCalendar

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.google.api.services.calendar.model.Event
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.ui.tasks.GoogleTasksState
import java.time.LocalDate

//Tasks and calendar data are stored together because they both are dependent on the selected dates
data class TasksAndCalendarState(
    val calendarLayout: MutableState<CalendarLayout> = mutableStateOf(CalendarLayout.ONE_DAY),
    //minSelectedDate will be used for one day calendar view
    val minSelectedDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now()),
    val maxSelectedDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now()),
    val minBufferDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now().minusDays(3)),
    //maxBufferDate will also represent maxTasksDate
    val maxBufferDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now().plusDays(3)),
    val minTasksAndEventsDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now().minusDays(3).minusWeeks(6)),
    val maxEventsDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now().plusDays(3).plusWeeks(6)),
    val googleCalendarState: MutableState<GoogleCalendarState> = mutableStateOf(GoogleCalendarState.Success()),
    val googleTasksState: MutableState<GoogleTasksState> = mutableStateOf(GoogleTasksState())
)

enum class CalendarLayout {
    ONE_DAY,
    THREE_DAYS,
    FIVE_DAYS,
    ONE_WEEK,
    TWO_WEEKS,
    ONE_MONTH
}

sealed interface GoogleCalendarState {
    data class Success(
        //need to add Calendar data type. stub with event dao for now
        val calendars: SnapshotStateList<EventDao> = mutableStateListOf(),
        //used to interact with Google Calendar API
        //map of EventId to Google Event
        val googleEvents: SnapshotStateMap<String, Event> = mutableStateMapOf(),
        //map of EventDaos shown in Tac's calendar
        //map of EventId to EventDaos
        val eventDaos: SnapshotStateMap<String, EventDao> = mutableStateMapOf(),
        //map of a map... used for associating Google Task Id with ScheduledTasks
        //map of ParentTaskId to map of EventId to ScheduledTask
        val scheduledTasks: SnapshotStateMap<String, SnapshotStateMap<String, ScheduledTask>> = mutableStateMapOf(),
    ) : GoogleCalendarState

    data class Error(val exception: Exception) : GoogleCalendarState
}