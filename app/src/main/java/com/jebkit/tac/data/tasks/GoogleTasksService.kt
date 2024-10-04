package com.jebkit.tac.data.tasks

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.Task
import com.google.api.services.tasks.model.TaskList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class GoogleTasksService(
    private var credential: GoogleAccountCredential,
    private val userRecoverableLauncher: ActivityResultLauncher<Intent>
) {
    private val TAG = "GoogleTasksService"
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

        withContext(Dispatchers.IO) {
            try {
                val taskLists = tasksService
                    .tasklists()
                    .list()
                    .execute()
                    .items

                apiResponse.addAll(taskLists)
            } catch (recoverableException: UserRecoverableAuthIOException) {
                userRecoverableLauncher.launch(recoverableException.intent)
            } catch (ioException: IOException) {
                Log.e("GoogleCalendarService", "Error getting task lists: ${ioException.message}")
            } catch (exception: Exception) {
                Log.e(
                    "GoogleCalendarService",
                    "Unexpected error getting task lists: ${exception.message}"
                )
            }
        }

        return apiResponse
    }

    suspend fun getTasks(taskListId: String): Pair<String, ArrayList<Task>> {
        val apiResponse = ArrayList<Task>()

        withContext(Dispatchers.IO) {
            try {
                val tasks = tasksService
                    .tasks()
                    .list(taskListId)
                    .execute()
                    .items

                apiResponse.addAll(tasks)
            } catch (recoverableException: UserRecoverableAuthIOException) {
                userRecoverableLauncher.launch(recoverableException.intent)
            } catch (ioException: IOException) {
                Log.e("GoogleCalendarService", "Error getting task lists: ${ioException.message}")
            } catch (exception: Exception) {
                Log.e(
                    "GoogleCalendarService",
                    "Unexpected error getting task lists: ${exception.message}"
                )
            }
        }

        return Pair(taskListId, apiResponse)
    }

    suspend fun updateTask(taskListId: String, task: Task): Task? {
        var apiResponse: Task? = null
        withContext(Dispatchers.IO) {
            try {
                apiResponse = tasksService.tasks().update(taskListId, task.id, task).execute()
            }
            catch (e: Exception) {
                Log.e(TAG, "Error updating google task: ${e.message}")
            }
        }

        return apiResponse
    }

//    fun associateScheduledTask(task: Task, scheduledTaskId: String) {
//
//    }
}