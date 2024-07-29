package com.jebkit.tac.data.calendar


//import android.util.Log
//import com.jebkit.tac.data.constants.Constants.Companion.URL_CALENDAR
//import com.jebkit.tac.data.constants.Constants.Companion.URL_CALENDAR_WITHOUT_HOST
//import com.fasterxml.jackson.core.type.TypeReference
//import com.fasterxml.jackson.databind.DeserializationFeature
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.google.api.client.util.DateTime
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import net.openid.appauth.AuthState
//import net.openid.appauth.AuthorizationService
//import okhttp3.HttpUrl
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONObject
//import java.time.ZonedDateTime
//import java.util.*
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Date


class GoogleCalendarService(private var credential: GoogleAccountCredential) {
    private var calendar: Calendar

    init {
        calendar = initCalendar()
    }

    fun updateCalendarServiceCredentials(newCredential: GoogleAccountCredential) {
        if (credential != newCredential) {
            credential = newCredential
            calendar = initCalendar()
        }
    }

    private fun initCalendar(): Calendar {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        return Calendar.Builder(
            transport, jsonFactory, credential
        )
            .setApplicationName("Tac")
            .build()
    }

    suspend fun getEvents(
        minDate: LocalDate,
        maxDate: LocalDate
    ): ArrayList<Event> {
        //have to use Calendar to work with Google's Date
        val minCalendar = java.util.Calendar.getInstance()
        minCalendar.clear()
        val maxCalendar = java.util.Calendar.getInstance()
        maxCalendar.clear()
        //subtract 1 from month values because calendar's month is 0-indexed (e.g. January is 0)
        minCalendar.set(minDate.year, minDate.monthValue - 1, minDate.dayOfMonth, 0, 0, 0)
        maxCalendar.set(maxDate.year, maxDate.monthValue - 1, maxDate.dayOfMonth, 23, 59, 59)
        val minDateTime = DateTime(minCalendar.time)
        val maxDateTime = DateTime(maxCalendar.time)
        val apiResponse = ArrayList<Event>()
        try {
            withContext(Dispatchers.IO) {
                val events = calendar
                    .events()
                    .list("primary")
                    .setTimeMin(minDateTime)
                    .setSingleEvents(true)
                    .setTimeMax(maxDateTime)
                    .setOrderBy("startTime")
                    .execute()
                    .items

                apiResponse.addAll(events)
            }
        } catch (e: Exception) {
            Log.d("GoogleCalendarService", e.message.toString())
            throw e
        }

        return apiResponse
    }

    suspend fun updateEventTime(event: Event): Event {
        var result: Event
        withContext(Dispatchers.IO) {
            result = calendar.events().update("primary", event.id, event).execute()
        }
        return result
    }

    suspend fun updateEvent() {

    }
}


//class CalendarService(val authState: AuthState, val authorizationService: AuthorizationService) {
//    val TAG = "CalendarService"
//    var mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//
//    suspend fun getCalendarList(): List<GoogleCalendar> {
//        var result: List<GoogleCalendar> = mutableListOf()
//        withContext(Dispatchers.IO) {
//            authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
//                Log.i(TAG, "Trying to get calendars")
//                val client = OkHttpClient()
//                val request = Request.Builder()
//                    .get()
//                    .url(URL_CALENDAR + "users/me/calendarList")
//                    .addHeader("Authorization", "Bearer $accessToken")
//                    .build()
//
//                try {
//                    val response = client.newCall(request).execute()
//                    var jsonBody = response.body?.string() ?: ""
//                    Log.i(TAG, "Response from calendar api: $jsonBody")
//                    jsonBody = JSONObject(jsonBody).getString("items")
//                    result = mapper.readValue(
//                        jsonBody,
//                        object : TypeReference<List<GoogleCalendar>>() {})
//                } catch (e: Exception) {
//                    Log.e(
//                        TAG,
//                        e.toString() + e.cause + e.message + e.localizedMessage + e.stackTraceToString()
//                    )
//                }
//            }
//        }
//        return result
//    }
//
//    suspend fun initEvents(
//        calendarId: String,
//        startDate: ZonedDateTime,
//        endDate: ZonedDateTime
//    ): List<GoogleEvent> {
//        var result: List<GoogleEvent> = mutableListOf()
//        withContext(Dispatchers.IO) {
//            authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
//                Log.i(TAG, "Trying to get events for id: $calendarId")
//
//                val client = OkHttpClient()
//                val url = HttpUrl
//                    .Builder()
//                    .scheme("https")
//                    .host(URL_CALENDAR_WITHOUT_HOST)
//                    .addPathSegment("calendar")
//                    .addPathSegment("v3")
//                    .addPathSegment("calendars")
//                    .addPathSegment(calendarId)
//                    .addPathSegment("events")
//                    .addQueryParameter("singleEvents", "true")
//                    .addQueryParameter("timeMin", DateTime(Date.from(startDate.toInstant())).toString())
//                    .addQueryParameter("timeMax", DateTime(Date.from(endDate.toInstant())).toStringRfc3339())
//                    .build()
//
//                val request = Request.Builder()
//                    .get()
//                    .url(url)
//                    .addHeader("Authorization", "Bearer $accessToken")
//                    .build()
//
//                try {
//                    val response = client.newCall(request).execute()
//                    var jsonBody = response.body?.string() ?: ""
//                    Log.i(TAG, "Response from calendar api for calendar $calendarId: $jsonBody")
//                    jsonBody = JSONObject(jsonBody).getString("items").toString()
//                    result = mapper.readValue(jsonBody, object : TypeReference<List<GoogleEvent>>() {})
//                } catch (e: Exception) {
//                    Log.e(
//                        TAG,
//                        e.toString() + e.cause + e.message + e.localizedMessage + e.stackTraceToString()
//                    )
//                }
//            }
//        }
//        return result
//    }
//
//    suspend fun getEvents(calendarId: String): List<GoogleEvent> {
//        var result: List<GoogleEvent> = mutableListOf()
//        withContext(Dispatchers.IO) {
//            authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
//                Log.i(TAG, "Trying to get events for id: $calendarId")
//                val client = OkHttpClient()
//                val request = Request.Builder()
//                    .get()
//                    .url(URL_CALENDAR + "calendars/$calendarId/events")
//                    .addHeader("Authorization", "Bearer $accessToken")
//                    .build()
//
//                try {
//                    val response = client.newCall(request).execute()
//                    var jsonBody = response.body?.string() ?: ""
//                    Log.i(TAG, "Response from calendar api for calendar $calendarId: $jsonBody")
//                    jsonBody = JSONObject(jsonBody).getString("items").toString()
//                    result =
//                        mapper.readValue(jsonBody, object : TypeReference<List<GoogleEvent>>() {})
//                } catch (e: Exception) {
//                    Log.e(
//                        TAG,
//                        e.toString() + e.cause + e.message + e.localizedMessage + e.stackTraceToString()
//                    )
//                }
//            }
//        }
//        return result
//    }
//}