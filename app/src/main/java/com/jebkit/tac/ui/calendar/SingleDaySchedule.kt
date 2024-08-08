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
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun SingleDaySchedule(
    eventDaos: List<EventDao>,
    scheduledTasks: List<ScheduledTask>,
    hourHeight: Dp,
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

private class DataModifier(
    val start: LocalTime,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = start
}

fun Modifier.startData(start: LocalTime) = this.then(DataModifier(start))

