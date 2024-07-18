package com.jebkit.tac.ui.calendar

import android.util.Log
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

import kotlinx.coroutines.launch


class TasksAndCalendarViewModel(credential: GoogleAccountCredential) : ViewModel() {
//    var maxLocalDate = LocalDate.of(maxYear, maxMonth, 1)
//        maxLocalDate = maxLocalDate.withDayOfMonth(maxLocalDate.month.length(maxLocalDate.isLeapYear))
    val TAG = "TasksAndCalendarViewModel"
    private val _uiState = MutableStateFlow(TasksAndCalendarState())
    val uiState: StateFlow<TasksAndCalendarState> = _uiState.asStateFlow()

    private val googleCalendarService: GoogleCalendarService

    init {
        googleCalendarService = GoogleCalendarService(credential)
        readGoogleCalendarForSpecificYearAndMonthRange(
            minYear = _uiState.value.minSelectedDate.value.minusMonths(1).year,
            minMonth = _uiState.value.minSelectedDate.value.minusMonths(1).monthValue,
            maxYear = _uiState.value.minSelectedDate.value.plusMonths(1).year,
            maxMonth = _uiState.value.minSelectedDate.value.plusMonths(1).monthValue,
        )
    }

    fun readGoogleCalendarForSpecificYearAndMonthRange(
        minYear: Int,
        minMonth: Int,
        maxYear: Int,
        maxMonth: Int
    ) {
        Log.i("TasksAndCalendarViewModel", "getting events")
        viewModelScope.launch {
            try {
                //update calendarS

                _uiState.value.googleCalendarState.value = GoogleCalendarState.Success()
                googleCalendarService
                    .getEvents(
                        minYear = minYear,
                        minMonth = minMonth,
                        maxYear = maxYear,
                        maxMonth = maxMonth
                    )
                    .forEach { googleEvent ->
                        //these events are a mixed bag of EventDaos and ScheduledTasks.
                        //we will separate them and update our viewmodel accordingly
                        Log.i(
                            "TasksAndCalendarViewModel",
                            "got results from Calendar: ${googleEvent.summary}"
                        )
                        if (googleEvent.description.contains("parentTaskId"))
                            (_uiState.value.googleCalendarState.value as GoogleCalendarState.Success).scheduledTasks.add(
                                ScheduledTask(googleEvent)
                            )
                        else (_uiState.value.googleCalendarState.value as GoogleCalendarState.Success).events.add(
                            EventDao(googleEvent)
                        )
                    }
                Log.i("TasksAndCalendarViewModel", "finished refreshing")
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
        //call calendar api to add scheduled task event
        //add response to view model because response contains proper id
//        _uiState.value.googleCalendarState.value  += newTask
    }

    fun removeScheduledTask(scheduledTask: ScheduledTask) {
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