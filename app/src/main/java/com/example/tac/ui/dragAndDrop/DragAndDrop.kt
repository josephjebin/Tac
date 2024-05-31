package com.example.tac.ui.dragAndDrop

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
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.Plan
import com.example.tac.data.calendar.ScheduledTask
import com.example.tac.ui.calendar.PlanComposable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)

    //    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Plan>(
        ScheduledTask(
            id = 0,
            name = "default",
            parentTaskId = "0",
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
            )
        )
    )
    var draggableHeight by mutableStateOf(0.dp)
    var topOfDraggable by mutableStateOf(Offset.Zero)
    var currentDropTarget by mutableIntStateOf(-1)
    var isRescheduling by mutableStateOf(true)
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
                    plan = state.dataToDrop,
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
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var currentData by remember {
        mutableStateOf<Plan>(
            ScheduledTask(
                id = 0,
                name = "default",
                parentTaskId = "0",
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
                )
            )
        )
    }
//    var planComposableModifier by remember { mutableStateOf<Modifier>(Modifier) }
    var planComposableHeight by remember { mutableStateOf(0.dp) }

    currentData = dataToDrop
    val draggableModifier = Modifier
        .height(draggableHeight)
        .fillMaxWidth()
        .background(Color.Transparent)
        .border(1.dp, Color.Blue)
    planComposableHeight = draggableHeight
    val currentState = LocalDragTargetInfo.current

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    onTaskDrag()
                    currentState.dataToDrop = currentData
                    currentState.draggableHeight = planComposableHeight
                    currentState.isRescheduling = isRescheduling
                    currentState.draggableModifier = draggableModifier
//                    currentState.draggableComposable = {
//                        PlanComposable(
//                            plan = currentData,
//                            modifier = planComposableModifier
//                        )
//                    }
                    currentState.dragPosition = currentPosition + it
                    currentState.isDragging = true
                    currentState.topOfDraggable = Offset(
                        (currentPosition + it).x,
                        (currentPosition + it).y
                            .minus(planComposableHeight.toPx() * .5f)
                            .plus(29f)
                    )
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                    currentState.topOfDraggable += Offset(dragAmount.x, dragAmount.y)
                }, onDragEnd = {
                    currentState.isDragging = false
                    currentState.dragOffset = Offset.Zero
//                    currentState.currentDropTarget = -1
                }, onDragCancel = {
                    currentState.isDragging = false
                    currentState.dragOffset = Offset.Zero
//                    currentState.currentDropTarget = -1
                })
        }
    ) {
        content()
    }
}

@Composable
fun <Plan> DropTarget(
    index: Int,
    selectedDate: LocalDate,
    timeSlot: LocalTime,
    addScheduledTask: (ScheduledTask) -> Unit,
    removeScheduledTask: (ScheduledTask) -> Unit,
    addEventDao: (EventDao) -> Unit,
    removeEventDao: (EventDao) -> Unit,
    modifier: Modifier,
    content: @Composable() (BoxScope.(isInBound: Boolean) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val topOfDraggable = dragInfo.topOfDraggable

    //if the first box is covered
    //the first box is the current drop target
    //else if last box is covered
    //the box that is draggableHeight above the last box is the current drop target
    //else
    //the box that is .5 * draggable height above the pointer && the box above is not covered is the current drop target
    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            if (dragInfo.isDragging && rect.contains(topOfDraggable)) dragInfo.currentDropTarget =
                index
        }
    }) {
        //user dropped droppable in this drop target
        if (dragInfo.currentDropTarget == index && !dragInfo.isDragging) {
            if (dragInfo.isRescheduling) {
                if (dragInfo.dataToDrop is ScheduledTask) {
                    removeScheduledTask(dragInfo.dataToDrop as ScheduledTask)
                } else {
                    removeEventDao(dragInfo.dataToDrop as EventDao)
                }
            }

            dragInfo.dataToDrop.start.value = ZonedDateTime.of(
                LocalDateTime.of(selectedDate, timeSlot),
                ZoneId.systemDefault()
            )
            dragInfo.dataToDrop.end.value = ZonedDateTime.of(
                LocalDateTime.of(selectedDate, timeSlot)
                    .plusMinutes(dragInfo.dataToDrop.duration.toLong()),
                ZoneId.systemDefault()
            )

            if (dragInfo.dataToDrop is ScheduledTask) {
                addScheduledTask(dragInfo.dataToDrop as ScheduledTask)
            } else {
                addEventDao(dragInfo.dataToDrop as EventDao)
            }

            //after updating viewmodel, reset currentDropTarget to prevent repeated calls to viewmodel
            dragInfo.currentDropTarget = -1
        }

        content(
            dragInfo.isDragging && dragInfo.currentDropTarget == index,
        )
    }
}