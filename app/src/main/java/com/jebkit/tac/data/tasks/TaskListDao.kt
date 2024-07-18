package com.jebkit.tac.data.tasks

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

//have to use custom model for google models to make attributes observable
data class TaskListDao(
    var id: MutableState<String>,
    var title: MutableState<String>
)