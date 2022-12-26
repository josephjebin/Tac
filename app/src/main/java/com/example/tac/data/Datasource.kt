package com.example.tac.data

import java.text.SimpleDateFormat
import java.util.*

class Datasource {
    val formatter = SimpleDateFormat("M d", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val testDate = formatter.format(calendar.time)

    val listOfTasks = listOf<Task>(
        Task(1, "Job", dueDate = testDate, duration = 480, project = "Career"),
        Task(2, "LeetCode", dueDate = testDate, duration = 60, project = "Career"),
        Task(3, "Rest", dueDate = testDate, duration = 30, project = "Fun"),
        Task(4, "Workout", dueDate = testDate, duration = 90, project = "Fitness")
    )
}