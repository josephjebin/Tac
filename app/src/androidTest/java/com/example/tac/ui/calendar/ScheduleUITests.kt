package com.example.tac.ui.calendar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.unit.dp
import com.example.tac.data.calendar.ScheduledTask
import com.example.tac.data.dummyData.dummyEvents
import com.example.tac.data.dummyData.dummyScheduledTasks
import com.example.tac.ui.task.TasksSheetState
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ScheduleUITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockCalendarViewModel: CalendarViewModel = mockk<CalendarViewModel>()

    @Test
    fun sanity() {
        composeTestRule.setContent {
            Schedule(
                selectedDate = LocalDate.now(),
                events = dummyEvents(),
                scheduledTasks = dummyScheduledTasks(),
                hourHeight = 64.dp,
                tasksSheetState = TasksSheetState.COLLAPSED,
                addScheduledTask = { newScheduledTask: ScheduledTask -> mockCalendarViewModel.addScheduledTask(newScheduledTask) },
                removeScheduledTask = { scheduledTask: ScheduledTask -> mockCalendarViewModel.removeScheduledTask(scheduledTask) },
                addEventDao = {},
                removeEventDao = {}
            )
        }

        composeTestRule.onNodeWithText("Work").assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun schedule_draggingScheduledTask_updatesScheduledTask() {
        composeTestRule.setContent {
            Schedule(
                selectedDate = LocalDate.now(),
                events = dummyEvents(),
                scheduledTasks = dummyScheduledTasks(),
                hourHeight = 64.dp,
                tasksSheetState = TasksSheetState.COLLAPSED,
                addScheduledTask = { newScheduledTask: ScheduledTask -> mockCalendarViewModel.addScheduledTask(newScheduledTask) },
                removeScheduledTask = { scheduledTask: ScheduledTask -> mockCalendarViewModel.removeScheduledTask(scheduledTask) },
                addEventDao = {},
                removeEventDao = {}
            )
        }

        val scheduledTask = dummyScheduledTasks()[0]
        verify(exactly = 0) { mockCalendarViewModel.removeScheduledTask(scheduledTask) }
        verify(exactly = 0) { mockCalendarViewModel.addScheduledTask(scheduledTask) }

        composeTestRule.onNodeWithText("Work").performMouseInput {
            click(center)
            moveBy(Offset(x = 0f, y = 64.dp.toPx()))
            release()
        }

        verify { mockCalendarViewModel.removeScheduledTask(scheduledTask) }
        scheduledTask.start.value = scheduledTask.start.value.minusHours(1)
        scheduledTask.end.value = scheduledTask.start.value.minusHours(1)

        verify { mockCalendarViewModel.addScheduledTask(scheduledTask) }
    }

}