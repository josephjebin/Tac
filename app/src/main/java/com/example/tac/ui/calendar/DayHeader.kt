package com.example.tac.ui.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import java.time.LocalDate

@Composable
fun DayHeader(selectedDate: LocalDate) {
    Row() {
        Text(text = "${selectedDate.dayOfWeek}")

        Text(text = "${selectedDate.month}")

        Text(text = "${selectedDate.dayOfMonth}")
    }
}