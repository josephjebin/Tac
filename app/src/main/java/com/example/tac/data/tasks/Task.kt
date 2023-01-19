package com.example.tac.data.tasks

data class Task(
    var kind: String,
    var id: String,
    var etag: String,
    var title: String,
    var updated: String,
    var selfLink: String,
    var parent: String,
    var position: String,
    var notes: String,
    var status: String,
    var due: String,
    var completed: String,
    var deleted: Boolean,
    var hidden: Boolean,
    var links: List<Link>
)

data class Link(var type: String, var description: String, var link: String)