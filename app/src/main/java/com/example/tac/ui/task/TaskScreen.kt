package com.example.tac.ui.task

import androidx.compose.foundation.layout.*
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
fun TaskSheet(projects: List<TaskList?>) {
    Column() {
        //projects
        LazyRow() {
            items(projects.size) { index ->
                Card(modifier = Modifier.padding(16.dp)) {
                    projects[index]?.let { Text(text = it.title) }
                }
            }
        }

        //tasks
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(1),
//            contentPadding = PaddingValues(4.dp)
//        ) {
//            items(items = datasource.listOfTasks, key = { task ->
//                task.id
//            }) { task ->
//                TaskRow(task)
//            }
//        }
    }
}

@Composable
fun TaskRow(taskDao: TaskDao) {
    Row() {
        Button(onClick = { /*TODO*/ }) {

        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = taskDao.title)


    }
}