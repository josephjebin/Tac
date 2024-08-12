package com.jebkit.tac.ui.fab

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.IconButton
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
    modifier: Modifier = Modifier,
    triggerDialog: () -> Unit
) {
    Box(modifier = modifier) {
        IconButton(onClick = { triggerDialog() }) {
            Image(
                painter = painterResource(id = R.drawable.google_plus_sign),
                contentDescription = "Add event or task"
            )
        }

//        Image(
//            painter = painterResource(id = R.drawable.google_plus_sign),
//            contentDescription = "",
//            modifier = Modifier
//                .clip(CircleShape)
//                .padding(0.dp)
//                .background(
//                    color = colorResource(id = R.color.google_divider_gray),
//                    shape = CircleShape
//                )
//                .clickable { triggerDialog() }
//        )
    }
}

@Composable
fun DialogButtonStack(
    paddingValues: PaddingValues,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Row(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(bottom = 48.dp, end = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            //tasks button at bottom
            Column(
//                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier
                    .weight(1f)
                    .clickable { onDismissRequest() }
                )

                IconButton(onClick = { /*TODO*/ }) {
                    Image(
                        painter = painterResource(id = R.drawable.google_task_button),
                        contentDescription = "Add task",
                        modifier = Modifier
//                            .clip(CircleShape)
                            .padding(0.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.border(2.dp, Color.Yellow)
            ) {
                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.round_calendar_today_24),
                        contentDescription = "Add event"
                    )
                }

                IconButton(onClick = { onDismissRequest() }) {
                    Image(
                        painter = painterResource(id = R.drawable.google_plus_sign),
                        contentDescription = "exit adding prompt"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AddButton() {
    AddEventAndTaskFab(
        triggerDialog = {}
    )
}

@Preview
@Composable
fun DialogButtonStackPreview() {
    DialogButtonStack(
        paddingValues = PaddingValues(bottom = 96.dp, end = 32.dp),
        onDismissRequest = {}
    )
}