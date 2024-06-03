package com.example.tac.ui.calendar

import androidx.compose.runtime.mutableStateOf
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
class CalendarViewModel : ViewModel() {
    val TAG = "CalendarViewModel"
    private val _uiState = MutableStateFlow(
        CalendarState()
    )
    val uiState: StateFlow<CalendarState> = _uiState.asStateFlow()
//    var calendarService: CalendarService

    init {
//        calendarService = CalendarService(authState, authorizationService)
        _uiState.value = CalendarState(
            events = mutableStateOf(dummyEvents()),
            scheduledTasks = mutableStateOf(dummyScheduledTasks())
        )
    }

    private fun dummyEvents(): List<EventDao> {
        return listOf(
            EventDao(
                id = 0,
                busy = true,
                name = "Breakfast",
                start = mutableStateOf(
                    ZonedDateTime.of(
                        LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
                        ZoneId.systemDefault()
                    )
                ),
                end = mutableStateOf(
                    ZonedDateTime.of(
                        LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)),
                        ZoneId.systemDefault()
                    )
                )
            ),
            EventDao(
                id = 1,
                busy = true,
                name = "Lunch",
                start = mutableStateOf(
                    ZonedDateTime.of(
                        LocalDateTime.of(LocalDate.now(), LocalTime.NOON),
                        ZoneId.systemDefault()
                    )
                ),
                end = mutableStateOf(
                    ZonedDateTime.of(
                        LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)),
                        ZoneId.systemDefault()
                    )
                )
            ),
            EventDao(
                id = 2,
                busy = true,
                name = "Dinner",
                start = mutableStateOf(
                    ZonedDateTime.of(
                        LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)),
                        ZoneId.systemDefault()
                    )
                ),
                end = mutableStateOf(
                    ZonedDateTime.of(
                        LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 0)),
                        ZoneId.systemDefault()
                    )
                )
            )
        )
    }

    private fun dummyScheduledTasks(): List<ScheduledTask> {
        return listOf(
            ScheduledTask(
                id = 0,
                name = "Apply",
                parentTaskId = "1",
                start = mutableStateOf(
                    ZonedDateTime.of(
                        LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)),
                        ZoneId.systemDefault()
                    )
                ),
                end = mutableStateOf(
                    ZonedDateTime.of(
                        LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
                        ZoneId.systemDefault()
                    )
                ),
                workedDuration = 0,
                color = Color.Gray
            )
        )
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

    fun addScheduledTask(newTask: ScheduledTask) {
        _uiState.value.scheduledTasks.value += newTask
    }

    fun removeScheduledTask(scheduledTask: ScheduledTask) {
        _uiState.value.scheduledTasks.value -= scheduledTask
    }

    fun addEventDao(newEventDao: EventDao) {
        _uiState.value.events.value += newEventDao
    }

    fun removeEventDao(eventDao: EventDao) {
        _uiState.value.events.value -= eventDao
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