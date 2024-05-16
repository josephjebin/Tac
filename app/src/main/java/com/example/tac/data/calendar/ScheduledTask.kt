package com.example.tac.data.calendar

import com.example.tac.data.tasks.Task

data class ScheduledTask(val parentTask: Task): Plan()
