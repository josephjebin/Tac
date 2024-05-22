package com.example.tac.ui.dragAndDrop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.tac.R
import com.example.tac.data.tasks.TaskDao

internal class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableType by mutableStateOf(DraggableType.TASK)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
    var size by mutableStateOf(IntSize.Zero)
}

enum class DraggableType {
    EVENT,
    SCHEDULED_TASK,
    TASK
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun LongPressDraggable(
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
                        translationX =
                            if (state.draggableType == DraggableType.TASK)
                                offset.x.minus(targetSize.width / 2)
                            else 0.0f
                        translationY = offset.y.minus(targetSize.height)
                    }
                    .onGloballyPositioned {
                        targetSize = it.size
                    }
                ) {
                    if(state.draggableType == DraggableType.TASK) {

                    } else {
//                        Box(modifier = Modifier.width(88.dp)) {
                        state.draggableComposable?.invoke()
//                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> EventDragTarget(
    modifier: Modifier,
    dataToDrop: T,
//    viewModel:
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(onDragStart = {
                currentState.dataToDrop = dataToDrop
                currentState.draggableType = DraggableType.EVENT
                currentState.isDragging = true
                currentState.dragPosition = currentPosition + it
                currentState.draggableComposable = content
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
fun <T> ScheduledTaskDragTarget(
    modifier: Modifier,
    dataToDrop: T,
//    viewModel:
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(onDragStart = {
                currentState.dataToDrop = dataToDrop
                currentState.draggableType = DraggableType.SCHEDULED_TASK
                currentState.isDragging = true
                currentState.dragPosition = currentPosition + it
                currentState.draggableComposable = content
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
fun <T> TaskDragTarget(
    modifier: Modifier,
    dataToDrop: T,
//    viewModel:
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(onDragStart = {
                currentState.dataToDrop = dataToDrop
                currentState.draggableType = DraggableType.TASK
                currentState.isDragging = true
                currentState.dragPosition = currentPosition + it
                currentState.draggableComposable = content
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
fun TaskRow(
    taskDao: TaskDao,
//    onTaskDrag: () -> Unit,
    onTaskSelected: (TaskDao) -> Unit,
    onTaskCompleted: (TaskDao) -> Unit
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .clickable { onTaskSelected(taskDao) }
//        .pointerInput(Unit) {
//            detectDragGesturesAfterLongPress(onDragStart = {
//                currentState.dataToDrop = ""
//                currentState.draggableType = DraggableType.TASK
//                currentState.isDragging = true
//                currentState.dragPosition = currentPosition + it
//                currentState.draggableComposable = content
//            }, onDrag = { change, dragAmount ->
//                change.consume()
//                currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
//            }, onDragEnd = {
//                currentState.isDragging = false
//                currentState.dragOffset = Offset.Zero
//            }, onDragCancel = {
//                currentState.dragOffset = Offset.Zero
//                currentState.isDragging = false
//            })
//        }
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { onTaskCompleted(taskDao) }) {
            Icon(painterResource(id = R.drawable.priority3_button), "")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .widthIn(max = 160.dp),
            text = taskDao.title,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            //due date
            Row() {
                Icon(
                    modifier = Modifier.scale(.8f),
                    painter = painterResource(id = R.drawable.round_calendar_today_24),
                    contentDescription = "Due date"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = taskDao.due)
            }
            //duration
            Row() {
                Icon(
                    modifier = Modifier.scale(.8f),
                    painter = painterResource(id = R.drawable.round_access_time_24),
                    contentDescription = "Duration"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${taskDao.totalDuration} minutes")
            }
        }
    }
}


@Composable
fun <T> DropTarget(
    modifier: Modifier,
    content: @Composable() (BoxScope.(isInBound: Boolean, data: T?) -> Unit)
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
        val data =
            if (isCurrentDropTarget && !dragInfo.isDragging) dragInfo.dataToDrop as T? else null
        content(isCurrentDropTarget, data)
    }
}