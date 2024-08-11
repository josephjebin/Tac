package com.jebkit.tac.ui.fab

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jebkit.tac.R

@Composable
fun AddEventAndTaskFab(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.google_plus_sign),
            contentDescription = "Add event or task",
            modifier = Modifier
                .clip(CircleShape)
                .padding(0.dp)
                .background(
                    color = colorResource(id = R.color.google_divider_gray),
                    shape = CircleShape
                )
                .clickable { }
        )
    }
}

@Composable
fun DialogButtonStack(
    onDismissRequest: () -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismissRequest() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(4.dp, Color.Green),
            horizontalArrangement = Arrangement.End
        ) {
            //tasks button at bottom
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.google_task_button),
                    contentDescription = "Add task",
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(0.dp)
                        .clickable { }
                )
            }

            Column {
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { /*TODO*/ }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.round_calendar_today_24),
                        contentDescription = "Add event"
                    )
                }

                Spacer(Modifier.size(56.dp))
            }
        }
    }
}

@Preview
@Composable
fun AddButton() {
    AddEventAndTaskFab()
}

@Preview
@Composable
fun DialogButtonStackPreview() {
    DialogButtonStack(
        onDismissRequest = {}
    )
}