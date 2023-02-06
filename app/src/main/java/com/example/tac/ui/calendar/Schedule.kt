package com.example.tac.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.tac.data.calendar.EventDao

@Composable
fun Schedule(
    events: List<EventDao>,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: EventDao) -> Unit = { Event(event = it) },
) {
    val hourHeight = 64.dp
    Layout(
        content = {
            events.sortedBy(EventDao::start).forEach { event ->
                Box(modifier = Modifier.eventData(event)) {
                    eventContent(event)
                }
            }
        },
        modifier = modifier,
    ) { measureables, constraints ->
        var height = 0
        val placeables = measureables.map { measurable ->
            val event = measurable.parentData as EventDao
            val placeable = measurable.measure(constraints.copy(maxHeight = 64.dp.roundToPx()))
            height += placeable.height
            placeable
        }
        layout(constraints.maxWidth, height) {
            var y = 0
            placeables.forEach { placeable ->
                placeable.place(0, y)
                y += placeable.height
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
