package com.jebkit.tac.ui.fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jebkit.tac.R
import com.jebkit.tac.ui.tasks.TasksSheetState
import com.jebkit.tac.ui.theme.google_divider_gray
import com.jebkit.tac.ui.theme.google_light_blue
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
    flexPadding: Dp,
    onDismissRequest: () -> Unit
) {
    var showAddButtons by remember { mutableStateOf(false) }
    val animatableRotation = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = showAddButtons) {
        if (showAddButtons) {
            coroutineScope.launch { animatableRotation.animateTo(45f) }
        }
    }
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismissRequest() }
                .padding(paddingValues)
                .padding(bottom = flexPadding, end = 16.dp)
        ) {
            Row(
                Modifier.weight(1f)
            ) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDismissRequest() }
                )
            }

            Row {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDismissRequest() }
                )

                AnimatedVisibility(visible = showAddButtons) {
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .background(
                                colorResource(id = R.color.surface_dark_gray),
                                CircleShape
                            )
                            .size(48.dp),

                        ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_calendar_today_24),
                            contentDescription = "Add event",
                            modifier = Modifier
                                .padding(8.dp),
                            tint = google_light_blue
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                }

            }

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier
                    .weight(1f)
                    .clickable { onDismissRequest() }
                )

                AnimatedVisibility(visible = showAddButtons) {
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .background(colorResource(id = R.color.google_button_gray), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google_task_button_2),
                            contentDescription = "Add event",
                            modifier = Modifier
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                IconButton(
                    modifier = Modifier
                        .onPlaced {
                            showAddButtons = true
                        },
                    onClick = { onDismissRequest() },
                ) {
                    Image(
                        painterResource(id = R.drawable.google_plus_sign),
                        contentDescription = "Add task or event",
                        modifier = Modifier
                            .background(google_divider_gray, shape = CircleShape)
                            .size(48.dp)
                            .padding(4.dp)
                            .rotate(animatableRotation.value)
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
        flexPadding = 16.dp,
        onDismissRequest = {}
    )
}