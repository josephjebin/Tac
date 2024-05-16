package com.example.tac.data.calendar

import java.time.ZonedDateTime

data class EventDao(
    var busy: Boolean,
    override var name: String,
    override var start: ZonedDateTime,
    override var end: ZonedDateTime
): Plan(
    name = name,
    start = start,
    end = end
)