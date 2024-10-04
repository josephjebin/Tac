package com.jebkit.tac

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.jebkit.tac.constants.GoogleAuthConstants
import com.jebkit.tac.constants.GoogleAuthConstants.PREF_ACCOUNT_NAME
import com.jebkit.tac.constants.GoogleAuthConstants.REQUEST_GOOGLE_PLAY_SERVICES
import com.jebkit.tac.ui.tasksAndCalendar.TasksAndCalendarViewModel
import com.jebkit.tac.ui.calendar.CalendarViewModelFactory
import com.jebkit.tac.ui.googleAuth.GoogleAuthViewModel
import com.jebkit.tac.ui.googleAuth.GoogleAuthViewModelFactory
import com.jebkit.tac.ui.layout.Tac
import com.jebkit.tac.ui.tasks.TasksSheetState
import com.jebkit.tac.ui.tasks.TasksSheetState.*
import com.jebkit.tac.ui.theme.TacTheme
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.tasks.TasksScopes
import pub.devrel.easypermissions.EasyPermissions

//import net.openid.appauth.*

class MainActivity : ComponentActivity() {
    val TAG = "Tac"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init the Google Auth View Model
        val googleAuthViewModelFactory = GoogleAuthViewModelFactory(initCredentials(this))
        val googleAuthViewModel =
            ViewModelProvider(this, googleAuthViewModelFactory)[GoogleAuthViewModel::class.java]

        //init the Google sign in launcher that launches the Google sign in activity
        val googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK && it.data!!.extras != null) {
                val accountName =
                    it.data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    val settings = this.getPreferences(Context.MODE_PRIVATE)
                    val editor = settings?.edit()
                    editor?.putString(PREF_ACCOUNT_NAME, accountName)
                    editor?.apply()
                    googleAuthViewModel.setAccountName(accountName)
                }
            }
        }

        //launcher used when sign in process is recoverable i.e. user signing in solves error
        val userRecoverableLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode != Activity.RESULT_OK) {
                //logic to handle unsuccessful login flow
            }
        }

        setContent {
            //TODO: dynamic layouts
            val context = LocalContext.current
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            TacTheme {
                if (googleAuthViewModel.gmail == "user") {
                    Log.i("SIGN IN", "sign in called")
                    SignIn(
                        this,
                        this,
                        googleAuthViewModel,
                        googleSignInLauncher
                    )
                } else {
                    val calendarViewModelFactory =
                        CalendarViewModelFactory(
                            googleAuthViewModel.googleAccountCredential,
                            userRecoverableLauncher
                        )
                    val tasksAndCalendarViewModel = ViewModelProvider(
                        viewModelStore, calendarViewModelFactory
                    )[TasksAndCalendarViewModel::class.java]

                    Log.e(TAG, "scopes: ${googleAuthViewModel.googleAccountCredential.scope}")
                    Tac(tasksAndCalendarViewModel = tasksAndCalendarViewModel)
                }
            }
        }
    }
}


@Composable
private fun SignIn(
    activity: MainActivity,
    context: Context,
    viewModel: GoogleAuthViewModel,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
) {
    //if Google Play isn't installed
    if (GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS
    ) {
        acquireGooglePlayServices(activity)
    }

    var havePermissionToGetAccounts by rememberSaveable {
        mutableStateOf(
            havePermissionToGetAccounts(activity, context)
        )
    }


    if (!havePermissionToGetAccounts) {
        AlertDialog(
            title = { Text(text = "We need to be able to sign in to Google!") },
            text = { Text(text = "How else are we going to get things from your Google Calendar and Tasks?") },
            onDismissRequest = {
                havePermissionToGetAccounts = havePermissionToGetAccounts(activity, context)
            },
            confirmButton = {
                Button(
                    onClick = {
                        havePermissionToGetAccounts =
                            havePermissionToGetAccounts(activity, context)
                    }
                ) {
                    Text(text = "I've given the app permission to sign in to Google.")
                }
            }
        )
    }

    var isNetworkAvailable by rememberSaveable { mutableStateOf(isNetworkAvailable(context)) }
    if (!isNetworkAvailable) {
        AlertDialog(
            title = { Text(text = "Network unavailable.") },
            text = { Text(text = "A network connection is needed to pull your calendars and tasks.") },
            onDismissRequest = { isNetworkAvailable = isNetworkAvailable(context) },
            confirmButton = {
                Button(
                    onClick = { isNetworkAvailable = isNetworkAvailable(context) }
                ) {
                    Text(text = "I've connected to the internet.")
                }
            }
        )
    }

    getGoogleAccountFromSharedPreferences(activity)?.let { viewModel.setAccountName(it) }
    if (viewModel.googleAccountCredential.selectedAccountName == null) {
        SideEffect {
            googleSignInLauncher.launch(viewModel.googleAccountCredential.newChooseAccountIntent())
        }
    }
}

