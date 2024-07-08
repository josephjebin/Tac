package com.example.tac.data.calendar


//import android.util.Log
//import com.example.tac.data.constants.Constants.Companion.URL_CALENDAR
//import com.example.tac.data.constants.Constants.Companion.URL_CALENDAR_WITHOUT_HOST
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
import java.io.IOException


class GoogleCalendarService(private var credential: GoogleAccountCredential) {
    private var calendar: Calendar

    init {
        calendar = initCalendarBuild()
    }

    fun updateCalendarServiceCredentials(newCredential: GoogleAccountCredential) {
        if(credential != newCredential) {
            credential = newCredential
            calendar = initCalendarBuild()
        }
    }

    private fun initCalendarBuild(): Calendar {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        return Calendar.Builder(
            transport, jsonFactory, credential
        )
            .setApplicationName("GetEventCalendar")
            .build()
    }


    suspend fun getEventsFromCalendar(): MutableList<Event> {
        val now = DateTime(System.currentTimeMillis())
        val result = ArrayList<Event>()
        try {
            withContext(Dispatchers.IO) {
                val events = calendar.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()

                val items = events.items
                result.addAll(items)
//                for (event in items) {
//                    var start = event.start.dateTime
//                    if (start == null) {
//                        start = event.start.date
//                    }
//
//                    eventStrings.add(
//                        GoogleEvent(
//                            summary = event.summary,
//                            start = start.toString()
//                        )
//                    )
//                }
            }
        } catch (e: IOException) {
            Log.d("Google", e.message.toString())
            throw e
        }

        return result
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