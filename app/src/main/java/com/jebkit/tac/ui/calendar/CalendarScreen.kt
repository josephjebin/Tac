package com.jebkit.tac.ui.calendar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.ui.dragAndDrop.ScheduleDraggable
import com.jebkit.tac.ui.tasks.TasksSheetState
import java.time.LocalDate

@Composable
fun Calendar(
    verticalScrollState: ScrollState,
    selectedDate: LocalDate,
    events: List<EventDao>,
    scheduledTasks: List<ScheduledTask>,
    tasksSheetState: TasksSheetState,
    addScheduledTask: (ScheduledTask) -> Unit,
    removeScheduledTask: (ScheduledTask) -> Unit,
    addEventDao: (EventDao) -> Unit,
    removeEventDao: (EventDao) -> Unit
) {
    val hourHeight = 64.dp
    Box {
        Row {
            HoursSidebar(
                hourHeight = hourHeight,
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
            )

            Box(modifier = Modifier) {
                DaysSchedule(
                    minSelectedDate = selectedDate,
                    eventDaos = events,
                    scheduledTasks = scheduledTasks,
                    hourHeight = hourHeight,
                    tasksSheetState = tasksSheetState,
                    addScheduledTask = addScheduledTask,
                    removeScheduledTask = removeScheduledTask,
                    addEventDao = addEventDao,
                    removeEventDao = removeEventDao,
                    modifier = Modifier
                        .verticalScroll(verticalScrollState)
                )

                ScheduleDraggable()

            }
        }
    }

}