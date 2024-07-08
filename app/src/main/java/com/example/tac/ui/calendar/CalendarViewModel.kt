package com.example.tac.ui.calendar

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.GoogleCalendarService
import com.example.tac.data.calendar.ScheduledTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.tac.data.dummyData.dummyEvents
import com.example.tac.data.dummyData.dummyScheduledTasks
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import kotlinx.coroutines.flow.update

import kotlinx.coroutines.launch
import java.time.LocalDate



class CalendarViewModel(credential: GoogleAccountCredential) : ViewModel() {
    val TAG = "CalendarViewModel"
    private val _uiState = MutableStateFlow(CalendarState())
    val uiState: StateFlow<CalendarState> = _uiState.asStateFlow()

    private val googleCalendarService: GoogleCalendarService

    init {
        googleCalendarService = GoogleCalendarService(credential)
        refreshEvents()
    }

    fun refreshEvents() {
        viewModelScope.launch {
            try {
                //these events are a mixed bag of EventDaos and ScheduledTasks.
                //we will separate them and update our viewmodel accordingly
                val events = googleCalendarService.getEventsFromCalendar()
                val eventDaos =
                events.forEach { googleEvent ->
                    //if scheduled task
                    if(googleEvent.description.contains("parentTaskId:")) {

                    }
                }
                if(_uiState.value.googleCalendarState.value is GoogleCalendarState.Success) {
                    (_uiState.value.googleCalendarState.value as GoogleCalendarState.Success).events = events

                } else {

                }
            } catch (e: Exception) {
                _uiState.value.googleCalendarState.value = GoogleCalendarState.Error(e)
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