package com.example.studyapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.studyapp.R
import com.example.studyapp.viewModel.StudyViewModel
import androidx.compose.material.icons.automirrored.filled.List

@Composable
fun DashboardScreen(navController: NavController, viewModel: StudyViewModel, isDarkTheme: Boolean, onToggleTheme: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.loadTodayStudyMinutes()
    }

    val sessions by viewModel.allSessions.collectAsState()
    val upcomingSessions = sessions.filter { session ->
        session.dueDate?.let { due ->
            val now = System.currentTimeMillis()
            val twoDaysFromNow = now + 2 * 24 * 60 * 60 * 1000
            !session.isCompleted && due in now..twoDaysFromNow
        } == true
    }

    val minutesToday by viewModel.todayStudyMinutes.collectAsState()
    val formattedTime = remember(minutesToday) {
        val hours = minutesToday / 60
        val minutes = minutesToday % 60
        if (hours > 0) "$hours h $minutes m" else "$minutes m"
    }

    val currentStreak by viewModel.currentStreak.collectAsState()
    val weeklyStudyMinutes by viewModel.weeklyStudyMinutes.collectAsState()
    val weeklyGoalMinutes by viewModel.weeklyGoalMinutes.collectAsState()
    var isDarkTheme by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.welcome),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard(stringResource(R.string.time_studied), formattedTime)

            InfoCard(stringResource(R.string.current_streak), "$currentStreak days")

            Surface(
                modifier = Modifier.height(100.dp).width(100.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Dark", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GoalProgressBar(
            current = weeklyStudyMinutes,
            goal = weeklyGoalMinutes,
            onGoalChange = { viewModel.setWeeklyGoal(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ActionButton(stringResource(R.string.log_a_session), Icons.Default.Add) {
            navController.navigate("log_session")
        }
        ActionButton(stringResource(R.string.logged_sessions), Icons.AutoMirrored.Filled.List) {
            navController.navigate("loggedSessions")
        }

        if (upcomingSessions.isNotEmpty()) {
            Text(
                text = stringResource(R.string.upcoming_tasks),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                upcomingSessions.forEach { session ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = session.subject,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            session.dueDate?.let {
                                Text(
                                    text = stringResource(R.string.due_label, formatTimestamp(it)),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Surface(
        modifier = Modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionButton(label: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth().height(60.dp).padding(vertical = 8.dp).clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
        }
    }
}

@Composable
fun GoalProgressBar(
    current: Int,
    goal: Int,
    onGoalChange: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(goal.toFloat()) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.set_weekly_goal)) },
            text = {
                Column {
                    val hours = sliderValue.toInt() / 60
                    val minutes = sliderValue.toInt() % 60
                    Text("$hours h $minutes m")

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 30f..1000f,
                        steps = 10
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onGoalChange(sliderValue.toInt())
                    showDialog = false
                }) {
                    Text(stringResource(R.string.set_goal))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                sliderValue = goal.toFloat()
                showDialog = true
            }.padding(16.dp)
    ) {
        Text(stringResource(R.string.weekly_progress), color = MaterialTheme.colorScheme.onSurface)
        val progress = (current.toFloat() / goal).coerceAtMost(1f)

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(16.dp).padding(top = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )

        val currentHours = current / 60
        val currentMinutes = current % 60
        val goalHours = goal / 60
        val goalMinutes = goal % 60

        Text(
            "$currentHours h $currentMinutes m / $goalHours h $goalMinutes m",
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

