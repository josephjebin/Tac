package com.jebkit.tac.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jebkit.tac.MyBottomBar
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskListDao
import com.jebkit.tac.ui.calendar.Calendar
import com.jebkit.tac.ui.tasksAndCalendar.TasksAndCalendarViewModel
import com.jebkit.tac.ui.calendar.DayHeader
import com.jebkit.tac.ui.dragAndDrop.RootDragInfoProvider
import com.jebkit.tac.ui.tasks.TaskSheet
import com.jebkit.tac.ui.tasks.TasksSheetState
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val outputFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy")

@Composable
fun Tac(tasksAndCalendarViewModel: TasksAndCalendarViewModel = viewModel()) {
    Surface(color = MaterialTheme.colors.background) {
        val tasksAndCalendarState by tasksAndCalendarViewModel.uiState.collectAsState()

        TasksAndCalendarScreen(
            selectedDate = tasksAndCalendarState.minSelectedDate.value,
            eventDaos = tasksAndCalendarState.eventDaos.values.toList(),
            scheduledTasks = tasksAndCalendarState.scheduledTasks.values.toList(),
            addScheduledTask = { scheduledTask: ScheduledTask ->
                tasksAndCalendarViewModel.addScheduledTask(
                    scheduledTask
                )
            },
            updateScheduledTaskTime = { scheduledTask: ScheduledTask, newStartTime: ZonedDateTime ->
                tasksAndCalendarViewModel.updateScheduledTaskTime(scheduledTask, newStartTime)
            },
            updateEventDaoTime = { eventDao: EventDao, newStartTime: ZonedDateTime ->
                tasksAndCalendarViewModel.updateEventDaoTime(eventDao, newStartTime)
            },
            taskListDaos = tasksAndCalendarState.taskListDaos.values.toList(),
            taskDaos = tasksAndCalendarState.taskDaos.values.toList()
                .filter { taskDao ->
                    taskDao.taskListId.value == (tasksAndCalendarState.currentSelectedTaskListDao.value?.id)
                },
            currentSelectedTaskListDao = tasksAndCalendarState.currentSelectedTaskListDao.value,
            onTaskListDaoSelected = { taskListDao: TaskListDao ->
                tasksAndCalendarViewModel.updateCurrentSelectedTaskListDao(taskListDao)
            },
            onTaskDaoSelected = {
                //TODO
            }
        )

    }
}


@Composable
fun TasksAndCalendarScreen(
    selectedDate: LocalDate,
    eventDaos: List<EventDao>,
    scheduledTasks: List<ScheduledTask>,
    addScheduledTask: (ScheduledTask) -> Unit,
    updateScheduledTaskTime: (ScheduledTask, ZonedDateTime) -> Unit,
    updateEventDaoTime: (EventDao, ZonedDateTime) -> Unit,
    taskListDaos: List<TaskListDao>,
    taskDaos: List<TaskDao>,
    currentSelectedTaskListDao: TaskListDao?,
    onTaskListDaoSelected: (TaskListDao) -> Unit,
    onTaskDaoSelected: (TaskDao) -> Unit
) {
    val tasksSheetState = rememberSaveable { mutableStateOf(TasksSheetState.COLLAPSED) }
    Scaffold(
        topBar = { DayHeader(selectedDate) },
        bottomBar = { MyBottomBar(tasksSheetState = tasksSheetState) }
    ) {
        RootDragInfoProvider {
            Column(
                modifier = Modifier.padding(it),
                verticalArrangement = Arrangement.Bottom
            ) {
                //CALENDAR
                val verticalScrollState = rememberScrollState()
                if (tasksSheetState.value != TasksSheetState.EXPANDED) {
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()
                    ) {
                        Calendar(
                            verticalScrollState = verticalScrollState,
                            selectedDate = selectedDate,
                            eventDaos = eventDaos,
                            scheduledTasks = scheduledTasks,
                            tasksSheetState = tasksSheetState.value,
                            addScheduledTask = addScheduledTask,
                            updateScheduledTaskTime = updateScheduledTaskTime,
                            updateEventDaoTime = updateEventDaoTime,
                        )


                    }
                }

                //TASKS SHEET
                TaskSheet(
                    tasksSheetState = tasksSheetState,
                    taskListDaos = taskListDaos,
                    taskDaos = taskDaos,
                    currentSelectedTaskListDao = currentSelectedTaskListDao,
                    onTaskListDaoSelected = onTaskListDaoSelected,
                    onTaskSelected = onTaskDaoSelected,
                    onTaskCompleted = { taskDao: TaskDao ->
                    },
                    onTaskDrag = { tasksSheetState.value = TasksSheetState.COLLAPSED },
                )
            }
        }

    }
}