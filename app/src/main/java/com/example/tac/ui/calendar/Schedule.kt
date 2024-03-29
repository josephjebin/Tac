package com.example.tac.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.tac.data.calendar.EventDao
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Composable
fun Schedule(
    events: List<EventDao>,
    hourHeight: Dp,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: EventDao) -> Unit = { Event(event = it) },
) {
    val dividerColor = if (MaterialTheme.colors.isLight) Color.LightGray else Color.DarkGray
    Layout(
        content = {
            events.sortedBy(EventDao::start).forEach { event ->
                Box(modifier = Modifier.eventData(event)) {
                    eventContent(event)
                }
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
        val placeablesWithEvents = measureables.map { measurable ->
            val event = measurable.parentData as EventDao
            val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
            val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(constraints.copy(minHeight = eventHeight, maxHeight = eventHeight))
            Pair(placeable, event)
        }
        layout(constraints.maxWidth, height) {
            placeablesWithEvents.forEach { (placeable, event) ->
                val eventOffsetMinutes = ChronoUnit.MINUTES.between(LocalTime.MIN, event.start.toLocalTime())
                val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
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
