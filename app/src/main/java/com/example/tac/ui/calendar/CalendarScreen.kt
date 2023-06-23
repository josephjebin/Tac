package com.example.tac.ui.calendar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tac.data.calendar.EventDao
import java.time.LocalDateTime

@Composable
fun Calendar(
    modifier: Modifier,
    uiCalendarState: CalendarState
) {
    val hourHeight = 64.dp
    val verticalScrollState = rememberScrollState()
    Column(modifier = modifier) {
        DayHeader(uiCalendarState.selectedDate)

        Row(modifier = Modifier.weight(1f)) {
            HoursSidebar(
                hourHeight = hourHeight,
                modifier = Modifier.verticalScroll(verticalScrollState)
            )

            Schedule(
//                events = sampleEvents,
                events = uiCalendarState.events.filter { eventDao ->
                    eventDao.start.toLocalDate().equals(uiCalendarState.selectedDate)
                },
                hourHeight = hourHeight,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(verticalScrollState)
            )
        }
    }
}

//private val sampleEvents = listOf(
//    EventDao(
//        name = "Google I/O Keynote",
//        color = Color(0xFFAFBBF2),
//        start = LocalDateTime.parse("2021-05-18T13:00:00"),
//        end = LocalDateTime.parse("2021-05-18T15:00:00"),
//        description = "Tune in to find out about how we're furthering our mission to organize the world’s information and make it universally accessible and useful.",
//    ),
//    EventDao(
//        name = "Developer Keynote",
//        color = Color(0xFFAFBBF2),
//        start = LocalDateTime.parse("2021-05-18T15:15:00"),
//        end = LocalDateTime.parse("2021-05-18T16:00:00"),
//        description = "Learn about the latest updates to our developer products and platforms from Google Developers.",
//    ),
//    EventDao(
//        name = "What's new in Android",
//        color = Color(0xFF1B998B),
//        start = LocalDateTime.parse("2021-05-18T16:45:00"),
//        end = LocalDateTime.parse("2021-05-18T18:00:00"),
//        description = "In this Keynote, Chet Haase, Dan Sandler, and Romain Guy discuss the latest Android features and enhancements for developers.",
//    ),
//)