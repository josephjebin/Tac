package com.example.tac.data.tasks

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
    var taskList: String = ""
) {
    constructor(task: Task, taskList: String) : this(
        kind = task.kind,
        id = task.id,
        etag = task.etag,
        title = task.title,
        updated = task.updated,
        selfLink = task.selfLink,
        parent = task.parent,
        position = task.position,
        notes = task.notes,
        status = task.status,
        due = task.due,
        completed = task.completed,
        deleted = task.deleted,
        hidden = task.hidden,
        links = task.links,
        taskList = taskList
    )
}