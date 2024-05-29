package com.example.tac.ui.task

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.tac.ui.theme.accent_gray
import java.time.ZonedDateTime

@Composable
fun TaskSheet(
    taskLists: List<TaskList>,
    tasks: List<TaskDao>,
    currentSelectedTaskList: TaskList,
    onTaskListSelected: (TaskList) -> Unit,
    onTaskSelected: (TaskDao) -> Unit,
    onTaskCompleted: (TaskDao) -> Unit,
    onTaskDrag: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                BorderStroke(1.dp, SolidColor(Color.Black)),
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .fillMaxHeight()
            .fillMaxWidth()
            .background(accent_gray, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        val hourHeight = 64.dp

        //projects
        Row {
            taskLists.forEach { taskList ->
                Card(
                    modifier = Modifier
                        .padding(16.dp, 16.dp)
                        .border(BorderStroke(1.dp, SolidColor(Color.Black)))
                        .clickable { onTaskListSelected(taskList) }
                ) {
                    Text(modifier = Modifier.padding(8.dp), text = taskList.title)
                }
            }
        }


        val filteredTasks =
            tasks.filter { taskDao -> taskDao.taskList == currentSelectedTaskList.title }
        //tasks
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
//            itemsIndexed(filteredTasks) { index, taskDao ->
            filteredTasks.forEachIndexed { index, task ->
                val eventDurationMinutes = task.neededDuration
                val eventHeight = ((eventDurationMinutes / 60f) * hourHeight)

                DragTarget(
                    dataToDrop = ScheduledTask(
                        id = 10,
                        name = task.title,
                        parentTaskId = task.id,
                        //stub
                        start = ZonedDateTime.now(),
                        end = ZonedDateTime.now().plusMinutes(30),
                    ),
                    isRescheduling = false,
                    onTaskDrag = onTaskDrag,
                    draggableModifier = Modifier
                        .height(eventHeight)
                        .fillMaxWidth(),
                    draggableHeight = eventHeight
                ) {
                    TaskRow(
                        taskDao = task,
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
//        .padding(4.dp)
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
            text = taskDao.title,
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
                Text(text = taskDao.due)
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