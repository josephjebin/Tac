package com.example.tac.data

import com.example.tac.data.tasks.Task
import java.text.SimpleDateFormat
import java.util.*

class Datasource {
    val formatter = SimpleDateFormat("M d", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val testDate = formatter.format(calendar.time)

    val listOfTasks = listOf<Task>(

    )
}