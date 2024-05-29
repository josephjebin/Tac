package com.example.tac.ui.calendar

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.GoogleCalendar
import com.example.tac.data.calendar.ScheduledTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

//class CalendarViewModel(authState: AuthState, authorizationService : AuthorizationService):
class CalendarViewModel() : ViewModel() {
    val TAG = "CalendarViewModel"
    private val _uiState = MutableStateFlow(
        CalendarState(
            calendars = listOf(),
            events = dummyEvents(),
            scheduledTasks = dummyScheduledTasks()
        )
    )
    val uiState: StateFlow<CalendarState> = _uiState.asStateFlow()

    private fun dummyEvents(): MutableList<EventDao> {
        return mutableListOf(
            EventDao(
                id = 0,
                busy = true,
                name = "Breakfast",
                start = ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
                    ZoneId.systemDefault()
                ),
                end = ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)),
                    ZoneId.systemDefault()
                )
            ),
            EventDao(
                id = 1,
                busy = true,
                name = "Lunch",
                start = ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.NOON),
                    ZoneId.systemDefault()
                ),
                end = ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)),
                    ZoneId.systemDefault()
                )
            ),
            EventDao(
                id = 2,
                busy = true,
                name = "Dinner",
                start = ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)),
                    ZoneId.systemDefault()
                ),
                end = ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 0)),
                    ZoneId.systemDefault()
                )
            )
        )
    }

    private fun dummyScheduledTasks(): MutableList<ScheduledTask> {
        return mutableListOf(
            ScheduledTask(
                id = 0,
                name = "Apply",
                parentTaskId = "1",
                start = ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)),
                    ZoneId.systemDefault()
                ),
                end = ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
                    ZoneId.systemDefault()
                ),
                workedDuration = 0,
                color = Color.Gray
            )
        )
    }


//    var calendarService: CalendarService

//    init {
//        calendarService = CalendarService(authState, authorizationService)
//    }

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

    private fun updateCalendarsState(newCalendars: List<GoogleCalendar>) {
        _uiState.update { calendarState ->
            calendarState.copy(calendars = newCalendars)
        }
    }

    private fun updateEventsState(newEvents: MutableList<EventDao>) {
        _uiState.update { calendarState ->
            calendarState.copy(events = newEvents)
        }
    }

    fun addScheduledTask(newTask: ScheduledTask) {
        _uiState.value.scheduledTasks.add(newTask)
    }

    fun removeScheduledTaskWithId(scheduledTaskId: Int) {
        _uiState.value.scheduledTasks.removeIf { task -> task.id == scheduledTaskId }
    }

    fun addEventDao(newEventDao: EventDao) {
        _uiState.value.events.add(newEventDao)
    }

    fun removeEventDaoWithId(eventDaoId: Int) {
        _uiState.value.events.removeIf { eventDao -> eventDao.id == eventDaoId }
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
//                CalendarViewModel(authState, authorizationService)
//            }
//        }
//    }
}