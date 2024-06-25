package com.example.tac.ui.layout

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
import com.example.tac.MyBottomBar
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.ScheduledTask
import com.example.tac.data.tasks.TaskDao
import com.example.tac.data.tasks.TaskList
import com.example.tac.ui.calendar.Calendar
import com.example.tac.ui.calendar.CalendarViewModel
import com.example.tac.ui.calendar.DayHeader
import com.example.tac.ui.dragAndDrop.RootDragInfoProvider
import com.example.tac.ui.task.TaskSheet
import com.example.tac.ui.task.TasksSheetState
import com.example.tac.ui.task.TasksViewModel
import java.time.LocalDate

@Composable
fun Tac(calendarViewModel: CalendarViewModel = viewModel(),
        tasksViewModel: TasksViewModel = viewModel()
) {
    Surface(color = MaterialTheme.colors.background) {
        val uiCalendarState by calendarViewModel.uiState.collectAsState()
        val uiTasksState by tasksViewModel.uiState.collectAsState()
        TasksAndCalendarScreen(
            selectedDate = uiCalendarState.selectedDate,
            events = uiCalendarState.events.value,
            scheduledTasks = uiCalendarState.scheduledTasks.value,
            addScheduledTask = { scheduledTask: ScheduledTask -> calendarViewModel.addScheduledTask(scheduledTask) },
            removeScheduledTask = { scheduledTask: ScheduledTask -> calendarViewModel.removeScheduledTask(scheduledTask) },
            addEventDao = { eventDao: EventDao -> calendarViewModel.addEventDao(eventDao) },
            removeEventDao = { eventDao: EventDao -> calendarViewModel.removeEventDao(eventDao) },
            taskLists = uiTasksState.taskLists,
            tasks = uiTasksState.tasks,
            currentSelectedTaskList = uiTasksState.currentSelectedTaskList,
            onTaskListSelected = { taskList: TaskList ->
                tasksViewModel.updateCurrentSelectedTaskList(taskList)
            },
            onTaskSelected = { taskDao: TaskDao ->
                tasksViewModel.updateCurrentSelectedTask(taskDao)
            }
        )
    }
}


@Composable
fun TasksAndCalendarScreen(
    selectedDate: LocalDate,
    events: List<EventDao>,
    scheduledTasks: List<ScheduledTask>,
    addScheduledTask: (ScheduledTask) -> Unit,
    removeScheduledTask: (ScheduledTask) -> Unit,
    addEventDao: (EventDao) -> Unit,
    removeEventDao: (EventDao) -> Unit,
    taskLists: List<TaskList>,
    tasks: List<TaskDao>,
    currentSelectedTaskList: TaskList,
    onTaskListSelected:  (TaskList) -> Unit,
    onTaskSelected: (TaskDao) -> Unit
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
                            events = events.filter { eventDao ->
                                eventDao.start.value.toLocalDate()
                                    .equals(selectedDate)
                            },
                            scheduledTasks = scheduledTasks.filter { scheduledTask ->
                                scheduledTask.start.value.toLocalDate()
                                    .equals(selectedDate)
                            },
                            tasksSheetState = tasksSheetState.value,
                            addScheduledTask = addScheduledTask,
                            removeScheduledTask = removeScheduledTask,
                            addEventDao = addEventDao,
                            removeEventDao = removeEventDao
                        )


                    }
                }

                //TASKS SHEET
                TaskSheet(
                    tasksSheetState = tasksSheetState,
                    taskLists = taskLists,
                    tasks = tasks,
                    currentSelectedTaskList = currentSelectedTaskList,
                    onTaskListSelected = onTaskListSelected,
                    onTaskSelected = onTaskSelected,
                    onTaskCompleted = { taskDao: TaskDao ->
                    },
                    onTaskDrag = { tasksSheetState.value = TasksSheetState.COLLAPSED },
                )
            }
        }

    }
}