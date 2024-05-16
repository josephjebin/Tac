package com.example.tac

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tac.data.constants.Constants
import com.example.tac.data.tasks.TaskDao
import com.example.tac.data.tasks.TaskList
import com.example.tac.ui.calendar.Calendar
import com.example.tac.ui.calendar.CalendarViewModel
import com.example.tac.ui.task.TaskSheet
import com.example.tac.ui.task.TasksSheetState
import com.example.tac.ui.task.TasksViewModel
import com.example.tac.ui.theme.TacTheme
import com.example.tac.ui.theme.accent_gray
import com.example.tac.ui.theme.primaryGray
import kotlinx.coroutines.*
import net.openid.appauth.*
import net.openid.appauth.browser.BrowserAllowList
import net.openid.appauth.browser.VersionedBrowserMatcher
import org.json.JSONException
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    val TAG = "Tac"
    private var authState: AuthState = AuthState()
    private lateinit var authorizationService: AuthorizationService
    lateinit var authServiceConfig: AuthorizationServiceConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        initAuthServiceConfig()
//        initAuthService()
//        if (!restoreState()) {
//            attemptAuthorization()
//        }

        setContent {
            TacTheme {
                Tac()
            }
        }
    }

//    fun persistState() {
//        application.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
//            .edit()
//            .putString(Constants.AUTH_STATE, authState.jsonSerializeString())
//            .apply()
//    }

//    fun restoreState(): Boolean {
//        val jsonString = application
//            .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
//            .getString(Constants.AUTH_STATE, null)
//
//        if (jsonString != null && !TextUtils.isEmpty(jsonString)) {
//            try {
//                authState = AuthState.jsonDeserialize(jsonString)
//                return !authState.hasClientSecretExpired()
//            } catch (jsonException: JSONException) {
//            }
//        }
//
//        return false
//    }

//    private fun initAuthServiceConfig() {
//        authServiceConfig = AuthorizationServiceConfiguration(
//            Uri.parse(Constants.URL_AUTHORIZATION),
//            Uri.parse(Constants.URL_TOKEN_EXCHANGE),
//            null,
//            Uri.parse(Constants.URL_LOGOUT)
//        )
//    }

//    private fun initAuthService() {
//        val appAuthConfiguration = AppAuthConfiguration.Builder()
//            .setBrowserMatcher(
//                BrowserAllowList(
//                    VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
//                    VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB
//                )
//            ).build()
//
//        authorizationService = AuthorizationService(
//            application,
//            appAuthConfiguration
//        )
//    }

//    fun attemptAuthorization() {
//        val request = AuthorizationRequest.Builder(
//            authServiceConfig,
//            Constants.CLIENT_ID,
//            ResponseTypeValues.CODE,
//            Uri.parse(Constants.URL_AUTH_REDIRECT))
//            .setScopes(Constants.SCOPE_CALENDAR, Constants.SCOPE_TASKS).build()
//
//        val authIntent = authorizationService.getAuthorizationRequestIntent(request)
//
//        Log.i(TAG, "Trying to get auth code")
//
//        val authorizationLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                run {
//                    Log.i(TAG, "Result code from activity result: ${result.resultCode}")
//                    if (result.resultCode == Activity.RESULT_OK) {
//                        Log.i(TAG, "Trying to handle auth response: ${result.data}")
//                        handleAuthorizationResponse(result.data!!)
//                    }
//                }
//            }
//
//        authorizationLauncher.launch(authIntent)
//    }

//    private fun handleAuthorizationResponse(intent: Intent) {
//        val authorizationResponse: AuthorizationResponse? = AuthorizationResponse.fromIntent(intent)
//        val error = AuthorizationException.fromIntent(intent)
//
//        authState = AuthState(authorizationResponse, error)
//
//        val tokenExchangeRequest = authorizationResponse?.createTokenExchangeRequest()
//        if (tokenExchangeRequest != null) {
//            authorizationService.performTokenRequest(tokenExchangeRequest) { response, exception ->
//                if (exception != null) {
//                    authState = AuthState()
//                } else {
//                    if (response != null) {
//                        authState.update(response, exception)
//                    }
//                }
//                persistState()
//            }
//        }
//    }
}

