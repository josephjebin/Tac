package com.example.tac.ui.calendar

import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tac.ui.task.TasksViewModel
import com.example.tac.R
import com.example.tac.data.calendar.EventDao
import com.example.tac.ui.theme.surfaceGray
import java.time.LocalDateTime

@Composable
fun Calendar(tasksViewModel: TasksViewModel, calendarViewModel: CalendarViewModel) {
    val hourHeight = 64.dp
    Box(modifier = Modifier.background(surfaceGray).requiredHeightIn(max = LocalConfiguration.current.screenHeightDp.dp)) {
//        LazyVerticalGrid(columns = GridCells.Fixed(1)) {
//            items(items = get24Hours(), key = { hour -> hour }) { hour ->
//                CalendarRow(hour)
//            }
//        }
        Row(Modifier.requiredHeightIn(max = LocalConfiguration.current.screenHeightDp.dp)) {
//        val verticalScrollState = rememberScrollState()

//            HoursSidebar(
//                hourHeight = hourHeight,
////                modifier = Modifier.verticalScroll(verticalScrollState)
//            )
            Schedule(
//                modifier = Modifier.verticalScroll(verticalScrollState),
                events = sampleEvents,
                hourHeight = hourHeight
            )
        }

    }
}

@Composable
fun CalendarRow(hour: String) {
    Row(modifier = Modifier.border(BorderStroke(2.dp, Color.DarkGray))) {
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .width(60.dp)
                .align(Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$hour:")
        }

        Column() {
            val rowModifier: Modifier =
                Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(2.dp, Color.LightGray))
                    .padding(start = 8.dp)
            Row(modifier = rowModifier) {
                Text(text = "00")
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

private val sampleEvents = listOf(
    EventDao(
        name = "Google I/O Keynote",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-05-18T13:00:00"),
        end = LocalDateTime.parse("2021-05-18T15:00:00"),
        description = "Tune in to find out about how we're furthering our mission to organize the world’s information and make it universally accessible and useful.",
    ),
    EventDao(
        name = "Developer Keynote",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-05-18T15:15:00"),
        end = LocalDateTime.parse("2021-05-18T16:00:00"),
        description = "Learn about the latest updates to our developer products and platforms from Google Developers.",
    ),
    EventDao(
        name = "What's new in Android",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-05-18T16:45:00"),
        end = LocalDateTime.parse("2021-05-18T18:00:00"),
        description = "In this Keynote, Chet Haase, Dan Sandler, and Romain Guy discuss the latest Android features and enhancements for developers.",
    ),
)