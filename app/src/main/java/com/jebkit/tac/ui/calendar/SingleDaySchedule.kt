package com.jebkit.tac.ui.calendar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.ui.dragAndDrop.CalendarDragTarget
//import com.jebkit.tac.ui.dragAndDrop.TimeDropTarget
import com.jebkit.tac.ui.tasks.TasksSheetState
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun SingleDaySchedule(
    selectedDate: LocalDate,
    eventDaos: List<EventDao>,
    scheduledTasks: List<ScheduledTask>,
    hourHeight: Dp,
    tasksSheetState: TasksSheetState,
    addScheduledTask: (ScheduledTask) -> Unit,
    updateScheduledTaskTime: (ScheduledTask, ZonedDateTime) -> Unit,
    updateEventDaoTime: (EventDao, ZonedDateTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    //TODO: light and dark themes
//    val dividerColor = if (MaterialTheme.colors.isLight) Color.LightGray else Color.DarkGray

    Layout(
        content = {
            eventDaos
                .sortedBy { eventDao -> eventDao.start.value }
                .forEach { eventDao ->
                    val eventHeight = ((eventDao.duration.intValue / 60f) * hourHeight)
                    val planComposableModifier = Modifier
                        .startData(eventDao.start.value.toLocalTime())
                        .height(eventHeight)
                        .fillMaxWidth()

                    CalendarDragTarget(
                        dataToDrop = eventDao,
                        modifier = planComposableModifier,
                        draggableHeight = eventHeight,
                    )
                }

            scheduledTasks
                .sortedBy { scheduledTask -> scheduledTask.start.value }
                .forEach { scheduledTask ->
                    val taskHeight = ((scheduledTask.duration.intValue / 60f) * hourHeight)
                    val planComposableModifier = Modifier
                        .startData(scheduledTask.start.value.toLocalTime())
                        .height(taskHeight)
                        .fillMaxWidth()

                    CalendarDragTarget(
                        dataToDrop = scheduledTask,
                        modifier = planComposableModifier,
                        draggableHeight = taskHeight,
                    )
                }

//            if (tasksSheetState == TasksSheetState.COLLAPSED) {
//                DropTargets(
//                    fiveMinuteHeight = hourHeight / 12,
//                    selectedDate = selectedDate,
//                    addScheduledTask = addScheduledTask,
//                    updateScheduledTaskTime = updateScheduledTaskTime,
//                    updateEventDaoTime = updateEventDaoTime
//                )
//            }
        },
        modifier = modifier
            .drawBehind {
                repeat(23) {
                    drawLine(
                        Color(65, 66, 70),
                        start = Offset(0f, (it + 1) * hourHeight.toPx()),
                        end = Offset(size.width, (it + 1) * hourHeight.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
    ) { measureables, constraints ->
        val height = hourHeight.roundToPx() * 24
        layout(constraints.maxWidth, height) {
            measureables.forEach { measurable ->
                val eventOffsetMinutes =
                    ChronoUnit.MINUTES.between(
                        LocalTime.MIN,
                        (measurable.parentData as LocalTime)
                    )
                val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                val placeable = measurable.measure(constraints.copy())
                placeable.place(0, eventY)
            }
        }
    }
}

//@Composable
//fun DropTargets(
//    fiveMinuteHeight: Dp,
//    selectedDate: LocalDate,
//    addScheduledTask: (ScheduledTask) -> Unit,
//    updateScheduledTaskTime: (ScheduledTask, ZonedDateTime) -> Unit,
//    updateEventDaoTime: (EventDao, ZonedDateTime) -> Unit
//) {
//    repeat(288) {
//        val timeSlot: LocalTime = LocalTime.MIN.plusMinutes(it * 5L)
//
//        TimeDropTarget(
//            index = it,
//            timeSlot = timeSlot,
//            selectedDate = selectedDate,
//            addScheduledTask = addScheduledTask,
//            updateScheduledTaskTime = updateScheduledTaskTime,
//            updateEventDaoTime = updateEventDaoTime,
//            modifier = Modifier
//                .startData(timeSlot)
//        ) { isCurrentDropTarget ->
//            Box(
//                modifier = Modifier
//                    .height(fiveMinuteHeight)
//                    .fillMaxWidth()
//                    .background(if (isCurrentDropTarget) Color.LightGray else Color.Transparent)
//            )
//        }
//    }
//}

private class DataModifier(
    val start: LocalTime,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = start
}

fun Modifier.startData(start: LocalTime) = this.then(DataModifier(start))

