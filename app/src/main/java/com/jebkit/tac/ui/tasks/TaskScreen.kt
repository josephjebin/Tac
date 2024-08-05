package com.jebkit.tac.ui.tasks

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.jebkit.tac.R
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.data.dummyData.dummyDataTaskListDaos
import com.jebkit.tac.data.dummyData.dummyDataTasksDaos
import com.jebkit.tac.data.tasks.TaskDao
import com.jebkit.tac.data.tasks.TaskListDao
import com.jebkit.tac.ui.dragAndDrop.CancelDropTarget
import com.jebkit.tac.ui.dragAndDrop.DragTarget
import com.jebkit.tac.ui.layout.outputFormat
import java.time.ZonedDateTime

@Composable
fun TaskSheet(
    tasksSheetState: TasksSheetState,
    taskListDaos: List<TaskListDao>,
    taskDaos: List<TaskDao>,
    currentSelectedTaskListDao: TaskListDao?,
    onTaskListDaoSelected: (TaskListDao) -> Unit,
    onTaskSelected: (TaskDao) -> Unit,
    onTaskCompleted: (TaskDao) -> Unit,
    onTaskDrag: () -> Unit
) {
    val taskSheetModifier = when (tasksSheetState) {
        TasksSheetState.COLLAPSED -> {
            Modifier
                .fillMaxWidth()
                .height(48.dp)
        }

        TasksSheetState.PARTIALLY_EXPANDED -> {
            Modifier
                .fillMaxWidth()
                .height(400.dp)
        }

        TasksSheetState.EXPANDED -> {
            Modifier
                .fillMaxSize()
        }
    }


    Column(
        modifier = taskSheetModifier
            .border(
                BorderStroke(2.dp, Color.Black),
                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
            .fillMaxHeight()
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.surface_dark_gray),
                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
    ) {
        val hourHeight = 64.dp

        var peekArrowModifier: Modifier by remember { mutableStateOf(Modifier) }
        //Peek Arrow
        CancelDropTarget(
            highlightBottomBar = { peekArrowModifier = Modifier.border(16.dp, Color.Red) }
        ) {
            Box(modifier = peekArrowModifier
                .height(48.dp)
                .fillMaxWidth()
                .clickable {

                }
            ) {
                Image(
                    painterResource(id = R.drawable.caret_down),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .rotate(if (tasksSheetState == TasksSheetState.COLLAPSED) 180f else 0f),
                    contentDescription = "Task Sheet Peek Arrow"
                )
            }
        }

        //projects
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp, 8.dp)
        ) {
            taskListDaos.forEach { taskListDao ->
                Card(
                    modifier = Modifier
                        .padding(8.dp, 0.dp)
                        .clickable { onTaskListDaoSelected(taskListDao) },
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = colorResource(id = R.color.surface_dark_gray),
                    border = BorderStroke(
                        2.dp,
                        if (taskListDao == currentSelectedTaskListDao) colorResource(id = R.color.akiflow_app_light_purple)
                        else colorResource(id = R.color.google_text_white)
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        color = if (taskListDao == currentSelectedTaskListDao) colorResource(id = R.color.akiflow_app_light_purple)
                        else colorResource(id = R.color.google_text_white),
                        text = taskListDao.title.value
                    )
                }
            }
        }


        //tasks
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            taskDaos.forEachIndexed { index, taskDao ->
                val eventDurationMinutes =
                    taskDao.neededDuration.intValue - taskDao.scheduledDuration.intValue
                val eventHeight = ((eventDurationMinutes / 60f) * hourHeight)

                DragTarget(
                    dataToDrop = ScheduledTask(
                        //STUB
                        //dropping this on the calendar will trigger a call to calendar api
                        //response from calendar api will contain the actual id, and this is what
                        //will be saved in the view model
                        id = "STUB",
                        title = taskDao.title,
                        parentTaskId = taskDao.id,
                        description = taskDao.notes,
                        //STUB
                        start = mutableStateOf(ZonedDateTime.now()),
                        //STUB
                        end = mutableStateOf(ZonedDateTime.now()),
                        duration = mutableIntStateOf(eventDurationMinutes),
                        color = taskDao.color
                    ),
                    isRescheduling = false,
                    onTaskDrag = onTaskDrag,
                    draggableHeight = eventHeight
                ) {
                    TaskRow(
                        taskDao = taskDao,
                        onTaskSelected = onTaskSelected,
                        onTaskCompleted = onTaskCompleted
                    )
                }

                if (index < taskDaos.lastIndex) Divider(
                    color = colorResource(id = R.color.google_text_gray),
                    thickness = 1.dp
                )
            }

        }
    }
}

@Composable
fun TaskRow(
    taskDao: TaskDao,
    onTaskSelected: (TaskDao) -> Unit,
    onTaskCompleted: (TaskDao) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .clickable { onTaskSelected(taskDao) }
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            onClick = { onTaskCompleted(taskDao) }) {
            Icon(
                painterResource(id = R.drawable.priority3_button),
                tint = colorResource(id = R.color.google_text_white),
                contentDescription = ""
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .fillMaxWidth(),
            color = colorResource(id = R.color.google_text_white),
            text = taskDao.title.value,
            overflow = TextOverflow.Ellipsis
        )

        //due date and durations
        Column(
            modifier = Modifier
                .width(136.dp)
                .fillMaxHeight()
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            //due date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = R.drawable.round_calendar_today_24),
                    tint = colorResource(id = R.color.google_text_white),
                    contentDescription = "Due date"
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    color = colorResource(id = R.color.google_text_white),
                    text = taskDao.due.value?.format(outputFormat) ?: "None",
                    textAlign = TextAlign.Center
                )
            }
            //duration
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = R.drawable.round_access_time_24),
                    tint = colorResource(id = R.color.google_text_white),
                    contentDescription = "Duration"
                )


                Text(
                    modifier = Modifier.fillMaxWidth(),
                    color = colorResource(id = R.color.google_text_white),
                    text = "${taskDao.scheduledDuration.intValue} / ${taskDao.workedDuration.intValue} / ${taskDao.neededDuration.intValue}",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun TaskSheetPreview() {
    val tasksSheetState by remember {
        mutableStateOf(TasksSheetState.PARTIALLY_EXPANDED)
    }
    TaskSheet(
        tasksSheetState = tasksSheetState,
        taskListDaos = dummyDataTaskListDaos(),
        taskDaos = dummyDataTasksDaos(),
        currentSelectedTaskListDao = dummyDataTaskListDaos()[0],
        onTaskListDaoSelected = {},
        onTaskSelected = {},
        onTaskCompleted = {}
    ) {}
}