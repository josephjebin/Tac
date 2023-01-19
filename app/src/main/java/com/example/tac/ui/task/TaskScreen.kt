package com.example.tac.ui.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tac.data.tasks.TaskDao
import com.example.tac.data.tasks.TaskList

@Composable
fun TaskSheet(
    taskLists: List<TaskList>,
    tasks: List<TaskDao>,
    currentSelectedTaskList: TaskList,
    onTaskListSelected: (TaskList) -> Unit,
    onTaskSelected: (TaskDao) -> Unit
) {
    Column() {
        //projects
        LazyRow() {
            items(taskLists, key = { taskList -> taskList.id }) { taskList ->
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { onTaskListSelected(taskList) }) {
                    Text(text = taskList.title)
                }
            }
        }

        val filteredTasks =
            tasks.filter { taskDao -> taskDao.taskList.equals(currentSelectedTaskList) }
        //tasks
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(filteredTasks, key = { task -> task.id }) { task ->
                TaskRow(
                    taskDao = task,
                    onTaskSelected = { onTaskSelected(task) },
                    onTaskCompleted = { }
                )
            }
        }
    }
}

@Composable
fun TaskRow(taskDao: TaskDao, onTaskSelected: , onTaskCompleted:) {
    Row(modifier = Modifier.clickable { onTaskSelected() }) {
        Button(onClick = { onTaskCompleted() }) {

        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = taskDao.title)
    }
}