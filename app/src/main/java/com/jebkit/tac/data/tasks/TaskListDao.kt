package com.jebkit.tac.data.tasks

import androidx.compose.runtime.MutableState

//have to use custom model for google models to make attributes observable
data class TaskListDao(
    var id: String,
    var title: MutableState<String>
)