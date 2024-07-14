package com.jebkit.tac.data.tasks
//
//
//import android.util.Log
//import com.jebkit.tac.data.constants.Constants
//import com.fasterxml.jackson.core.type.TypeReference
//import com.fasterxml.jackson.databind.ObjectMapper
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import net.openid.appauth.AuthState
//import net.openid.appauth.AuthorizationService
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONObject
//
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.Task
import com.google.api.services.tasks.model.TaskList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class GoogleTasksService(private var credential: GoogleAccountCredential) {
    private var tasksService: Tasks

    init {
        tasksService = initTasksService()
    }

    fun updateTasksServiceCredentials(newCredential: GoogleAccountCredential) {
        if (credential != newCredential) {
            credential = newCredential
            tasksService = initTasksService()
        }
    }

    private fun initTasksService(): Tasks {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()
        return Tasks.Builder(
            transport, jsonFactory, credential
        )
            .setApplicationName("Tac")
            .build()
    }

    suspend fun getTaskLists(): ArrayList<TaskList> {
        val apiResponse = ArrayList<TaskList>()

        try {
            withContext(Dispatchers.IO) {
                val taskLists = tasksService
                    .tasklists()
                    .list()
                    .execute()
                    .items

                apiResponse.addAll(taskLists)
            }
        } catch (e: Exception) {
            Log.d("GoogleTasksService", "Error getting task lists: ${e.message}. ${e.cause}")
        }

        return apiResponse
    }


    //returns all tasks that are overdue or can be done during the specified year and month
    suspend fun getTasksForSpecificYearAndMonth(
        taskList: String,
        minYear: Int,
        minMonth: Int,
        maxYear: Int,
        maxMonth: Int
    ): ArrayList<Task> {
        //have to use Calendar to work with Google's Date
        var localDateLastDateOfMonth = LocalDate.of(maxYear, maxMonth, 1)
        localDateLastDateOfMonth = localDateLastDateOfMonth.withDayOfMonth(localDateLastDateOfMonth.month.length(localDateLastDateOfMonth.isLeapYear))

        val calendarFirstDateOfSpecifiedYearAndMonth = java.util.Calendar.getInstance()
        calendarFirstDateOfSpecifiedYearAndMonth.set(minYear, minMonth, 1, 0, 0, 0)
        val calendarLastDateOfSpecifiedYearAndMonth = java.util.Calendar.getInstance()
        calendarLastDateOfSpecifiedYearAndMonth.set(maxYear, maxMonth, localDateLastDateOfMonth.dayOfMonth, 23, 59, 59)

        val dateTimeFirstDateOfMonth = DateTime(calendarFirstDateOfSpecifiedYearAndMonth.time)
        val dateTimeLastDateOfMonth = DateTime(calendarLastDateOfSpecifiedYearAndMonth.time)
        val apiResponse = ArrayList<Task>()

        try {
            withContext(Dispatchers.IO) {
                val tasks = tasksService
                    .tasks()
                    .list(taskList)
                    .setDueMin(dateTimeFirstDateOfMonth.toStringRfc3339())
                    .setDueMax(dateTimeLastDateOfMonth.toStringRfc3339())
                    .execute()
                    .items

                apiResponse.addAll(tasks)
            }
        } catch (e: Exception) {
            Log.d("GoogleCalendarService", e.message.toString())
            throw e
        }

        return apiResponse
    }
}


//class TasksService(val authState: AuthState, val authorizationService: AuthorizationService) {
//    val TAG = "TasksService"
//    var mapper = ObjectMapper()
//
//    suspend fun getTaskLists(): List<TaskList> {
//        var result: List<TaskList> = mutableListOf()
//        withContext(Dispatchers.IO) {
//            authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
//                Log.i(TAG, "Trying to get tasklists")
//                val client = OkHttpClient()
//                val request = Request.Builder()
//                    .get()
//                    .url(Constants.URL_TASKS + "users/@me/lists")
//                    .addHeader("Authorization", "Bearer $accessToken")
//                    .build()
//
//                try {
//                    val response = client.newCall(request).execute()
//                    var jsonBody = response.body?.string() ?: ""
//                    Log.i(TAG, "Response from tasks api: $jsonBody")
//                    jsonBody = JSONObject(jsonBody).getString("items").toString()
//                    result = mapper.readValue(jsonBody, object : TypeReference<List<TaskList>>() {})
//                } catch (e: Exception) {
//                    Log.e(TAG, e.toString() + e.cause + e.message + e.localizedMessage + e.stackTraceToString())
//                }
//            }
//        }
//        return result
//    }
//
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
//}
