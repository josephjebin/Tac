package com.example.tac.ui.calendar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.ScheduledTask
import com.example.tac.ui.dragAndDrop.ScheduleDraggable
import com.example.tac.ui.task.TasksSheetState

@Composable
fun Calendar(
    uiCalendarState: CalendarState,
    tasksSheetState: TasksSheetState,
    removeScheduledTask: (Int) -> Unit,
    removeEventDao: (Int) -> Unit,
    addScheduledTask: (ScheduledTask) -> Unit,
    addEventDao: (EventDao) -> Unit,
) {
    val hourHeight = 64.dp
    val verticalScrollState = rememberScrollState()
    Box {
        Row {
            HoursSidebar(
                hourHeight = hourHeight,
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
            )

            Box(modifier = Modifier) {
                Schedule(
                    events = uiCalendarState.events.filter { eventDao ->
                        eventDao.start.toLocalDate()
                            .equals(uiCalendarState.selectedDate.toLocalDate())
                    },
                    scheduledTasks = uiCalendarState.scheduledTasks.filter { scheduledTask ->
                        scheduledTask.start.toLocalDate()
                            .equals(uiCalendarState.selectedDate.toLocalDate())
                    },
                    hourHeight = hourHeight,
                    tasksSheetState = tasksSheetState,
                    removeScheduledTask = removeScheduledTask,
                    removeEventDao = removeEventDao,
                    addScheduledTask = addScheduledTask,
                    addEventDao = addEventDao,
                    modifier = Modifier
                        .verticalScroll(verticalScrollState)
                )

                ScheduleDraggable()

            }
        }
    }

}