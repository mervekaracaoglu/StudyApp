package com.example.studyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.screens.DashboardScreen
import com.example.studyapp.screens.LogSessionScreen
import com.example.studyapp.viewModel.StudyViewModel
import com.example.studyapp.viewModel.StudyViewModelFactory
import androidx.activity.viewModels
import com.example.studyapp.database.StudyDatabase
import com.example.studyapp.repository.StudyRepository


class MainActivity : ComponentActivity() {

    private val viewModel: StudyViewModel by viewModels {
        val dao = StudyDatabase.getDatabase(applicationContext).sessionDao()
        val repository = StudyRepository(dao)
        StudyViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StudyAppTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = { BottomBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            DashboardScreen(onLogSessionClick = {navController.navigate("log_session")})
                        }
                        composable("log_session"){
                            LogSessionScreen(
                                viewModel = viewModel,
                                onSessionSaved = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        /*
                        composable("tasks") { TaskListScreen() }
                        composable("reminders") { ReminderScreen() }
                        composable("settings") { SettingsScreen() }
                        composable("analytics"){ AnalyticsScreen() }

                         */
                    }
                }
            }
        }
    }
}
@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("home", "Home", Icons.Default.Home),
        BottomNavItem("tasks", "Tasks", Icons.Default.List),
        BottomNavItem("reminders", "Reminders", Icons.Default.Notifications),
        BottomNavItem("settings", "Settings", Icons.Default.Settings)
    )

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home")
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)


