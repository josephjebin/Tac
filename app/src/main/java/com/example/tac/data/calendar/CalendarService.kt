package com.example.tac.data.calendar

import android.util.Log
import com.example.tac.data.tasks.Task
import com.example.tac.data.tasks.TaskList
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class CalendarService(val authState: AuthState, val authorizationService: AuthorizationService) {
    val TAG = "CalendarService"
    var mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    suspend fun getCalendarList(): List<String> {
        val result: MutableList<String> = mutableListOf()
        withContext(Dispatchers.IO) {
            authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
                Log.i(TAG, "Trying to get calendars")
                val client = OkHttpClient()
                val request = Request.Builder()
                    .get()
                    .url("https://www.googleapis.com/calendar/v3/users/me/calendarList")
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    var jsonBody = response.body?.string() ?: ""
                    Log.i(TAG, "Response from calendar api: $jsonBody")
                    jsonBody = JSONObject(jsonBody).getString("items")
                    val calendarList = mapper.readValue(jsonBody, object : TypeReference<List<JSONObject>>() {})
                    for(calendar in calendarList) {
                        result += calendar.get("summary").toString()
                    }
                    println("calendars: " + result)
                } catch (e: Exception) {
                    Log.e(TAG, e.toString() + e.cause + e.message + e.localizedMessage + e.stackTraceToString())
                }
            }
        }
        return result
    }

//    suspend fun getTasks(taskList: String): List<Task> {
//        var result: List<Task> = mutableListOf()
//        withContext(Dispatchers.IO) {
//            authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
//                Log.i(TAG, "Trying to get task for id: $taskList")
//                val client = OkHttpClient()
//                val request = Request.Builder()
//                    .get()
//                    .url(Constants.URL_TASKS + "lists/$taskList/tasks")
//                    .addHeader("Authorization", "Bearer $accessToken")
//                    .build()
//
//                try {
//                    val response = client.newCall(request).execute()
//                    var jsonBody = response.body?.string() ?: ""
//                    Log.i(TAG, "Response from tasks api: $jsonBody")
//                    jsonBody = JSONObject(jsonBody).getString("items").toString()
//                    result = mapper.readValue(jsonBody, object : TypeReference<List<Task>>() {})
//                } catch (e: Exception) {
//                    Log.e(TAG, e.toString() + e.cause + e.message + e.localizedMessage + e.stackTraceToString())
//                }
//            }
//        }
//        return result
//    }
}