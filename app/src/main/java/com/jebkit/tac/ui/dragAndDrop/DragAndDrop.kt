package com.jebkit.tac.ui.dragAndDrop

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jebkit.tac.R
import com.jebkit.tac.data.calendar.Plan
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.ui.calendar.PlanComposable
import com.jebkit.tac.ui.theme.akiflow_lavender
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

internal class DragTargetInfo {
    var minuteVerticalOffset by mutableFloatStateOf(0f)
    var fiveMinuteVerticalOffset by mutableFloatStateOf(0f)

    //TODO: delete - don't need
    var headerVerticalOffset by mutableFloatStateOf(0f)

    var dragStartedFromTaskSheet by mutableStateOf(false)
    var dragStartedFromCalendar by mutableStateOf(false)

    var isDragging: Boolean by mutableStateOf(false)

    //TODO: used for determining if user's pointer is in the cancel region when dragging
    var windowPointerOffset by mutableStateOf(Offset.Zero)

    //need to maintain top of draggable instead of just deriving it from pointer offset since
    //clicking and dragging from the calendar should just lift the draggable and is independent of
    //pointer offset
    var topOfDraggableOffset by mutableStateOf(Offset.Zero)

    //used for calculating change in time
    var dragVerticalOffset by mutableFloatStateOf(0f)

    //TODO: can we delete and derive this?
    //we care about change in time by increments of 5 minutes because that's the precision used
    //for updating calendar events when dragging
    //e.g. if we dragged an event an hour up, that's 12 5-minute increments upwards,
    //so timeChangeInIncrementsOfFiveMinutes would be -12
    var timeChangeInIncrementsOfFiveMinutes by mutableIntStateOf(0)



    //TODO: delete - we use offset from parent instead
    var calendarScrollState: ScrollState? by mutableStateOf(null)


    //    var draggableHeight by mutableStateOf(0.dp)
//    var topOfDraggable by mutableStateOf(Offset.Zero)
//    var currentDropTarget by mutableIntStateOf(-1)
//    var currentDropTargetTime: LocalTime by mutableStateOf(LocalTime.MIN)
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
            color = mutableStateOf(akiflow_lavender)
        )
    )
    var draggableModifier: Modifier by mutableStateOf(Modifier)
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun RootDragInfoProvider(
    minuteVerticalOffset: Float,
    //TODO: remove since we covert offset to local
    headerVerticalOffset: Float,
    calendarScrollState: ScrollState,
    content: @Composable() (BoxScope.() -> Unit)
) {
    val state = remember { DragTargetInfo() }
    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        state.minuteVerticalOffset = minuteVerticalOffset
        state.fiveMinuteVerticalOffset = 5 * minuteVerticalOffset
        state.headerVerticalOffset = headerVerticalOffset
        state.calendarScrollState = calendarScrollState

        Box()
        {
            content()

            Box {
                Text(
                    text =
                    """windowPointerOffset: ${state.windowPointerOffset},
                        |dragVerticalOffset: ${state.dragVerticalOffset},
                        |topOfDraggableOffset: ${state.topOfDraggableOffset} 
                    """.trimMargin(),
                    color = colorResource(id = R.color.google_text_white)
                )
            }
        }
    }
}

//@Composable
//fun CalendarDraggable() {
//    val state = LocalDragTargetInfo.current
//    var bounds: Rect by remember { mutableStateOf(Rect(Offset.Zero, Offset.Zero)) }
//    var layoutCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
//
//    Box(modifier = Modifier
//        .fillMaxSize()
//        .background(Color.Transparent)
//        .onGloballyPositioned {
//            bounds = it.boundsInParent()
//            layoutCoordinates = it
//
//        }
//    ) {
//        if (state.isDragging) {
////            if(state.isPointerInCancelRegion) highlight border else
//            state.timeChangeInIncrementsOfFiveMinutes =
//                (state.dragVerticalOffset / state.fiveMinuteVerticalOffset).roundToInt()
//            val verticalOffset =
//                (state.composableStartOffset.y.plus(state.dragVerticalOffset))
//            Box(modifier = Modifier
//                .graphicsLayer {
//                    alpha = 1f
//                    scaleX = 1.0f
//                    scaleY = 1.0f
//                    translationX = 0.0f
//                    translationY = verticalOffset
//                    //.minus(295)
////                    translationY = offset.y.minus(176f).minus(.5f * state.draggableHeight.toPx())
//                }
//            ) {
//                PlanComposable(
//                    title = state.dataToDrop.title.value,
//                    description = state.dataToDrop.description.value,
//                    color = state.dataToDrop.color.value,
//                    start = state.dataToDrop.start.value.toLocalTime()
//                        .plusMinutes(5 * state.timeChangeInIncrementsOfFiveMinutes.toLong()),
//                    end = state.dataToDrop.end.value.toLocalTime()
//                        .plusMinutes(5 * state.timeChangeInIncrementsOfFiveMinutes.toLong()),
//                    modifier = state.draggableModifier
//                )
//            }
//        }
//    }
//}

