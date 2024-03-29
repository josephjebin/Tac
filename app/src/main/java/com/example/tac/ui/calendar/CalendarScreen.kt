package com.example.tac.ui.calendar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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