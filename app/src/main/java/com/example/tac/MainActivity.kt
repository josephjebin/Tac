package com.example.tac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tac.data.tasks.TaskDao
import com.example.tac.data.tasks.TaskList
import com.example.tac.ui.calendar.Calendar
import com.example.tac.ui.calendar.CalendarViewModel
import com.example.tac.ui.calendar.DayHeader
import com.example.tac.ui.dragAndDrop.LongPressDraggable
import com.example.tac.ui.task.TaskSheet
import com.example.tac.ui.task.TasksSheetState
import com.example.tac.ui.task.TasksSheetState.*
import com.example.tac.ui.task.TasksViewModel
import com.example.tac.ui.theme.TacTheme
import com.example.tac.ui.theme.accent_gray
import net.openid.appauth.*

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


@Composable
fun TasksAndCalendarScreen(
    calendarViewModel: CalendarViewModel = CalendarViewModel(),
    tasksViewModel: TasksViewModel = TasksViewModel()
) {
    val uiCalendarState by calendarViewModel.uiState.collectAsState()
    val uiTasksState by tasksViewModel.uiState.collectAsState()
//    val coroutineScope = rememberCoroutineScope()
//    val configuration = LocalConfiguration.current
//    val screenHeight = configuration.screenHeightDp.dp
//    val screenWidth = configuration.screenWidthDp.dp
    val tasksSheetState = remember { mutableStateOf(COLLAPSED) }


    Scaffold(
        topBar = { DayHeader(uiCalendarState.selectedDate) },
        bottomBar = { MyBottomBar(tasksSheetState = tasksSheetState) }
    ) {
        LongPressDraggable {
            Column(
                modifier = Modifier.padding(it),
                verticalArrangement = Arrangement.Bottom
            ) {
                val taskSheetModifier = when (tasksSheetState.value) {
                    COLLAPSED -> {
                        Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                    }

                    PARTIALLY_EXPANDED -> {
                        Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    }

                    EXPANDED -> {
                        Modifier
                            .fillMaxSize()
                    }
                }

                //CALENDAR
                if (tasksSheetState.value != EXPANDED) {
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()
                    ) {

                        Calendar(
                            uiCalendarState,
//                            updateSidebarWidth = { newWidth: Dp -> calendarViewModel.updateSidebarWidth(newWidth) },
//                            updateScheduleWidth = { newWidth: Dp -> calendarViewModel.updateScheduleWidth(newWidth) }
                        )


                    }
                }

                //TASKS SHEET
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
                    },
                    modifier = taskSheetModifier
                )
            }
        }

    }
}

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

@Composable
fun MyBottomBar(
    tasksSheetState: MutableState<TasksSheetState>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(accent_gray)
    ) {
        //CALENDAR BUTTON
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1.0f)
                .height(48.dp)
                .border(1.dp, Color.Black)
                .clickable {
                    tasksSheetState.value = COLLAPSED
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_calendar_today_24),
                contentDescription = "Calendar button"
            )
        }

        //TO-DO LIST BUTTON
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1.0f)
                .height(48.dp)
                .border(1.dp, Color.Black)
                .clickable {
                    if (tasksSheetState.value == COLLAPSED || tasksSheetState.value == PARTIALLY_EXPANDED)
                        tasksSheetState.value = EXPANDED
                    else
                        tasksSheetState.value = PARTIALLY_EXPANDED
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_task_24),
                contentDescription = "Tasks button"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TacTheme() {
        Tac()
    }
}