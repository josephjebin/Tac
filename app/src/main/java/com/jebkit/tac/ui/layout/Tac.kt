package com.jebkit.tac.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jebkit.tac.MyBottomBar
import com.jebkit.tac.R
import com.jebkit.tac.constants.Constants.Companion.hourHeight
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

val outputFormat = DateTimeFormatter.ofPattern("MM - dd - yyyy")

@Composable
fun Tac(tasksAndCalendarViewModel: TasksAndCalendarViewModel = viewModel()) {
    Surface(color = colorResource(id = R.color.background_dark_gray)) {
        val tasksAndCalendarState by tasksAndCalendarViewModel.uiState.collectAsState()

        TasksAndCalendarScreen(
            selectedDate = tasksAndCalendarState.minSelectedDate.value,
            eventDaos = tasksAndCalendarState.eventDaos.values.toList(),
            scheduledTasks = tasksAndCalendarState.scheduledTasks.values.toList(),
//            addScheduledTask = { scheduledTask: ScheduledTask ->
//                tasksAndCalendarViewModel.addScheduledTask(
//                    scheduledTask
//                )
//            },
//            updateScheduledTaskTime = { scheduledTask: ScheduledTask, newStartTime: ZonedDateTime ->
//                tasksAndCalendarViewModel.updateScheduledTaskStartTime(scheduledTask, newStartTime)
//            },
//            updateEventDaoTime = { eventDao: EventDao, newStartTime: ZonedDateTime ->
//                tasksAndCalendarViewModel.updateEventDaoTime(eventDao, newStartTime)
//            },
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
    taskListDaos: List<TaskListDao>,
    taskDaos: List<TaskDao>,
    currentSelectedTaskListDao: TaskListDao?,
    onTaskListDaoSelected: (TaskListDao) -> Unit,
    onTaskDaoSelected: (TaskDao) -> Unit
) {
    val tasksSheetState = rememberSaveable { mutableStateOf(TasksSheetState.COLLAPSED) }
    var minuteVerticalOffset: Float by remember { mutableFloatStateOf(0f) }
    val calendarScrollState = rememberScrollState()
    var isDragging by remember { mutableStateOf(false) }
    var isDraggingInsideCancelRegion by remember { mutableStateOf(false) }

    RootDragInfoProvider(
        verticalOffsetPerMinute = minuteVerticalOffset,
        calendarScrollState = calendarScrollState,
        updateIsDragging = { boolean: Boolean -> isDragging = boolean }
    ) {
        Scaffold(
            topBar = { DayHeader(selectedDate) },
            bottomBar = {
                MyBottomBar(
                    tasksSheetState = tasksSheetState,
                )
            }
        ) {
            //invisible boxes to calculate one hour's offset
            Box {
                Column {
                    Box(modifier = Modifier.height(hourHeight))
                    Box(modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        minuteVerticalOffset = layoutCoordinates.positionInParent().y.div(60)
                    })
                }
            }

            Column(
                modifier = Modifier
                    .background(color = Color.Black)
                    .padding(it),
                verticalArrangement = Arrangement.Bottom
            ) {
                //CALENDAR
                if (tasksSheetState.value != TasksSheetState.EXPANDED) {
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()
                            .background(color = colorResource(id = R.color.background_dark_gray))
                    ) {
                        Calendar(
                            hourHeight = hourHeight,
                            verticalScrollState = calendarScrollState,
                            selectedDate = selectedDate,
                            eventDaos = eventDaos,
                            scheduledTasks = scheduledTasks
                        )
                    }
                }

                //TASKS SHEET
                TaskSheet(
                    tasksSheetState = tasksSheetState.value,
                    taskListDaos = taskListDaos,
                    taskDaos = taskDaos,
                    currentSelectedTaskListDao = currentSelectedTaskListDao,
                    onTaskListDaoSelected = onTaskListDaoSelected,
                    onTaskSelected = onTaskDaoSelected,
                    onTaskCompleted = { taskDao: TaskDao ->
                    },
                    closeTaskSheet = { tasksSheetState.value = TasksSheetState.COLLAPSED },
                )
            }


        }

        //Drag Cancel Region
        if(isDragging) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(colorResource(id = R.color.surface_dark_gray))
            ) {
                Column {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = Color.Black
                    )
                    Text(
                        text = "DRAG AREA WHOOOO"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TacPreview() {
    TasksAndCalendarScreen(
        selectedDate = LocalDate.now(),
        eventDaos = listOf(),
        scheduledTasks = listOf(),
        taskListDaos = listOf(),
        taskDaos = listOf(),
        currentSelectedTaskListDao = null,
        onTaskListDaoSelected = { (TaskListDao) -> },
        onTaskDaoSelected = { (TaskDao) -> }
    )
}