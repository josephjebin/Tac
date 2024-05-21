package com.example.tac.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.Plan
import com.example.tac.data.calendar.ScheduledTask
import com.example.tac.ui.dragAndDrop.EventDragTarget
import com.example.tac.ui.dragAndDrop.ScheduledTaskDragTarget
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun Schedule(
    events: List<EventDao>,
    scheduledTasks: List<ScheduledTask>,
    hourHeight: Dp,
    modifier: Modifier = Modifier,
//    planContent: @Composable (plan: Plan, modifier: Modifier) -> Unit,
) {
    val dividerColor = if (MaterialTheme.colors.isLight) Color.LightGray else Color.DarkGray
//    var scheduleSize by remember {
//        mutableStateOf(IntSize.Zero)
//    }
    Layout(
        content = {
            events.sortedBy(EventDao::start).forEach { event ->
                val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
                val eventHeight = ((eventDurationMinutes / 60f) * hourHeight)
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp

//                Box(modifier = Modifier
//                    .eventData(event)
//                ) {
                EventDragTarget(
                    modifier = Modifier.eventData(event),
                    dataToDrop = event
                ) {
                    PlanComposable(
                        event,
                        Modifier
//                            .fillMaxSize()
                            .height(eventHeight)
                            .fillMaxWidth()
//                                .width(scheduleSize.width.dp)
                    )
//                    }
                }
            }

            scheduledTasks.sortedBy(ScheduledTask::start).forEach { scheduledTask ->
                val eventDurationMinutes =
                    ChronoUnit.MINUTES.between(scheduledTask.start, scheduledTask.end)
                val eventHeight = ((eventDurationMinutes / 60f) * hourHeight)

                Box(modifier = Modifier.scheduledTaskData(scheduledTask)) {
                    ScheduledTaskDragTarget(modifier = Modifier, dataToDrop = scheduledTask) {
                        PlanComposable(
                            scheduledTask,
                            Modifier
                                .height(eventHeight)
//                                .width(scheduleSize.width.dp)
                        )
                    }
                }
            }
        },
        modifier = modifier
//            .onGloballyPositioned {
//                scheduleSize = it.size
//            }
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
//        val placeablesWithEvents = measureables.map { measurable ->
//            val event = measurable.parentData as Plan
//            val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
//            val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
//            val placeable = measurable.measure(constraints.copy(minHeight = eventHeight, maxHeight = eventHeight))
//            Pair(placeable, event)
//        }

        layout(constraints.maxWidth, height) {
            measureables.forEach { measurable ->
                val eventOffsetMinutes =
                    ChronoUnit.MINUTES.between(LocalTime.MIN, (measurable.parentData as Plan).start.toLocalTime())
                val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
//                val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
                val placeable = measurable.measure(constraints.copy())
                placeable.place(0, eventY)
            }
        }
    }
}

private class EventDataModifier(
    val event: EventDao,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = event
}

private fun Modifier.eventData(event: EventDao) = this.then(EventDataModifier(event))

private class ScheduledTaskDataModifier(
    val scheduledTask: ScheduledTask,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = scheduledTask
}

private fun Modifier.scheduledTaskData(scheduledTask: ScheduledTask) =
    this.then(ScheduledTaskDataModifier(scheduledTask))