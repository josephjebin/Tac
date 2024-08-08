package com.jebkit.tac.ui.dragAndDrop

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jebkit.tac.constants.Constants.Companion.dpPerMinute
import com.jebkit.tac.data.calendar.Plan
import com.jebkit.tac.data.calendar.ScheduledTask
import com.jebkit.tac.ui.calendar.PlanComposable
import com.jebkit.tac.ui.theme.akiflow_lavender
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.pow
import kotlin.math.roundToInt

internal class DragTargetInfo {
    var verticalOffsetPerMinute by mutableFloatStateOf(0f)
    var verticalOffsetPerFiveMinutes by mutableFloatStateOf(0f)

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

    //used to avoid recalculating since drag targets already calculate this
    var composableHeight by mutableStateOf(0.dp)

    var dragStartedFromTaskSheet by mutableStateOf(false)
    var dragStartedFromCalendar by mutableStateOf(false)
    var isDragging: Boolean by mutableStateOf(false)

    //TODO: used for determining if user's pointer is in the cancel region when dragging
    var windowPointerOffset by mutableStateOf(Offset.Zero)
    var dragCancelBoundaryOffset by mutableFloatStateOf(0f)
    var draggingInsideCancelBoundary by mutableStateOf(false)

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

    var calendarScrollState: ScrollState? by mutableStateOf(null)
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun RootDragInfoProvider(
    verticalOffsetPerMinute: Float,
    calendarScrollState: ScrollState,
    updateIsDragging: (Boolean) -> Unit,
    updateIsDraggingInsideCancelRegion: (Boolean) -> Unit,
    content: @Composable() (BoxScope.() -> Unit)
) {
    val state = remember { DragTargetInfo() }
    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        state.verticalOffsetPerMinute = verticalOffsetPerMinute
        state.verticalOffsetPerFiveMinutes = 5 * verticalOffsetPerMinute
        state.calendarScrollState = calendarScrollState

        updateIsDragging(state.isDragging)
        updateIsDraggingInsideCancelRegion(state.draggingInsideCancelBoundary)

        Box(
            modifier = Modifier.onPlaced {
                val bottomOfScreen = it.boundsInWindow().bottom
                val dpPerOffset = dpPerMinute.div(state.verticalOffsetPerMinute)
                //96.dp because both app bar and tasks sheet peek height are 48.dp
                val offsetPerCancelArea = dpPerOffset.div(96.dp).pow(-1)
                state.dragCancelBoundaryOffset = bottomOfScreen.minus(offsetPerCancelArea)
            }
        )
        {
            content()
        }
    }
}

//TODO - combine drag targets
@Composable
fun CalendarDragTarget(
    modifier: Modifier,
    dataToDrop: Plan,
    draggableHeight: Dp,
    onTaskDrag: (() -> Unit) = {},
) {
    val state = LocalDragTargetInfo.current
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var currentData by remember { mutableStateOf(dataToDrop) }
    var composableHeight by remember { mutableStateOf(draggableHeight) }
    var planComposableModifier: Modifier by remember { mutableStateOf(modifier) }
    var layoutCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
    //made this whole variable just to trigger alpha changes for this single plan composable
    var thisTargetIsBeingDragged by remember { mutableStateOf(false) }

    currentData = dataToDrop
    composableHeight = draggableHeight
    planComposableModifier = modifier
//    planComposableHeight = draggableHeight
//    planComposableModifier = modifier
//        .height(planComposableHeight)
//        .fillMaxWidth()
//        .background(Color.Transparent)

    Box(modifier = planComposableModifier
        .onPlaced {
            layoutCoordinates = it
            currentPosition = it.positionInWindow()
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    onTaskDrag()
                    thisTargetIsBeingDragged = true
                    state.dataToDrop = currentData
                    state.topOfDraggableOffset = currentPosition
                    state.windowPointerOffset = layoutCoordinates!!.localToWindow(it)
                    state.composableHeight = composableHeight
                    state.dragStartedFromCalendar = true
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    state.dragVerticalOffset += dragAmount.y
                },
                onDragEnd = {
                    state.isDragging = false
                    state.dragStartedFromCalendar = false
                    thisTargetIsBeingDragged = false
                    state.dragVerticalOffset = 0f
                    //if pointer was not in cancel area
                        // logic to update event time in viewmodel
                        // logic to update event time in google
                },
                onDragCancel = {
                    state.isDragging = false
                    state.dragStartedFromCalendar = false
                    thisTargetIsBeingDragged = false
                    state.dragVerticalOffset = 0f
                }
            )
        }
    ) {
        PlanComposable(
            title = dataToDrop.title.value,
            description = dataToDrop.description.value,
            color = dataToDrop.color.value,
            start = dataToDrop.start.value.toLocalTime(),
            end = dataToDrop.end.value.toLocalTime(),
            modifier = planComposableModifier
                .padding(2.dp)
                .alpha(if(thisTargetIsBeingDragged) .4f else 1f)
        )
    }
}

