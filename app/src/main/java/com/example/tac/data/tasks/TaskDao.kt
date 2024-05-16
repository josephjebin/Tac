package com.example.tac.data.tasks

import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class TaskDao(
    var kind: String = "",
    var id: String = "",
    var etag: String = "",
    var title: String = "",
    var updated: String = "",
    var selfLink: String = "",
    var parent: String = "",
    var position: String = "",
    var notes: String = "",
    var status: String = "",
    var due: String = "",
    var completed: String = "",
    var deleted: Boolean = false,
    var hidden: Boolean = false,
    var links: List<Link> = listOf(Link("", "", "")),
    var taskList: String = "",
    var scheduledDuration: Int = 0,
    var workedDuration: Int = 0,
    var totalDuration: Int = 15,
    var priority: Priority = Priority.Priority4
) {
    constructor(task: Task, taskList: String) : this() {
        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val outputFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        kind = task.kind
        id = task.id
        etag = task.etag
        title = task.title
        updated = task.updated
        selfLink = task.selfLink
        parent = task.parent
        position = task.position
        notes = task.notes
        status = task.status
        due = if (task.due.isEmpty()) "No date"
        else LocalDate.parse(task.due.dropLast(1), inputFormat).format(outputFormat)
        completed = task.completed
        deleted = task.deleted
        hidden = task.hidden
        links = task.links
        this.taskList = taskList
        //TODO: make dynamic
        totalDuration = 15
    }
}