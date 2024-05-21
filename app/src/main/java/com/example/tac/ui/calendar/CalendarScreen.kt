package com.example.tac.ui.calendar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.tac.ui.dragAndDrop.LongPressDraggable

@Composable
fun Calendar(
    uiCalendarState: CalendarState,
//    updateSidebarWidth: (Dp) -> Unit,
//    updateScheduleWidth: (Dp) -> Unit,
) {
    val hourHeight = 64.dp
    val verticalScrollState = rememberScrollState()
    Box {
        Row {
            HoursSidebar(
                hourHeight = hourHeight,
                modifier = Modifier
//                    .onGloballyPositioned {
//                        updateSidebarWidth(it.size.width.dp)
//                    }
                    .verticalScroll(verticalScrollState)
            )


            Box(modifier = Modifier) {
                LongPressDraggable {
                    Schedule(
                        events = uiCalendarState.events.filter { eventDao ->
                            eventDao.start.toLocalDate()
                                .equals(uiCalendarState.selectedDate.toLocalDate())
                        },
                        scheduledTasks = uiCalendarState.scheduledTasks.filter { scheduledTask ->
                            scheduledTask.start.toLocalDate()
                                .equals(uiCalendarState.selectedDate.toLocalDate())
                        },
                        hourHeight = hourHeight,
                        modifier = Modifier
                            //                        .onGloballyPositioned {
                            //                            updateScheduleWidth(it.size.width.dp)
                            //                        }
                            .verticalScroll(verticalScrollState)
                    )
                }
            }
        }
    }
}