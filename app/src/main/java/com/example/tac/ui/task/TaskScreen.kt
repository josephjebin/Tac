package com.example.tac.ui.task

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.tac.R
import com.example.tac.data.calendar.ScheduledTask
import com.example.tac.data.tasks.TaskDao
import com.example.tac.data.tasks.TaskList
import com.example.tac.ui.dragAndDrop.DragTarget
import com.example.tac.ui.layout.outputFormat
import com.example.tac.ui.theme.accent_gray
import com.example.tac.ui.theme.onSurfaceGray
import java.time.ZonedDateTime

@Composable
fun TaskSheet(
    tasksSheetState: MutableState<TasksSheetState>,
    taskLists: List<TaskList>,
    tasks: List<TaskDao>,
    currentSelectedTaskList: TaskList,
    onTaskListSelected: (TaskList) -> Unit,
    onTaskSelected: (TaskDao) -> Unit,
    onTaskCompleted: (TaskDao) -> Unit,
    onTaskDrag: () -> Unit
) {
    val taskSheetModifier = when (tasksSheetState.value) {
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
                BorderStroke(1.dp, SolidColor(Color.Black)),
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .fillMaxHeight()
            .fillMaxWidth()
            .background(accent_gray, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        val hourHeight = 64.dp

        //Peek Arrow
        Box(modifier = Modifier
            .height(48.dp)
            .border(
                BorderStroke(0.dp, Color.Transparent),
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .fillMaxWidth()
            .clickable {

            }
        ) {
            Image(
                painterResource(id = R.drawable.caret_down),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .rotate(if(tasksSheetState.value == TasksSheetState.COLLAPSED)180f else 0f),
                contentDescription = "Task Sheet Peek Arrow"
            )
        }


        //projects
        Row {
            taskLists.forEach { taskList ->
                Card(
                    modifier = Modifier
                        .padding(8.dp, 16.dp)
                        .border(BorderStroke(1.dp, SolidColor(Color.Black)))
                        .clickable { onTaskListSelected(taskList) }
                ) {
                    Text(modifier = Modifier.padding(8.dp), text = taskList.title)
                }
            }
        }


        //tasks
        val filteredTasks =
            tasks.filter { taskDao -> taskDao.taskList.value == currentSelectedTaskList.title }
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            filteredTasks.forEachIndexed { index, taskDao ->
                val eventDurationMinutes = taskDao.neededDuration.intValue - taskDao.scheduledDuration.intValue
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
                        start = taskDao.start,
                        //STUB
                        end = taskDao.end,
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
                if (index < taskLists.lastIndex) Divider(color = Color.Black, thickness = 1.dp)
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
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { onTaskCompleted(taskDao) }) {
            Icon(painterResource(id = R.drawable.priority3_button), "")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .widthIn(max = 160.dp),
            text = taskDao.title.value,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            //due date
            Row() {
                Icon(
                    modifier = Modifier.scale(.8f),
                    painter = painterResource(id = R.drawable.round_calendar_today_24),
                    contentDescription = "Due date"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = taskDao.end.value.format(outputFormat))
            }
            //duration
            Row() {
                Icon(
                    modifier = Modifier.scale(.8f),
                    painter = painterResource(id = R.drawable.round_access_time_24),
                    contentDescription = "Duration"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${taskDao.neededDuration} mins")
            }
        }
    }
}