@Composable
fun CalendarDragTarget(
    dataToDrop: Plan,
    modifier: Modifier = Modifier,
    draggableHeight: Dp,
    onTaskDrag: (() -> Unit) = {},
) {
    val currentState = LocalDragTargetInfo.current
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var currentData by remember { mutableStateOf(dataToDrop) }
    var planComposableHeight by remember { mutableStateOf(0.dp) }
    var planComposableModifier: Modifier by remember { mutableStateOf(Modifier) }
    var layoutCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }

    currentData = dataToDrop
    planComposableHeight = draggableHeight
    planComposableModifier = Modifier
        .height(planComposableHeight)
        .fillMaxWidth()
        .background(Color.Transparent)
        .border(1.dp, Color.Blue)

    Box(modifier = modifier
        .onGloballyPositioned {
            layoutCoordinates = it
            currentPosition = it.positionInWindow()
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    onTaskDrag()
                    currentState.dataToDrop = currentData
                    currentState.topOfDraggableOffset = currentPosition
                    currentState.windowPointerOffset = layoutCoordinates!!.localToWindow(it)
//                    currentState.draggableHeight = planComposableHeight
//                    currentState.topOfDraggable = Offset(
//                        (currentPosition + it).x,
//                        (currentPosition + it).y
//                            .minus(planComposableHeight.toPx() * .5f)
//                            .plus(29f)
//                    )
                    currentState.draggableModifier = planComposableModifier
                    currentState.dragStartedFromCalendar = true
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    currentState.dragVerticalOffset += dragAmount.y
//                    currentState.topOfDraggable += Offset(dragAmount.x, dragAmount.y)
                }, onDragEnd = {
                    currentState.isDragging = false
                    currentState.dragStartedFromCalendar = false
                    currentState.dragVerticalOffset = 0f
                    //logic to update event time
                }, onDragCancel = {
                    currentState.isDragging = false
                    currentState.dragStartedFromCalendar = false
                    currentState.dragVerticalOffset = 0f
                })
        }
    ) {
        PlanComposable(
            title = dataToDrop.title.value,
            description = dataToDrop.description.value,
            color = dataToDrop.color.value,
            start = dataToDrop.start.value.toLocalTime(),
            end = dataToDrop.end.value.toLocalTime(),
            modifier = planComposableModifier
        )
    }
}

@Composable
fun Draggable() {
    val state = LocalDragTargetInfo.current
    var bounds: Rect by remember { mutableStateOf(Rect(Offset.Zero, Offset.Zero)) }
    var layoutCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
    //used for spawning the draggable at the right position
    //TODO: move to draggable
    var composableStartOffset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier
        .fillMaxSize()
        .onGloballyPositioned {
            layoutCoordinates = it
            bounds = it.boundsInParent()
        }
        .background(Color.Transparent)
    ) {
//            if(state.isPointerInCancelRegion) highlight border else
        if (state.isDragging) {
            state.timeChangeInIncrementsOfFiveMinutes =
                (state.dragVerticalOffset / state.fiveMinuteVerticalOffset).roundToInt()
            Box(modifier = Modifier
                .graphicsLayer {
                    alpha = 1f
                    scaleX = 1.0f
                    scaleY = 1.0f
                    translationX = 0.0f
                    translationY = composableStartOffset.y.plus(state.dragVerticalOffset)
                }
            ) {
                PlanComposable(
                    title = state.dataToDrop.title.value,
                    description = state.dataToDrop.description.value,
                    color = state.dataToDrop.color.value,
                    start = state.dataToDrop.start.value.toLocalTime()
                        .plusMinutes(5 * state.timeChangeInIncrementsOfFiveMinutes.toLong()),
                    end = state.dataToDrop.end.value.toLocalTime()
                        .plusMinutes(5 * state.timeChangeInIncrementsOfFiveMinutes.toLong()),
                    modifier = state.draggableModifier
                )
            }
        } else if (state.dragStartedFromTaskSheet) {
            //LOL put all the code inside the graphics layer to guarantee layout coordinates isn't null
            Box(modifier = Modifier.graphicsLayer {
                val halfComposableHeightOffset = Offset(
                    x = 0f, y = state.dataToDrop.duration.intValue
                        .div(2)
                        .times(state.minuteVerticalOffset)
                )
                composableStartOffset = layoutCoordinates?.windowToLocal(state.windowPointerOffset)
                    ?.minus(halfComposableHeightOffset) ?: Offset.Zero
                state.dragStartedFromTaskSheet = false
                state.isDragging = true
            })
        } else if (state.dragStartedFromCalendar) {
            //LOL put all the code inside the graphics layer to guarantee layout coordinates isn't null
            Box(modifier = Modifier.graphicsLayer {
                composableStartOffset = layoutCoordinates!!.windowToLocal(state.topOfDraggableOffset)
                state.dragStartedFromCalendar = false
                state.isDragging = true
            })
        }
    }
}

