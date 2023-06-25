package com.example.tac.ui.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.api.client.util.DateTime
import java.time.LocalDate
import java.util.*

@Composable
fun DayHeader(selectedDate: DateTime) {
    Row() {

        val dateTime = GregorianCalendar(TimeZone.getTimeZone("GMT"))
        val localTime = selectedDate.value + selectedDate.timeZoneShift * 60000L
        dateTime.timeInMillis = localTime
        val localDate = LocalDate
        Text(text = "${selectedDate.dayOfWeek}")

        Spacer(modifier = Modifier.width(24.dp))

        Text(text = "${selectedDate.month}")

        Spacer(modifier = Modifier.width(24.dp))

        Text(text = "${selectedDate.dayOfMonth}")
    }
}