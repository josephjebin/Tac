package com.example.tac.ui.task

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tac.R
import com.example.tac.data.tasks.TaskDao
import com.example.tac.data.tasks.TaskList

@Composable
fun TaskSheet(
    taskSheetModifier: Modifier = Modifier,
    taskLists: List<TaskList>,
    tasks: List<TaskDao>,
    currentSelectedTaskList: TaskList,
    onTaskListSelected: (TaskList) -> Unit,
    onTaskSelected: (TaskDao) -> Unit,
    onTaskCompleted: (TaskDao) -> Unit
) {
    Column(modifier = taskSheetModifier) {
        //projects
        LazyRow() {
            itemsIndexed(taskLists) { index, taskList ->
                Card(
                    modifier = Modifier
                        .padding(16.dp, 24.dp)
                        .border(BorderStroke(1.dp, SolidColor(Color.Black)))
                        .clickable { onTaskListSelected(taskList) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(text = taskList.title)
                }
            }
        }

        val filteredTasks =
            tasks.filter { taskDao -> taskDao.taskList == currentSelectedTaskList.title }
        //tasks
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            itemsIndexed(filteredTasks) { index, taskDao ->
                TaskRow(
                    taskDao = taskDao,
                    onTaskSelected = onTaskSelected,
                    onTaskCompleted = onTaskCompleted
                )

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
//        .border(BorderStroke(1.dp, SolidColor(Color.LightGray)))
        .clickable { onTaskSelected(taskDao) }) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { onTaskCompleted(taskDao) }) {
            Icon(painterResource(id = R.drawable.priority3_button), "")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(modifier = Modifier.align(Alignment.CenterVertically).widthIn(max = 160.dp), text = taskDao.title, overflow = TextOverflow.Ellipsis)

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
                Text(text = "${taskDao.duration} minutes")
            }
        }
    }
}