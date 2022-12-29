package com.example.tac.ui.task

import android.provider.ContactsContract.Data
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tac.data.Task
import com.example.tac.data.Datasource
import com.google.api.services.tasks.model.TaskList

@Composable
fun TaskSheet(projects: List<TaskList>) {
    val datasource = Datasource()
    Column() {
        val tasksViewModel = TaskViewModel()
        LazyRow() {
            items(projects.size) {index ->
                Card() {
                    Text(text = projects[index].title)
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(items = datasource.listOfTasks, key = { task ->
                task.id
            }) { task ->
                TaskRow(task)
            }
        }
    }
}

@Composable
fun TaskRow(task: Task) {
    Row() {
        Button(onClick = { /*TODO*/ }) {

        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = task.text)


    }
}