package com.example.tac.data.tasks

import retrofit2.http.GET

class TasksService {


    suspend fun getTaskLists() {

    }

    suspend fun getTasks() {

    }

    interface TaskServiceInterface {
        @GET()
        fun getTaskLists(): List<TaskList>
    }
}