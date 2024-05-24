package com.example.tac.ui.dragAndDrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.tac.data.calendar.EventDao
import com.example.tac.data.calendar.Plan
import com.example.tac.data.calendar.ScheduledTask
import com.example.tac.ui.calendar.PlanComposable
import java.time.ZonedDateTime

internal class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Plan>(
        ScheduledTask(
            name = "default",
            parentTaskId = "0",
            start = ZonedDateTime.now(),
            end = ZonedDateTime.now()
        )
    )
    var draggableHeight by mutableStateOf(0.dp)
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun RootDragInfoProvider(
//    planWidth: Dp,
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
                    alpha = .8f
                    scaleX = 1.0f
                    scaleY = 1.0f
                    translationX = 0.0f
                    translationY = offset.y.minus(180f).minus(.5f * state.draggableHeight.toPx())
                }
            ) {
                state.draggableComposable?.invoke()
//                Text(
//                    text = """
//                        targetSize: ${with(LocalDensity.current) { state.draggableHeight.toPx() }}
//                        currentPosition: ${state.dragPosition.y}
//                        currentOffset: ${with(LocalDensity.current) { 64.dp.toPx() }}
//                        translationY: ${with(LocalDensity.current) { (state.dragPosition + state.dragOffset).y}}
//                    """.trimIndent()
//                )
            }
        }
    }
}

@Composable
fun DragTarget(
    dataToDrop: Plan,
    modifier: Modifier = Modifier,
    draggableModifier: Modifier,
    draggableHeight: Dp,
    onTaskDrag: (() -> Unit) = {},
    content: @Composable () -> Unit
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var currentData by remember { mutableStateOf<Plan>(
            ScheduledTask(
                name = "default",
                parentTaskId = "0",
                start = ZonedDateTime.now(),
                end = ZonedDateTime.now()
            )
        )
    }
    var planComposableModifier by remember { mutableStateOf<Modifier>(Modifier) }
    var planComposableHeight by remember { mutableStateOf(0.dp) }


    currentData = dataToDrop
    planComposableModifier = draggableModifier
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
//                    println(currentState.dataToDrop)
//                    println(name)
                    currentState.draggableComposable = {
                        PlanComposable(
                            plan = currentData,
                            modifier = planComposableModifier
                        )
                    }
                    currentState.dragPosition = currentPosition + it
                    currentState.isDragging = true
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                }, onDragEnd = {
                    currentState.isDragging = false
                    currentState.dragOffset = Offset.Zero
                }, onDragCancel = {
                    currentState.dragOffset = Offset.Zero
                    currentState.isDragging = false
                })
        }
    ) {
        content()
    }
}

@Composable
fun <Plan> DropTarget(
    modifier: Modifier,
    content: @Composable() (BoxScope.(isInBound: Boolean, data: Plan?) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDropTarget by remember {
        mutableStateOf(false)
    }

    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
        }
    }) {
        val data = if (isCurrentDropTarget && !dragInfo.isDragging) dragInfo.dataToDrop as Plan else null
        if(dragInfo.dataToDrop is EventDao) {

        } else {

        }
        content(isCurrentDropTarget, data)
    }
}