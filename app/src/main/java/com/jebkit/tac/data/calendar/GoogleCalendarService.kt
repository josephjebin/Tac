package com.jebkit.tac.data.calendar


//import net.openid.appauth.AuthState
//import net.openid.appauth.AuthorizationService
//import okhttp3.HttpUrl
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONObject
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
import java.time.LocalDate

//Design decision: try to handle all api exceptions at the lowest level (which is here)
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
        val apiResponse = ArrayList<Event>()
        withContext(Dispatchers.IO) {
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
            try {
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
            } catch (ioException: IOException) {
                Log.e("GoogleCalendarService", ioException.message.toString())
            } catch (exception: Exception) {
                Log.e("GoogleCalendarService", "Unexpected error: " + exception.message.toString())
            }
        }

        return apiResponse
    }

    suspend fun addEvent(event: Event): Event? {
        var result: Event? = null
        withContext(Dispatchers.IO) {
            try {
                result = calendar.events().insert("primary", event).execute()
            } catch (ioException: IOException) {
                Log.e("GoogleCalendarService", ioException.message.toString())
            } catch (exception: Exception) {
                Log.e("GoogleCalendarService", "Unexpected error: " + exception.message.toString())
            }
        }

        return result
    }

    suspend fun updateEvent(event: Event): Event? {
        var result: Event? = null
        withContext(Dispatchers.IO) {
            try {
                result = calendar.events().update("primary", event.id, event).execute()
            } catch (ioException: IOException) {
                Log.e("GoogleCalendarService", ioException.message.toString())
            } catch (exception: Exception) {
                Log.e("GoogleCalendarService", "Unexpected error: " + exception.message.toString())
            }
        }
        return result
    }

    suspend fun deleteEvent(eventId: String): Boolean {
        var result = false
        withContext(Dispatchers.IO) {
            try {
                calendar.events().delete("primary", eventId).execute()
                result = true
            } catch (ioException: IOException) {
                Log.e("GoogleCalendarService", ioException.message.toString())
            } catch (exception: Exception) {
                Log.e("GoogleCalendarService", "Unexpected error: " + exception.message.toString())
            }
        }

        return result
    }
}