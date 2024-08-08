package com.jebkit.tac.ui.calendar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.ui.dragAndDrop.Draggable
import com.jebkit.tac.ui.tasks.TasksSheetState
import java.time.LocalDate
import java.time.ZonedDateTime

@Composable
fun Calendar(
    hourHeight: Dp = 64.dp,
    verticalScrollState: ScrollState,
    selectedDate: LocalDate,
    eventDaos: List<EventDao>,
    scheduledTasks: List<ScheduledTask>,
    tasksSheetState: TasksSheetState,
    addScheduledTask: (ScheduledTask) -> Unit,
    updateScheduledTaskTime: (ScheduledTask, ZonedDateTime) -> Unit,
    updateEventDaoTime: (EventDao, ZonedDateTime) -> Unit
) {
    Box{
        Row {
            HoursSidebar(
                hourHeight = hourHeight,
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
            )

            Box(modifier = Modifier) {
                SingleDaySchedule(
                    selectedDate = selectedDate,
                    eventDaos = eventDaos.filter { eventDao -> eventDao.start.value.toLocalDate() == selectedDate || eventDao.end.value.toLocalDate() == selectedDate },
                    scheduledTasks = scheduledTasks.filter { scheduledTask -> scheduledTask.start.value.toLocalDate() == selectedDate || scheduledTask.end.value.toLocalDate() == selectedDate },
                    hourHeight = hourHeight,
                    tasksSheetState = tasksSheetState,
                    addScheduledTask = addScheduledTask,
                    updateScheduledTaskTime = updateScheduledTaskTime,
                    updateEventDaoTime = updateEventDaoTime,
                    modifier = Modifier
                        .verticalScroll(verticalScrollState)
                )

                Draggable()
            }
        }
    }

}