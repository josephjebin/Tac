package com.jebkit.tac.data.dummyData

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskListDao
import java.time.ZonedDateTime

//fun dummyEvents(): List<EventDao> {
//    return listOf(
//        EventDao(
//            id = 0,
//            busy = true,
//            title = "Breakfast",
//            start = mutableStateOf(
//                ZonedDateTime.of(
//                    LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)),
//                    ZoneId.systemDefault()
//                )
//            ),
//            end = mutableStateOf(
//                ZonedDateTime.of(
//                    LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)),
//                    ZoneId.systemDefault()
//                )
//            )
//        ),
//        EventDao(
//            id = 1,
//            busy = true,
//            title = "Lunch",
//            start = mutableStateOf(
//                ZonedDateTime.of(
//                    LocalDateTime.of(LocalDate.now(), LocalTime.NOON),
//                    ZoneId.systemDefault()
//                )
//            ),
//            end = mutableStateOf(
//                ZonedDateTime.of(
//                    LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)),
//                    ZoneId.systemDefault()
//                )
//            )
//        ),
//        EventDao(
//            id = 2,
//            busy = true,
//            title = "Dinner",
//            start = mutableStateOf(
//                ZonedDateTime.of(
//                    LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0)),
//                    ZoneId.systemDefault()
//                )
//            ),
//            end = mutableStateOf(
//                ZonedDateTime.of(
//                    LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 0)),
//                    ZoneId.systemDefault()
//                )
//            )
//        )
//    )
//}

//public fun dummyScheduledTasks(): List<ScheduledTask> {
//    return listOf(
//        ScheduledTask(
//            id = 0,
//            name = "Work",
//            parentTaskId = "1",
//            start = mutableStateOf(
//                ZonedDateTime.of(
//                    LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0)),
//                    ZoneId.systemDefault()
//                )
//            ),
//            end = mutableStateOf(
//                ZonedDateTime.of(
//                    LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0)),
//                    ZoneId.systemDefault()
//                )
//            ),
//            workedDuration = 0,
//            color = Color.Gray
//        ),
////            ScheduledTask(
////                id = 0,
////                name = "Apply",
////                parentTaskId = "1",
////                start = mutableStateOf(
////                    ZonedDateTime.of(
////                        LocalDateTime.of(LocalDate.now(), LocalTime.of(7, 0)),
////                        ZoneId.systemDefault()
////                    )
////                ),
////                end = mutableStateOf(
////                    ZonedDateTime.of(
////                        LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
////                        ZoneId.systemDefault()
////                    )
////                ),
////                workedDuration = 0,
////                color = Color.Gray
////            )
//    )
//}

fun dummyDataTaskListDaos(): List<TaskListDao> {
    return listOf(
        TaskListDao(id = "sewingTaskListDaoId", title = mutableStateOf("Sewing")),
//        TaskListDao(id = "financeTaskListDaoId", title = mutableStateOf("Finance")),
//        TaskListDao(id = "fitnessTaskListDaoId", title = mutableStateOf("Fitness"))
    )
}

fun dummyDataTasksDaos(): List<TaskDao> {
    return listOf(
        TaskDao(
            id = "1",
            title = mutableStateOf("Re-hem pants"),
            notes = mutableStateOf(null),
            completed = mutableStateOf(false),
            due = mutableStateOf(
                ZonedDateTime.now()
            ),
            deleted = mutableStateOf(false),
            taskListId = mutableStateOf("sewingTaskListDaoId"),
            scheduledDuration = mutableIntStateOf(0),
            workedDuration = mutableIntStateOf(0),
            neededDuration = mutableIntStateOf(45)
        ),
        TaskDao(
            id = "2",
            title = mutableStateOf("Alter black pants"),
            notes = mutableStateOf(null),
            completed = mutableStateOf(false),
            due = mutableStateOf(
                ZonedDateTime.now()
            ),
            deleted = mutableStateOf(false),
            taskListId = mutableStateOf("sewingTaskListDaoId"),
            neededDuration = mutableIntStateOf(120)
        ),
        TaskDao(
            id = "3",
            title = mutableStateOf("Patch Prishnee's ripped pants"),
            notes = mutableStateOf(null),
            completed = mutableStateOf(false),
            due = mutableStateOf(
                ZonedDateTime.now()
            ),
            deleted = mutableStateOf(false),
            taskListId = mutableStateOf("sewingTaskListDaoId"),
            neededDuration = mutableIntStateOf(45)
        ),
//        TaskDao(
//            id = "4",
//            title = mutableStateOf("Get a job"),
//            notes = mutableStateOf("notes"),
//            completed = mutableStateOf(false),
//            due = mutableStateOf(
//                ZonedDateTime.now()
//            ),
//            deleted = mutableStateOf(false),
//            taskListId = mutableStateOf("financeTaskListDaoId"),
//            neededDuration = mutableIntStateOf(45)
//        ),
//        TaskDao(
//            id = "5",
//            title = mutableStateOf("Revise budget"),
//            notes = mutableStateOf("notes"),
//            completed = mutableStateOf(true),
//            due = mutableStateOf(
//                ZonedDateTime.now()
//            ),
//            deleted = mutableStateOf(false),
//            taskListId = mutableStateOf("financeTaskListDaoId"),
//            neededDuration = mutableIntStateOf(180)
//        ),
//        TaskDao(
//            id = "6",
//            title = mutableStateOf("Categorize expenses"),
//            notes = mutableStateOf("notes"),
//            completed = mutableStateOf(false),
//            due = mutableStateOf(
//                ZonedDateTime.now()
//            ),
//            deleted = mutableStateOf(false),
//            taskListId = mutableStateOf("financeTaskListDaoId"),
//            neededDuration = mutableIntStateOf(45)
//        ),
//        TaskDao(
//            id = "7",
//            title = mutableStateOf("Gym"),
//            notes = mutableStateOf("notes"),
//            completed = mutableStateOf(false),
//            due = mutableStateOf(
//                ZonedDateTime.now()
//            ),
//            deleted = mutableStateOf(false),
//            taskListId = mutableStateOf("fitnessTaskListDaoId"),
//            neededDuration = mutableIntStateOf(45)
//        ),
//        TaskDao(
//            id = "8",
//            title = mutableStateOf("Stretch"),
//            notes = mutableStateOf("notes"),
//            completed = mutableStateOf(false),
//            due = mutableStateOf(
//                ZonedDateTime.now()
//            ),
//            deleted = mutableStateOf(false),
//            taskListId = mutableStateOf("fitnessTaskListDaoId"),
//            neededDuration = mutableIntStateOf(45)
//        ),
//        TaskDao(
//            id = "9",
//            title = mutableStateOf("Walk"),
//            notes = mutableStateOf("notes"),
//            completed = mutableStateOf(false),
//            due = mutableStateOf(
//                ZonedDateTime.now()
//            ),
//            deleted = mutableStateOf(false),
//            taskListId = mutableStateOf("fitnessTaskListDaoId"),
//            neededDuration = mutableIntStateOf(45)
//        )
    )
}