private fun initCredentials(context: Context): GoogleAccountCredential {
    return GoogleAccountCredential.usingOAuth2(
        context,
        arrayListOf(CalendarScopes.CALENDAR, TasksScopes.TASKS)
    ).setBackOff(ExponentialBackOff())
}

private fun acquireGooglePlayServices(
    activity: Activity,
) {
    val apiAvailability = GoogleApiAvailability.getInstance()
    val connectionStatusCode =
        apiAvailability.isGooglePlayServicesAvailable(activity.applicationContext)
    if (apiAvailability.isUserResolvableError(connectionStatusCode))
        showGooglePlayServicesAvailabilityErrorDialog(
            activity,
            connectionStatusCode,
        )
}

fun showGooglePlayServicesAvailabilityErrorDialog(
    activity: Activity,
    connectionStatusCode: Int,
) {
    val apiAvailability = GoogleApiAvailability.getInstance()
    val dialog = apiAvailability.getErrorDialog(
        activity,
        connectionStatusCode,
        REQUEST_GOOGLE_PLAY_SERVICES
    )
    dialog?.show()

}

private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nw = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        //for other device how are able to connect with Ethernet
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        //for check internet over Bluetooth
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}

private fun havePermissionToGetAccounts(activity: MainActivity, context: Context): Boolean {
    var permissions = retrievePermissions(context)
    return if (permissions.contains(GoogleAuthConstants.PERMISSION_GET_ACCOUNTS)) {
        true
    } else {
        EasyPermissions.requestPermissions(
            activity,
            "This app needs to access your Google account (via Contacts).",
            GoogleAuthConstants.REQUEST_PERMISSION_GET_ACCOUNTS,
            GoogleAuthConstants.PERMISSION_GET_ACCOUNTS
        )

        permissions = retrievePermissions(context)
        permissions.contains(GoogleAuthConstants.PERMISSION_GET_ACCOUNTS)
    }
}

private fun getGoogleAccountFromSharedPreferences(activity: MainActivity): String? {
    return activity
        .getPreferences(Context.MODE_PRIVATE)
        ?.getString(PREF_ACCOUNT_NAME, null)
}

fun retrievePermissions(context: Context): Array<String?> {
    val pkgName = context.packageName
    return try {
        context
            .packageManager
            .getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS).requestedPermissions
    } catch (e: PackageManager.NameNotFoundException) {
        arrayOfNulls(0)
    }
}

@Composable
fun MyBottomBar(
    tasksSheetState: MutableState<TasksSheetState>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(colorResource(R.color.surface_dark_gray))
    ) {
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp),
            color = Color.Black
        )

        //CALENDAR BUTTON
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1.0f)
                .height(48.dp)
                .clickable {
                    tasksSheetState.value = COLLAPSED
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_calendar_today_24),
                tint = colorResource(id = R.color.google_text_gray),
                contentDescription = "Calendar button"
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp),
            color = Color.Black
        )

        //TO-DO LIST BUTTON
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1.0f)
                .height(48.dp)
                .clickable {
                    if (tasksSheetState.value == COLLAPSED || tasksSheetState.value == PARTIALLY_EXPANDED)
                        tasksSheetState.value = EXPANDED
                    else
                        tasksSheetState.value = PARTIALLY_EXPANDED
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_task_24),
                tint = colorResource(id = R.color.google_text_gray),
                contentDescription = "Tasks button"
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp),
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyBottomBarPreview() {
    TacTheme() {
        MyBottomBar(
            tasksSheetState = remember { mutableStateOf(COLLAPSED) }
        )
    }
}
