package com.example.tac.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tac.ui.task.TasksViewModel
import com.example.tac.R

@Composable
fun Calendar(tasksViewModel: TasksViewModel) {
    Box {
        LazyVerticalGrid(columns = GridCells.Fixed(1)) {
            items(items = get24Hours(), key = { hour -> hour }) { hour ->
                CalendarRow(hour)
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            onClick = { tasksViewModel.getTaskLists() },
            content = {
                Icon(painter = painterResource(id = R.drawable.round_refresh_24),
                    contentDescription = "Refresh")
            }
        )
    }
}

@Composable
fun CalendarRow(hour: String) {
    Row(modifier = Modifier.border(BorderStroke(2.dp, Color.DarkGray))) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .width(60.dp)
        ) {
            Text(text = hour)
        }

        Column() {
            val rowModifier: Modifier =
                Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(2.dp, Color.LightGray))
                    .padding(start = 8.dp)
            Row(modifier = rowModifier) {
                Text(text = "0")
            }
            Row(modifier = rowModifier) {
                Text(text = "15")
            }
            Row(modifier = rowModifier) {
                Text(text = "30")
            }
            Row(modifier = rowModifier) {
                Text(text = "45")
            }
        }
    }
}

private fun get24Hours(): List<String> {
    val hours = mutableListOf<String>()
    for (i in 0..23) hours.add("$i")
    return hours
}