@Composable
fun Tac() {
    Surface(color = MaterialTheme.colors.background) {
        TasksAndCalendarScreen()
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TasksAndCalendarScreen(
    tasksViewModel: TasksViewModel = TasksViewModel(),
    calendarViewModel: CalendarViewModel = CalendarViewModel()
) {
    val uiTasksState by tasksViewModel.uiState.collectAsState()
    val uiCalendarState by calendarViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val swipeableState = rememberSwipeableState(initialValue = TasksSheetState.PARTIALLY_EXPANDED)

    Box {
        //calendar padding is used to offset the bottom of the calendar dependent on the tasks sheet's size
        val calendarPadding = when(swipeableState.currentValue) {
            TasksSheetState.COLLAPSED -> 64.dp
            else -> 296.dp
        }
        Calendar(Modifier.padding(PaddingValues(bottom = calendarPadding)), uiCalendarState)
        MyBottomSheet(swipeableState = swipeableState) {
            TaskSheet(
                uiTasksState.taskLists,
                uiTasksState.tasks,
                uiTasksState.currentSelectedTaskList,
                onTaskListSelected = { taskList: TaskList ->
                    tasksViewModel.updateCurrentSelectedTaskList(taskList)
                },
                onTaskSelected = { taskDao: TaskDao ->
                    tasksViewModel.updateCurrentSelectedTask(taskDao)
                },
                onTaskCompleted = { taskDao: TaskDao ->
                }
            )
        }

        MyBottomBar(
            tasksSheetState = swipeableState.currentValue,
            updateTasksSheetState = { newTasksSheetState: TasksSheetState ->
                coroutineScope.launch {
                    swipeableState.animateTo(newTasksSheetState)
                }
            }
        )

//        MyFAB(modifier = Modifier
//            .align(Alignment.TopEnd)
//            .padding(top = 40.dp, end = 16.dp),
//            onClick = {
//                tasksViewModel.getTaskListsAndTasks()
//                calendarViewModel.initCalendarsAndEvents()
//            }
//        )
    }
}
//
//@Composable
//fun MyFAB(modifier: Modifier, onClick: () -> Unit) {
//    FloatingActionButton(
//        modifier = modifier,
//        onClick = onClick,
//        content = {
//            Icon(
//                painter = painterResource(id = R.drawable.round_refresh_24),
//                contentDescription = "Refresh"
//            )
//        }
//    )
//}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyBottomSheet(
    swipeableState: SwipeableState<TasksSheetState>,
    body: @Composable () -> Unit
) {
    BoxWithConstraints  {
        //logic to obtain screen size
        val constraintsScope = this
        val maxHeight = with(LocalDensity.current) {
            constraintsScope.maxHeight.toPx()
        }

        val columnModifier = when (swipeableState.currentValue) {
            TasksSheetState.EXPANDED -> Modifier
                .fillMaxHeight()
                .padding(PaddingValues(bottom = 56.dp))
            TasksSheetState.PARTIALLY_EXPANDED -> Modifier
                .height(320.dp)
                .padding(PaddingValues(bottom = 70.dp))
            else -> Modifier.height(0.dp)
        }

        Column(columnModifier) {
            Box(
                Modifier
                    .offset {
                        IntOffset(
                            x = 0,
                            y = swipeableState.offset.value.roundToInt()
                        )
                    }
                    .swipeable(
                        state = swipeableState,
                        orientation = Orientation.Vertical,
                        anchors = mapOf(
                            0f to TasksSheetState.EXPANDED,
                            1300f to TasksSheetState.PARTIALLY_EXPANDED,
                            maxHeight to TasksSheetState.COLLAPSED,
                        )
                    )
            ) {
                body()
            }
        }
    }
}

@Composable
fun MyBottomBar(
    tasksSheetState: TasksSheetState,
    updateTasksSheetState: (TasksSheetState) -> Unit
) {
    Box(modifier = Modifier.fillMaxHeight()) {
        BottomAppBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(accent_gray)
        ) {
            //calendar button
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    updateTasksSheetState(TasksSheetState.COLLAPSED)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_calendar_today_24),
                    contentDescription = "Calendar button"
                )
            }

            val tasksButtonModifier: Modifier = when (tasksSheetState) {
                TasksSheetState.COLLAPSED -> {
                    Modifier
                        .weight(1f)
                        .background(primaryGray)
                }
                TasksSheetState.PARTIALLY_EXPANDED -> {
                    Modifier
                        .weight(1f)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    primaryGray,
                                    accent_gray
                                )
                            )
                        )
                }
                else -> {
                    Modifier
                        .weight(1f)
                        .background(accent_gray)
                }
            }

            //tasks button
            IconButton(modifier = tasksButtonModifier,
                onClick = {
                    when (tasksSheetState) {
                        TasksSheetState.COLLAPSED -> updateTasksSheetState(TasksSheetState.PARTIALLY_EXPANDED)
                        TasksSheetState.PARTIALLY_EXPANDED -> updateTasksSheetState(TasksSheetState.EXPANDED)
                        else -> updateTasksSheetState(TasksSheetState.PARTIALLY_EXPANDED)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_task_24),
                    contentDescription = "Tasks button"
                )
            }
        }
    }

}


//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun TasksAndCalendarScreen(
//    tasksViewModel: TasksViewModel = viewModel(factory = TasksViewModel.Factory),
//    calendarViewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
//) {
//    val uiTasksState by tasksViewModel.uiState.collectAsState()
//    val uiCalendarState by calendarViewModel.uiState.collectAsState()
//
//    Box {
//        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
//            bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
//        )
//        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
//        var sheetPeekHeight by remember { mutableStateOf(0.dp) }
//
//
//        val taskSheetModifier = when (tasksButtonState) {
//            0 -> Modifier.height(0.dp)
//            else -> Modifier.wrapContentHeight()
//        }
//        BottomSheetScaffold(
//            modifier = Modifier.requiredHeightIn(max = screenHeight),
//            sheetContent = {
//                TaskSheet(
//                    taskSheetModifier = taskSheetModifier,
//                    uiTasksState.taskLists,
//                    uiTasksState.tasks,
//                    uiTasksState.currentSelectedTaskList,
//                    onTaskListSelected = { taskList: TaskList ->
//                        tasksViewModel.updateCurrentSelectedTaskList(taskList)
//                    },
//                    onTaskSelected = { taskDao: TaskDao ->
//                        tasksViewModel.updateCurrentSelectedTask(taskDao)
//                    },
//                    onTaskCompleted = { taskDao: TaskDao ->
//                    }
//                )
//            },
//            scaffoldState = bottomSheetScaffoldState,
//            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
//            sheetPeekHeight = sheetPeekHeight
//        ) {
//            Box(
//                modifier = Modifier
//                    .padding()
//                    .requiredHeightIn(max = LocalConfiguration.current.screenHeightDp.dp)
//            ) {
//                Calendar(
//                    tasksViewModel,
//                    calendarViewModel
//                )
//            }
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TacTheme() {
    }
}