package com.example.tac.ui.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tac.data.Datasource
import com.example.tac.data.Task

@Composable
fun TaskSheet() {
    Column() {
        val datasource = Datasource()
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