@Composable
fun TaskRowDragTarget(
    dataToDrop: ScheduledTask,
    //TODO: do we really need to pass modifier around like this?
    modifier: Modifier = Modifier,
    draggableHeight: Dp,
    closeTaskSheet: (() -> Unit) = {},
    content: @Composable () -> Unit
) {
    val state = LocalDragTargetInfo.current
    var currentData by remember { mutableStateOf(dataToDrop) }
    var planComposableHeight by remember { mutableStateOf(0.dp) }
    var planComposableModifier: Modifier by remember { mutableStateOf(Modifier) }
    var layoutCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }

    currentData = dataToDrop
    planComposableHeight = draggableHeight
    planComposableModifier = Modifier
        .height(planComposableHeight)
        .fillMaxWidth()
        .background(Color.Transparent)
        .border(1.dp, Color.Blue)

    Box(modifier = modifier
        .onGloballyPositioned {
            layoutCoordinates = it
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    closeTaskSheet()
                    state.dataToDrop = currentData
                    state.draggableModifier = planComposableModifier
//                    val amountScrolledInMinutes =
//                        (state.calendarScrollState?.value?.toDp())?.div(dpPerMinute)

                    state.windowPointerOffset = layoutCoordinates!!.localToWindow(it)

//                    val startTime = composableScheduleOffset.toLocalTime(state.minuteVerticalOffset)
//                    state.dataToDrop.start.value = ZonedDateTime.of(
//                        state.dataToDrop.start.value.toLocalDate(),
//                        startTime,
//                        ZoneId.systemDefault()
//                    )

//                    state.composableStartOffset =
//                        layoutCoordinates
//                            ?.localToWindow(it)
//                            ?.minus(
//                                Offset(
//                                    x = 0f,
//                                    y = currentData.duration.intValue
//                                        .div(2)
//                                        .times(state.minuteVerticalOffset)
//                                )
//                            ) ?: Offset.Zero
                    state.dragStartedFromTaskSheet = true
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    state.dragVerticalOffset += dragAmount.y
                }, onDragEnd = {
                    state.isDragging = false
                    state.dragStartedFromTaskSheet = false
                    state.dragVerticalOffset = 0f
                    //logic to update event time
                }, onDragCancel = {
                    state.isDragging = false
                    state.dragStartedFromTaskSheet = false
                    state.dragVerticalOffset = 0f
                })
        }
    ) {
        content()
    }
}

//@Composable
//fun TimeDropTarget(
//    index: Int,
//    selectedDate: LocalDate,
//    timeSlot: LocalTime,
//    addScheduledTask: (ScheduledTask) -> Unit,
//    updateScheduledTaskTime: (ScheduledTask, ZonedDateTime) -> Unit,
//    updateEventDaoTime: (EventDao, ZonedDateTime) -> Unit,
//    modifier: Modifier,
//    content: @Composable() (BoxScope.(isInBound: Boolean) -> Unit)
//) {
//    val dragInfo = LocalDragTargetInfo.current
//    val topOfDraggable = dragInfo.topOfDraggable
//
//    Box(modifier = modifier.onGloballyPositioned {
//        it.boundsInWindow().let { rect ->
//            if (dragInfo.isDragging && rect.contains(topOfDraggable)) {
////                dragInfo.dataToDrop.start.value = timeSlot
//            }
//        }
//    }) {
//        //user dropped payload in this drop target
//        if (dragInfo.currentDropTarget == index && !dragInfo.isDragging) {
//            if (dragInfo.isRescheduling) {
//                if (dragInfo.dataToDrop is ScheduledTask) {
//                    updateScheduledTaskTime(
//                        dragInfo.dataToDrop as ScheduledTask, ZonedDateTime.of(
//                        LocalDateTime.of(selectedDate, timeSlot),
//                        ZoneId.systemDefault()
//                    ))
//                } else {
//                    updateEventDaoTime(dragInfo.dataToDrop as EventDao, ZonedDateTime.of(
//                        LocalDateTime.of(selectedDate, timeSlot),
//                        ZoneId.systemDefault()
//                    ))
//                }
//            } else {
//                val scheduledTask = dragInfo.dataToDrop as ScheduledTask
//                scheduledTask.start.value = ZonedDateTime.of(
//                    LocalDateTime.of(selectedDate, timeSlot),
//                    ZoneId.systemDefault()
//                )
//                scheduledTask.end.value = scheduledTask.start.value.plusMinutes(scheduledTask.duration.intValue.toLong())
//                addScheduledTask(scheduledTask)
//            }
//
//            //after updating viewmodel, reset currentDropTarget to prevent repeated calls to viewmodel
//            dragInfo.currentDropTarget = -1
//        }
//
//        content(
//            dragInfo.isDragging && dragInfo.currentDropTarget == index,
//        )
//    }
//}

@Composable
fun CancelDropTarget(
    highlightBottomBar: () -> Unit,
    content: @Composable() (BoxScope.() -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
//    val topOfDraggable = dragInfo.topOfDraggable

    Box(
        modifier = Modifier.onGloballyPositioned {
            it.boundsInWindow().let { rect ->
//                if (dragInfo.isDragging && rect.contains(topOfDraggable)) {
//                    highlightBottomBar()
//                }
            }
        }
    ) {
        content()
    }
}

fun Offset.toLocalTime(minuteVerticalOffset: Float): LocalTime {
    return LocalTime.MIN.plusMinutes(this.y.div(minuteVerticalOffset).toLong())
}