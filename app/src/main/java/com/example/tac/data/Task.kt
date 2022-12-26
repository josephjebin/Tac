package com.example.tac.data

data class Task(
    val id: Int,
    var text: String,
    var dueDate: String,
    var startTime: String? = "",
    var endTime: String? = "",
    var duration: Int,
    var project: String
)