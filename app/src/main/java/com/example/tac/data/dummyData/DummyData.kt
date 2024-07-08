package com.example.tac.data.dummyData

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.ScheduledTask
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun dummyEvents(): List<EventDao> {
    return listOf(
        EventDao(
            id = 0,
            busy = true,
            title = "Breakfast",
            start = mutableStateOf(
                ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
                    ZoneId.systemDefault()
                )
            ),
            end = mutableStateOf(
                ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)),
                    ZoneId.systemDefault()
                )
            )
        ),
        EventDao(
            id = 1,
            busy = true,
            title = "Lunch",
            start = mutableStateOf(
                ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.NOON),
                    ZoneId.systemDefault()
                )
            ),
            end = mutableStateOf(
                ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)),
                    ZoneId.systemDefault()
                )
            )
        ),
        EventDao(
            id = 2,
            busy = true,
            title = "Dinner",
            start = mutableStateOf(
                ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)),
                    ZoneId.systemDefault()
                )
            ),
            end = mutableStateOf(
                ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 0)),
                    ZoneId.systemDefault()
                )
            )
        )
    )
}

public fun dummyScheduledTasks(): List<ScheduledTask> {
    return listOf(
        ScheduledTask(
            id = 0,
            name = "Work",
            parentTaskId = "1",
            start = mutableStateOf(
                ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0)),
                    ZoneId.systemDefault()
                )
            ),
            end = mutableStateOf(
                ZonedDateTime.of(
                    LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0)),
                    ZoneId.systemDefault()
                )
            ),
            workedDuration = 0,
            color = Color.Gray
        ),
//            ScheduledTask(
//                id = 0,
//                name = "Apply",
//                parentTaskId = "1",
//                start = mutableStateOf(
//                    ZonedDateTime.of(
//                        LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)),
//                        ZoneId.systemDefault()
//                    )
//                ),
//                end = mutableStateOf(
//                    ZonedDateTime.of(
//                        LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
//                        ZoneId.systemDefault()
//                    )
//                ),
//                workedDuration = 0,
//                color = Color.Gray
//            )
    )
}
