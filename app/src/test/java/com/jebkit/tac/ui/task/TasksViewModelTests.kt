package com.jebkit.tac.ui.task

import com.jebkit.tac.data.tasks.TaskList
import org.junit.Assert.assertEquals
import org.junit.Test

class TasksViewModelTests {
    private val viewModel = TasksViewModel()

    @Test
    fun tasksViewModel_initialized_firstTaskListMarkedAsCurrentSelectedTaskList() {
        val tasksState = viewModel.uiState.value
        assertEquals("Career", tasksState.currentSelectedTaskList.title)
    }

    @Test
    fun tasksViewModel_updateCurrentSelectedTaskList_updatesCurrentSelectedTaskList() {
        var tasksState = viewModel.uiState.value
        assertEquals("Career", tasksState.currentSelectedTaskList.title)

        viewModel.updateCurrentSelectedTaskList(TaskList(id = "financeTaskListId", title = "Finance"))
        tasksState = viewModel.uiState.value
        assertEquals("Finance", tasksState.currentSelectedTaskList.title)
    }
}