@Composable
fun Draggable() {
    val state = LocalDragTargetInfo.current
    var bounds: Rect by remember { mutableStateOf(Rect(Offset.Zero, Offset.Zero)) }
    var layoutCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
    //used for spawning the draggable at the right position
    var composableStartOffset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier
        .fillMaxSize()
        .onPlaced {
            layoutCoordinates = it
            bounds = it.boundsInParent()
        }
        .background(Color.Transparent)
    ) {
        val density: Density = LocalDensity.current

        if (state.isDragging) {
            //communicate up to show drag cancel area
            if(state.windowPointerOffset.y + state.dragVerticalOffset >= state.dragCancelBoundaryOffset) {
                //communicate up to highlight drag cancel area
                state.draggingInsideCancelBoundary = true
            } else {
                //communicate up to stop highlighting drag cancel area
                state.draggingInsideCancelBoundary = false
                state.timeChangeInIncrementsOfFiveMinutes =
                    (state.dragVerticalOffset / state.verticalOffsetPerFiveMinutes).roundToInt()
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
                        modifier = Modifier
                            .height(state.composableHeight)
                            .fillMaxWidth()
                    )
                }
            }
        } else if (state.dragStartedFromTaskSheet) {
            //LOL put all the code inside this modifier to guarantee layout coordinates aren't null
            Box(modifier = Modifier.onPlaced {
                val halfComposableHeightOffset = Offset(
                    x = 0f, y = state.dataToDrop.duration.intValue
                        .div(2)
                        .times(state.verticalOffsetPerMinute)
                )
                composableStartOffset = layoutCoordinates!!.windowToLocal(state.windowPointerOffset)
                    .minus(halfComposableHeightOffset)
                Log.e(
                    "DND",
                    "root offset: ${layoutCoordinates!!.localToRoot(composableStartOffset)}"
                )
                //need to calculate minutes, so that we can spawn the composable at an increment of 5 mins
                //float because scroll might not result in a flat minute
                var minutesUntilTopOfComposable: Float = composableStartOffset.toMinutes(
                    minuteVerticalOffset = state.verticalOffsetPerMinute,
                    with(density) { state.calendarScrollState!!.value.toDp() }
                )

                //TODO: sometimes, coercing the time doesn't work, so this needs to be improved and tested
                //e.g. 62.5 mins til top of composable --> we'll shift the composable down 2.5 mins
                //(by adding 2.5 mins) to spawn the composable at 65 mins
                //e.g. 50.00 mins --> perfectly divisible by 5, so no subtracting necessary
                //in this case, minutes to subtract would be 0
                if (minutesUntilTopOfComposable.roundToInt() % 5 < 2) {
                    val minutesToSubtract = minutesUntilTopOfComposable.mod(5f)
                    minutesUntilTopOfComposable -= minutesToSubtract
                    val offsetToSubtract = minutesToSubtract.times(state.verticalOffsetPerMinute)
                    composableStartOffset -= Offset(x = 0f, y = offsetToSubtract)
                } else {
                    val minutesToAdd = minutesUntilTopOfComposable.mod(5f)
                    minutesUntilTopOfComposable += minutesToAdd
                    val offsetToAdd = minutesToAdd.times(state.verticalOffsetPerMinute)
                    composableStartOffset += Offset(x = 0f, y = offsetToAdd)
                }


                state.dataToDrop.start.value = ZonedDateTime.of(
                    state.dataToDrop.start.value.toLocalDate(),
                    LocalTime.MIN.plusMinutes(minutesUntilTopOfComposable.toLong()),
                    ZoneId.systemDefault()
                )

                state.dataToDrop.end.value =
                    state.dataToDrop.start.value.plusMinutes(state.dataToDrop.duration.intValue.toLong())

                state.dragStartedFromTaskSheet = false
                state.isDragging = true
            })
        } else if (state.dragStartedFromCalendar) {
            //LOL put all the code inside this modifier to guarantee layout coordinates aren't null
            Box(modifier = Modifier.onPlaced {
                composableStartOffset =
                    layoutCoordinates!!.windowToLocal(state.topOfDraggableOffset)
                state.dragStartedFromCalendar = false
                state.isDragging = true
            })
        }
    }
}

@Composable
fun TaskRowDragTarget(
    dataToDrop: ScheduledTask,
    draggableHeight: Dp,
    closeTaskSheet: (() -> Unit) = {},
    content: @Composable () -> Unit
) {
    val state = LocalDragTargetInfo.current
    var currentData by remember { mutableStateOf(dataToDrop) }
    var composableHeight by remember { mutableStateOf(0.dp) }
//    var planComposableModifier: Modifier by remember { mutableStateOf(Modifier) }
    var layoutCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }

    currentData = dataToDrop
    composableHeight = draggableHeight
//    planComposableModifier = Modifier
//        .height(planComposableHeight)
//        .fillMaxWidth()
//        .background(Color.Transparent)

    Box(modifier = Modifier
        .onPlaced {
            layoutCoordinates = it
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    closeTaskSheet()
                    state.dataToDrop = currentData
//                    state.draggableModifier = planComposableModifier
                    state.windowPointerOffset = layoutCoordinates!!.localToWindow(it)
                    state.composableHeight = composableHeight
                    state.dragStartedFromTaskSheet = true
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    state.dragVerticalOffset += dragAmount.y
                }, onDragEnd = {
                    state.isDragging = false
                    state.dragStartedFromTaskSheet = false
                    //if pointer was not in cancel area
                        //logic to add scheduled task
                    state.dragVerticalOffset = 0f
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

fun Offset.toMinutes(minuteVerticalOffset: Float, dpScrolled: Dp): Float {
    val minutesScrolled = dpScrolled.div(dpPerMinute)
    //this.y is the offset from the top of the schedule (even after scrolling) to the composable
    //offset / (offset / min) --> offset * (min / offset) --> offsets cancel, resulting in minutes
    return this.y.div(minuteVerticalOffset).plus(minutesScrolled)
}