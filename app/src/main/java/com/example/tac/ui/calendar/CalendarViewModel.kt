package com.example.tac.ui.calendar

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tac.TacApplication
import com.example.tac.data.calendar.CalendarService
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.GoogleCalendar
import com.example.tac.data.constants.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import org.json.JSONException

class CalendarViewModel(authState: AuthState, authorizationService : AuthorizationService):
    ViewModel() {
    val TAG = "CalendarViewModel"
    private val _uiState = MutableStateFlow(CalendarState(listOf(), listOf()))
    val uiState: StateFlow<CalendarState> = _uiState.asStateFlow()
    var calendarService: CalendarService

    init {
        calendarService = CalendarService(authState, authorizationService)
    }

    fun initCalendarsAndEvents() {
        viewModelScope.launch {
            val calendars = calendarService.getCalendarList()
            updateCalendarsState(calendars)
            val events = mutableListOf<EventDao>()
            for(calendar in calendars) {
                calendarService.initEvents(calendar.id, _uiState.value.selectedDate, _uiState.value.constantMaxDate).forEach { events.add(EventDao(it)) }
            }
            updateEventsState(events)
        }
    }

    private fun updateCalendarsState(newCalendars: List<GoogleCalendar>) {
        _uiState.update {calendarState ->
            calendarState.copy(calendars = newCalendars)
        }
    }

    private fun updateEventsState(newEvents: List<EventDao>) {
        _uiState.update {calendarState ->
            calendarState.copy(events = newEvents)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TacApplication)

                var authState = AuthState()
                val jsonString = application
                    .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                    .getString(Constants.AUTH_STATE, null)

                if (jsonString != null && !TextUtils.isEmpty(jsonString)) {
                    try { authState = AuthState.jsonDeserialize(jsonString) }
                    catch (jsonException: JSONException) { }
                }

                val appAuthConfiguration = AppAuthConfiguration.Builder()
                    .setBrowserMatcher(
                        BrowserAllowList(
                            VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                            VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
                        )
                    ).build()

                val authorizationService = AuthorizationService(
                    application,
                    appAuthConfiguration)

                CalendarViewModel(authState, authorizationService)
            }
        }
    }
}