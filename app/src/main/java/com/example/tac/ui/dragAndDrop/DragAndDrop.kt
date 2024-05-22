package com.example.tac.ui.dragAndDrop

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
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
}

enum class DraggableType {
    EVENT,
    SCHEDULED_TASK,
    TASK
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun RootDragInfoProvider(
    modifier: Modifier = Modifier,
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
            var targetSize by remember {
                mutableStateOf(IntSize.Zero)
            }

            Box(modifier = Modifier
                .graphicsLayer {
                    val offset = (state.dragPosition + state.dragOffset)
                    alpha = .8f
                    scaleX = 1.0f
                    scaleY = 1.0f
                    translationX = 0.0f
                    translationY = offset.y.minus(targetSize.height)
                }
                .onGloballyPositioned {
                    targetSize = it.size
                }
            ) {
                    state.draggableComposable?.invoke()
            }
        }
    }
}

@Composable
fun DragTarget(
    dataToDrop: Plan,
    modifier: Modifier,
    draggableModifier: Modifier,
    content: @Composable () -> Unit
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(onDragStart = {
                currentState.isDragging = true
                currentState.dataToDrop = dataToDrop
                currentState.dragPosition = currentPosition + it
                currentState.draggableComposable = {
                    PlanComposable(
                        plan = dataToDrop,
                        modifier = draggableModifier
                    )
                }
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

//
//@Composable
//fun <Plan> DropTarget(
//    modifier: Modifier,
//    content: @Composable() (BoxScope.(isInBound: Boolean, data: Plan) -> Unit)
//) {
//    val dragInfo = LocalDragTargetInfo.current
//    val dragPosition = dragInfo.dragPosition
//    val dragOffset = dragInfo.dragOffset
//    var isCurrentDropTarget by remember {
//        mutableStateOf(false)
//    }
//
//    Box(modifier = modifier.onGloballyPositioned {
//        it.boundsInWindow().let { rect ->
//            isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
//        }
//    }) {
//        if(dragInfo.dataToDrop is EventDao) {
//
//        } else {
//
//        }
//        val data =
//            if (isCurrentDropTarget && !dragInfo.isDragging) dragInfo.dataToDrop as Plan else null
//        content(isCurrentDropTarget, data)
//    }
//}