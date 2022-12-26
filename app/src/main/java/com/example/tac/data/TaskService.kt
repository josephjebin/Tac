package com.example.tac.data

import retrofit2.http.GET


class TaskService {
    private val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com/"

    @GET
    suspend fun getProjects() {

    }

    @GET
    suspend fun getTasks() {

    }
}