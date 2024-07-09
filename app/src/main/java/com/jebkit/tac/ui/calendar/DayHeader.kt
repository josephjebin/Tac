package com.jebkit.tac.ui.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun DayHeader(selectedDate: LocalDate) {
    Row() {
        Text(text = "${selectedDate.dayOfWeek}")

        Spacer(modifier = Modifier.width(24.dp))

        Text(text = "${selectedDate.month}")

        Spacer(modifier = Modifier.width(24.dp))

        Text(text = "${selectedDate.dayOfMonth}")
    }
}