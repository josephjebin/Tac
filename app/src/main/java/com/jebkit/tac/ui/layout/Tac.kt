package com.jebkit.tac.ui.layout

import android.view.animation.RotateAnimation
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.jebkit.tac.ui.fab.AddEventAndTaskFab
import com.jebkit.tac.ui.fab.DialogButtonStack
import com.jebkit.tac.ui.tasks.TaskSheet
import com.jebkit.tac.ui.tasks.TasksSheetState
import com.jebkit.tac.ui.theme.google_divider_gray
import com.jebkit.tac.ui.theme.google_light_blue
import kotlinx.coroutines.launch
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
            updateEventDaoTime = { eventDao: EventDao, newStartTime: ZonedDateTime ->
                tasksAndCalendarViewModel.updateEventDaoTime(eventDao, newStartTime)
            },
            scheduledTasks = tasksAndCalendarState.scheduledTasks.values.toList(),
            addScheduledTask = { scheduledTask: ScheduledTask ->
                tasksAndCalendarViewModel.addScheduledTask(
                    scheduledTask
                )
            },
            updateScheduledTaskTime = { scheduledTask: ScheduledTask, newStartTime: ZonedDateTime ->
                tasksAndCalendarViewModel.updateScheduledTaskStartTime(scheduledTask, newStartTime)
            },
            toggleScheduledTaskCompletion = { scheduledTask: ScheduledTask ->
                tasksAndCalendarViewModel.toggleScheduledTaskCompletion(
                    scheduledTask
                )
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
    updateEventDaoTime: (EventDao, ZonedDateTime) -> Unit,
    scheduledTasks: List<ScheduledTask>,
    addScheduledTask: (ScheduledTask) -> Unit,
    updateScheduledTaskTime: (ScheduledTask, ZonedDateTime) -> Unit,
    toggleScheduledTaskCompletion: (ScheduledTask) -> Unit,
    taskListDaos: List<TaskListDao>,
    taskDaos: List<TaskDao>,
    currentSelectedTaskListDao: TaskListDao?,
    onTaskListDaoSelected: (TaskListDao) -> Unit,
    onTaskDaoSelected: (TaskDao) -> Unit,
) {
    val tasksSheetState = rememberSaveable { mutableStateOf(TasksSheetState.COLLAPSED) }
    var minuteVerticalOffset: Float by remember { mutableFloatStateOf(0f) }
    val calendarScrollState = rememberScrollState()
    var isDragging by remember { mutableStateOf(false) }
    var isDraggingInsideCancelRegion by remember { mutableStateOf(false) }
    var addEventAndTask by remember { mutableStateOf(false) }

    RootDragInfoProvider(
        verticalOffsetPerMinute = minuteVerticalOffset,
        calendarScrollState = calendarScrollState,
        updateIsDragging = { boolean: Boolean -> isDragging = boolean },
        updateIsDraggingInsideCancelRegion = { boolean: Boolean ->
            isDraggingInsideCancelRegion = boolean
        }
    ) {
        val animatedFabPadding by animateDpAsState(
            if (tasksSheetState.value == TasksSheetState.COLLAPSED) {
                56.dp
            } else {
                8.dp
            },
            label = "fab_padding"
        )

        Scaffold(
            topBar = { DayHeader(selectedDate) },
            bottomBar = { MyBottomBar(tasksSheetState = tasksSheetState) }
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
                            updateEventDaoTime = updateEventDaoTime,
                            scheduledTasks = scheduledTasks,
                            updateScheduledTaskTime = updateScheduledTaskTime,
                            toggleScheduledTaskCompletion = toggleScheduledTaskCompletion
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
                    setTasksSheetState = { newTasksSheetState ->
                        tasksSheetState.value = newTasksSheetState
                    },
                    addScheduledTask = addScheduledTask
                )
            }

            //FAB button to add task or event
            val animatableRotation = remember { Animatable(0f) }
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = addEventAndTask) {
                when(addEventAndTask) {
                    false -> coroutineScope.launch { animatableRotation.animateTo(0f) }
                    true -> coroutineScope.launch { animatableRotation.animateTo(45f) }
                }
            }
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = animatedFabPadding, end = 16.dp),
                    onClick = { addEventAndTask = true },
                ) {
                    Image(
                        painterResource(id = R.drawable.google_plus_sign),
                        contentDescription = "Add task or event",
                        modifier = Modifier
                            .background(google_divider_gray, shape = CircleShape)
                            .size(48.dp)
                            .padding(4.dp)
                            .rotate(animatableRotation.value)
                    )
                }
            }

            //Dialog spawned by FAB
            if (addEventAndTask) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    DialogButtonStack(
                        it,
                        animatedFabPadding,
                        onDismissRequest = { addEventAndTask = false }
                    )
                }
            }
        }

        //Drag Cancel Region
        if (isDragging) {
            val stroke = Stroke(
                width = 4f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(colorResource(id = R.color.surface_dark_gray))
                    .drawBehind {
                        drawRoundRect(
                            color = if (isDraggingInsideCancelRegion) google_light_blue else google_divider_gray,
                            style = stroke
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(
                            color = if (isDraggingInsideCancelRegion) google_light_blue else colorResource(
                                id = R.color.google_text_gray
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(),
                        text = "Drop here to cancel",
                        textAlign = TextAlign.Center,
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
        updateEventDaoTime = { _, _ -> },
        scheduledTasks = listOf(),
        addScheduledTask = { _ -> },
        toggleScheduledTaskCompletion = {},
        updateScheduledTaskTime = { _, _ -> },
        taskListDaos = listOf(),
        taskDaos = listOf(),
        currentSelectedTaskListDao = null,
        onTaskListDaoSelected = { (TaskListDao) -> },
        onTaskDaoSelected = { (TaskDao) -> },
    )
}