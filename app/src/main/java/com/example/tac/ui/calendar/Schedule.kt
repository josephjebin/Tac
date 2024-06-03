package com.example.tac.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
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
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.ScheduledTask
import com.example.tac.ui.dragAndDrop.DragTarget
import com.example.tac.ui.dragAndDrop.DropTarget
import com.example.tac.ui.task.TasksSheetState
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun Schedule(
    selectedDate: LocalDate,
    events: List<EventDao>,
    scheduledTasks: List<ScheduledTask>,
    hourHeight: Dp,
    tasksSheetState: TasksSheetState,
    addScheduledTask: (ScheduledTask) -> Unit,
    removeScheduledTask: (ScheduledTask) -> Unit,
    addEventDao: (EventDao) -> Unit,
    removeEventDao: (EventDao) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dividerColor = if (MaterialTheme.colors.isLight) Color.LightGray else Color.DarkGray

    Layout(
        content = {
            events.sortedBy { eventDao -> eventDao.start.value }.forEach { event ->
                val eventHeight = ((event.duration / 60f) * hourHeight)
                val planComposableModifier = Modifier
                    .startData(event.start.value.toLocalTime())
                    .height(eventHeight)
                    .fillMaxWidth()

                DragTarget(
                    dataToDrop = event,
                    modifier = planComposableModifier,
                    draggableHeight = eventHeight,
                    isRescheduling = true
                ) {
                    PlanComposable(
                        name = event.name,
                        description = event.description,
                        color = event.color,
                        start = event.start.value.toLocalTime(),
                        end = event.end.value.toLocalTime()
                    )
                }
            }

            scheduledTasks.sortedBy { scheduledTask -> scheduledTask.start.value }.forEach { scheduledTask ->
                val eventDurationMinutes =
                    ChronoUnit.MINUTES.between(scheduledTask.start.value, scheduledTask.end.value)
                val taskHeight = ((eventDurationMinutes / 60f) * hourHeight)
                val planComposableModifier = Modifier
                    .startData(scheduledTask.start.value.toLocalTime())
                    .height(taskHeight)
                    .fillMaxWidth()

                DragTarget(
                    dataToDrop = scheduledTask,
                    modifier = planComposableModifier,
                    draggableHeight = taskHeight,
                    isRescheduling = true
                ) {
                    PlanComposable(
                        name = scheduledTask.name,
                        description = scheduledTask.description,
                        color = scheduledTask.color,
                        start = scheduledTask.start.value.toLocalTime(),
                        end = scheduledTask.end.value.toLocalTime()
                    )
                }
            }

            if (tasksSheetState == TasksSheetState.COLLAPSED) {
                DropTargets(
                    fiveMinuteHeight = hourHeight / 12,
                    selectedDate = selectedDate,
                    removeScheduledTask = removeScheduledTask,
                    removeEventDao = removeEventDao,
                    addScheduledTask = addScheduledTask,
                    addEventDao = addEventDao
                )
            }
        },
        modifier = modifier
            .drawBehind {
                repeat(23) {
                    drawLine(
                        dividerColor,
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

@Composable
fun DropTargets(
    fiveMinuteHeight: Dp,
    selectedDate: LocalDate,
    addScheduledTask: (ScheduledTask) -> Unit,
    removeScheduledTask: (ScheduledTask) -> Unit,
    addEventDao: (EventDao) -> Unit,
    removeEventDao: (EventDao) -> Unit
) {
    repeat(288) {
        val timeSlot: LocalTime = LocalTime.MIN.plusMinutes(it * 5L)

        DropTarget(
            index = it,
            timeSlot = timeSlot,
            selectedDate = selectedDate,
            addScheduledTask = addScheduledTask,
            removeScheduledTask = removeScheduledTask,
            addEventDao = addEventDao,
            removeEventDao = removeEventDao,
            modifier = Modifier
                .startData(timeSlot)
        ) { isCurrentDropTarget ->
            Box(
                modifier = Modifier
                    .height(fiveMinuteHeight)
                    .fillMaxWidth()
                    .background(if (isCurrentDropTarget) Color.LightGray else Color.Transparent)
            )
        }
    }
}

private class DataModifier(
    val start: LocalTime,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = start
}

fun Modifier.startData(start: LocalTime) = this.then(DataModifier(start))

