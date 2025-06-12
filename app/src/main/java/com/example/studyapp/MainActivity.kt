package com.example.studyapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.studyapp.database.StudyDatabase
import com.example.studyapp.repository.StudyRepository
import com.example.studyapp.screens.*
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.viewModel.StudyViewModel
import com.example.studyapp.viewModel.StudyViewModelFactory
import com.example.studyapp.reminders.ReminderScreen
import com.example.studyapp.timer.PomodoroViewModel
import com.example.studyapp.timer.PomodoroScreen
import com.example.studyapp.timer.PomodoroViewModelFactory
import com.example.studyapp.datastore.SettingsDataStore
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.studyapp.datastore.SettingsUiState

/**
 * MainActivity is the main entry point of the StudyBuddy.
 * It initializes the theme settings, handles runtime permissions,
 * manages navigation, and provides global access to ViewModels.
 */


class MainActivity : ComponentActivity() {
    /**
     * ViewModel for managing study sessions and app logic.
     * It uses a repository that abstracts Room database access.
     */
    private val viewModel: StudyViewModel by viewModels {
        val dao = StudyDatabase.getDatabase(applicationContext).sessionDao()
        val repository = StudyRepository(dao) //abstract the data layer
        //enables accessing room db methods while decoupling ViewModel from db implementation
        StudyViewModelFactory(application, repository)
        //building StudyViewModel with injected Application and Repository
    }

    /**
     * ViewModel for managing Pomodoro timer state.
     */
    val pomodoroViewModel: PomodoroViewModel by viewModels {
        PomodoroViewModelFactory(applicationContext)
    }

    /**
     * Sets up the UI, navigation, theme preferences, and notification permissions.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        val settingsFlow = SettingsDataStore.getSettings(applicationContext)
        //gets the preference for theme as flow

        setContent {
            val uiState by settingsFlow.collectAsState(initial = SettingsUiState())
            //collects the flow as state, so compose can observe changes

            //react to state changes in a coroutine-safe way
            LaunchedEffect(uiState.isDarkTheme) {
                //control global UI Settings
                AppCompatDelegate.setDefaultNightMode(
                    if (uiState.isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }

            StudyAppTheme(darkTheme = uiState.isDarkTheme) {
                val navController = rememberNavController()
                //remember navigation state across recompositions
                val context = this
                //saving the current activity context to show a Toast
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    //registers a permission launcher
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    Toast.makeText(
                        context,
                        if (isGranted) "Notification permission granted" else "Notification permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //side effect handler
                //unit : this block runs once, when composable first enters the compositions
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                //check if the permission is granted, using the activity context above
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            //launch the permission API
                        }
                    }
                }

                Scaffold(
                    bottomBar = { BottomBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                navController = navController,
                                viewModel = viewModel,
                                isDarkTheme = uiState.isDarkTheme,
                                onToggleTheme = {
                                    lifecycleScope.launch {
                                        SettingsDataStore.toggleDarkTheme(applicationContext)
                                    }
                                }
                            )
                        }

                        composable("log_session") {
                            LogSessionScreen(viewModel = viewModel) {
                                navController.popBackStack()
                            }
                        }
                        composable("loggedSessions") {
                            LoggedSessionsScreen(viewModel = viewModel, navController = navController)
                        }

                        //dynamic route
                        composable("editSession/{sessionId}") { backStackEntry ->
                            val sessionId = backStackEntry.arguments?.getString("sessionId")?.toIntOrNull() //?. if not null, ?: if null
                            sessionId?.let {
                                EditSessionScreen(it, viewModel, navController)
                            }
                        }
                        composable("analytics") {
                            AnalyticsScreen(viewModel = viewModel)
                        }
                        composable("pomodoro") {
                            PomodoroScreen(viewModel = pomodoroViewModel)
                        }
                        composable("reminders") {
                            ReminderScreen()
                        }
                        }

                    }
                }
            }
        }
    }

/**
 * Composable function that renders the bottom navigation bar of the app.
 *
 * @param navController The NavHostController used to handle navigation.
 */

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("pomodoro", "Timer", painterResource(R.drawable.baseline_timer_24) ),
        BottomNavItem("reminders", "Reminders", painterResource(R.drawable.baseline_notifications_24)),
        BottomNavItem("dashboard", "Home", painterResource(R.drawable.baseline_home_24)),
        BottomNavItem("analytics", "Analytics", painterResource(R.drawable.baseline_analytics_24))
    )

    NavigationBar {
        //observes the current navigation route reactively
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("dashboard")
                            //avoids stacking the same screen multiple times
                            launchSingleTop = true
                            //if the route is already at the top, it wont relaunch
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

/**
 * Represents an item in the bottom navigation bar.
 *
 * @property route The route string used for navigation.
 * @property label The text label displayed below the icon.
 * @property icon The painter resource used for the icon.
 */

data class BottomNavItem(val route: String, val label: String, val icon: Painter)
