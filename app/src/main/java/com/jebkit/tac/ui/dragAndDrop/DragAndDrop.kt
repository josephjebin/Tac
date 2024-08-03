package com.jebkit.tac.ui.dragAndDrop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jebkit.tac.data.calendar.EventDao
import com.jebkit.tac.data.calendar.Plan
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.ui.calendar.PlanComposable
import com.jebkit.tac.ui.theme.onSurfaceGray
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var isRescheduling by mutableStateOf(true)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableHeight by mutableStateOf(0.dp)
    var topOfDraggable by mutableStateOf(Offset.Zero)
    var currentDropTarget by mutableIntStateOf(-1)
    var currentDropTargetTime: LocalTime by mutableStateOf(LocalTime.MIN)
    var dataToDrop by mutableStateOf<Plan>(
        ScheduledTask(
            id = "defaultId",
            title = mutableStateOf("defaultTitle"),
            parentTaskId = "defaultParentTaskId",
            description = mutableStateOf("defaultDescription"),
            start = mutableStateOf(
                ZonedDateTime.of(
                    LocalDateTime.MIN,
                    ZoneId.systemDefault()
                )
            ),
            end = mutableStateOf(
                ZonedDateTime.of(
                    LocalDateTime.MIN,
                    ZoneId.systemDefault()
                )
            ),
            duration = mutableIntStateOf(30),
            color = mutableStateOf(onSurfaceGray)
        )
    )
    var draggableModifier: Modifier by mutableStateOf(Modifier)
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun RootDragInfoProvider(
    content: @Composable() (BoxScope.() -> Unit)
) {
    val state = remember { DragTargetInfo() }
    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        Box()
        {
            content()
        }
    }
}

@Composable
fun ScheduleDraggable() {
    val state = LocalDragTargetInfo.current
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isDragging) {
            Box(modifier = Modifier
                .graphicsLayer {
                    val offset = (state.dragPosition + state.dragOffset)
                    alpha = 1f
                    scaleX = 1.0f
                    scaleY = 1.0f
                    translationX = 0.0f
                    translationY = offset.y.minus(176f).minus(.5f * state.draggableHeight.toPx())
                }
            ) {
                PlanComposable(
                    title = state.dataToDrop.title.value,
                    description = state.dataToDrop.description.value,
                    color = state.dataToDrop.color.value,
                    start = state.currentDropTargetTime,
                    end = state.currentDropTargetTime.plusMinutes(state.dataToDrop.duration.intValue.toLong()),
                    modifier = state.draggableModifier
                )
            }
        }
    }
}

@Composable
fun DragTarget(
    dataToDrop: Plan,
    modifier: Modifier = Modifier,
    draggableHeight: Dp,
    isRescheduling: Boolean,
    onTaskDrag: (() -> Unit) = {},
    content: @Composable () -> Unit
) {
    val currentState = LocalDragTargetInfo.current
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var currentData by remember { mutableStateOf(dataToDrop) }
    var planComposableHeight by remember { mutableStateOf(0.dp) }
    var planComposableModifier: Modifier by remember { mutableStateOf(Modifier) }

    currentData = dataToDrop
    planComposableHeight = draggableHeight
    planComposableModifier = Modifier
        .height(planComposableHeight)
        .fillMaxWidth()
        .background(Color.Transparent)
        .border(1.dp, Color.Blue)

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    onTaskDrag()
                    currentState.dataToDrop = currentData
                    currentState.isRescheduling = isRescheduling
                    currentState.dragPosition = currentPosition + it
                    currentState.draggableHeight = planComposableHeight
                    currentState.topOfDraggable = Offset(
                        (currentPosition + it).x,
                        (currentPosition + it).y
                            .minus(planComposableHeight.toPx() * .5f)
                            .plus(29f)
                    )
                    currentState.draggableModifier = planComposableModifier
                    currentState.isDragging = true
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                    currentState.topOfDraggable += Offset(dragAmount.x, dragAmount.y)
                }, onDragEnd = {
                    currentState.isDragging = false
                    currentState.dragOffset = Offset.Zero
                }, onDragCancel = {
                    currentState.isDragging = false
                    currentState.dragOffset = Offset.Zero
                })
        }
    ) {
        content()
    }
}

@Composable
fun DropTarget(
    index: Int,
    selectedDate: LocalDate,
    timeSlot: LocalTime,
    addScheduledTask: (ScheduledTask) -> Unit,
    updateScheduledTaskTime: (ScheduledTask, ZonedDateTime) -> Unit,
    updateEventDaoTime: (EventDao, ZonedDateTime) -> Unit,
    modifier: Modifier,
    content: @Composable() (BoxScope.(isInBound: Boolean) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val topOfDraggable = dragInfo.topOfDraggable

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            if (dragInfo.isDragging && rect.contains(topOfDraggable)) {
                dragInfo.currentDropTarget = index
                dragInfo.currentDropTargetTime = timeSlot
            }
        }
    }) {
        //user dropped payload in this drop target
        if (dragInfo.currentDropTarget == index && !dragInfo.isDragging) {
            if (dragInfo.isRescheduling) {
                if (dragInfo.dataToDrop is ScheduledTask) {
                    updateScheduledTaskTime(
                        dragInfo.dataToDrop as ScheduledTask, ZonedDateTime.of(
                        LocalDateTime.of(selectedDate, timeSlot),
                        ZoneId.systemDefault()
                    ))
                } else {
                    updateEventDaoTime(dragInfo.dataToDrop as EventDao, ZonedDateTime.of(
                        LocalDateTime.of(selectedDate, timeSlot),
                        ZoneId.systemDefault()
                    ))
                }
            } else {
                val scheduledTask = dragInfo.dataToDrop as ScheduledTask
                scheduledTask.start.value = ZonedDateTime.of(
                    LocalDateTime.of(selectedDate, timeSlot),
                    ZoneId.systemDefault()
                )
                scheduledTask.end.value = scheduledTask.start.value.plusMinutes(scheduledTask.duration.intValue.toLong())
                addScheduledTask(scheduledTask)
            }

            //after updating viewmodel, reset currentDropTarget to prevent repeated calls to viewmodel
            dragInfo.currentDropTarget = -1
        }

        content(
            dragInfo.isDragging && dragInfo.currentDropTarget == index,
        )
    }
}