package com.jebkit.tac.data.calendar

data class GoogleEvent (
    val summary: String = "",
    val description: String = "",
    val start: String,
    val end: Time = Time(),
    val id: String = "",
    val recurrence: Array<String> = arrayOf("")
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GoogleEvent

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class Time (
    val dateTime: String = "",
    val timeZone: String = ""
)