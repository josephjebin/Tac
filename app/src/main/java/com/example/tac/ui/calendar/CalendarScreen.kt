package com.example.tac.ui.calendar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Calendar(
    uiCalendarState: CalendarState
) {
    val hourHeight = 64.dp
    val verticalScrollState = rememberScrollState()
    Box {
        Row {
            HoursSidebar(
                hourHeight = hourHeight,
                modifier = Modifier.verticalScroll(verticalScrollState)
            )

            Schedule(
                events = uiCalendarState.events.filter { eventDao ->
                    eventDao.start.toLocalDate().equals(uiCalendarState.selectedDate.toLocalDate())
                },
                hourHeight = hourHeight,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(verticalScrollState)
            )
        }
    }
}