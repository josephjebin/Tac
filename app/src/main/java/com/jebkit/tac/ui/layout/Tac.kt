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
import com.jebkit.tac.ui.tasksAndCalendar.GoogleCalendarState
import com.jebkit.tac.ui.tasksAndCalendar.TasksAndCalendarViewModel
import com.jebkit.tac.ui.calendar.DayHeader
import com.jebkit.tac.ui.dragAndDrop.RootDragInfoProvider
import com.jebkit.tac.ui.tasks.TaskSheet
import com.jebkit.tac.ui.tasks.TasksSheetState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val outputFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy")

@Composable
fun Tac(tasksAndCalendarViewModel: TasksAndCalendarViewModel = viewModel()) {
    Surface(color = MaterialTheme.colors.background) {
        val tasksAndCalendarState by tasksAndCalendarViewModel.uiState.collectAsState()

        TasksAndCalendarScreen(
            selectedDate = tasksAndCalendarState.minSelectedDate.value,
            eventDaos = tasksAndCalendarState.googleCalendarState.value.eventDaos.values.toList(),
            scheduledTasks = tasksAndCalendarState.googleCalendarState.value.scheduledTasks.values.toList(),
//                try {
//                    val scheduledTasks: MutableList<ScheduledTask> = mutableListOf()
//                    (tasksAndCalendarState.googleCalendarState.value as GoogleCalendarState.Success).scheduledTasks.values.forEach { scheduledTasksMap ->
//                        scheduledTasksMap.values.filter { scheduledTask ->
//                            //scheduled task's end lies within current selected range OR
//                            (scheduledTask.end.value.toLocalDate() >= tasksAndCalendarState.minSelectedDate.value && scheduledTask.end.value.toLocalDate() < tasksAndCalendarState.maxSelectedDate.value.plusDays(1)) ||
//                                    //scheduled task starts within current selected range
//                                    (scheduledTask.start.value.toLocalDate() >= tasksAndCalendarState.minSelectedDate.value && scheduledTask.start.value.toLocalDate() < tasksAndCalendarState.maxSelectedDate.value.plusDays(1))
//                        }.forEach { scheduledTasks.add(it) }
//                    }
//                    scheduledTasks.toList()
//                } catch (e: Exception) {
//                    val scheduledTasks: MutableList<ScheduledTask> = mutableListOf()
//                    scheduledTasks.toList()
//                },
            saveScheduledTask = { scheduledTask: ScheduledTask ->
                tasksAndCalendarViewModel.addScheduledTask(
                    scheduledTask
                )
            },
            deleteScheduledTask = { scheduledTask: ScheduledTask ->
                tasksAndCalendarViewModel.deleteScheduledTask(
                    scheduledTask
                )
            },
            addEventDao = { eventDao: EventDao -> tasksAndCalendarViewModel.addEventDao(eventDao) },
            removeEventDao = { eventDao: EventDao ->
                tasksAndCalendarViewModel.deleteEventDao(
                    eventDao
                )
            },
            taskListDaos = tasksAndCalendarState.googleTasksState.value.taskListDaos.values.toList(),
            taskDaos = tasksAndCalendarState.googleTasksState.value.taskDaos.values.toList(),
            currentSelectedTaskListDao = tasksAndCalendarState.googleTasksState.value.currentSelectedTaskListDao.value,
            onTaskListDaoSelected = { taskListDao: TaskListDao ->
                //TODO
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
    saveScheduledTask: (ScheduledTask) -> Unit,
    deleteScheduledTask: (ScheduledTask) -> Unit,
    addEventDao: (EventDao) -> Unit,
    removeEventDao: (EventDao) -> Unit,
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
                            addScheduledTask = saveScheduledTask,
                            removeScheduledTask = deleteScheduledTask,
                            addEventDao = addEventDao,
                            removeEventDao = removeEventDao
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