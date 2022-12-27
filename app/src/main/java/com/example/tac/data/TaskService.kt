package com.example.tac.data

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.TaskList
import retrofit2.http.GET


class TaskService {
    private val APPLICATION_NAME = "Tac"
    val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    val googleClient = GoogleClient()

    // Build a new authorized API client service.
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val service =
        Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleClient.getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME).build()

    @GET
    fun getTaskLists(): MutableList<TaskList> {
        return service.tasklists().list().execute().items
    }

    @GET
    suspend fun getTasks() {

    }
}