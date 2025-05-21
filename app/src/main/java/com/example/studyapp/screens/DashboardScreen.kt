package com.example.studyapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.studyapp.database.StudySession
import com.example.studyapp.viewModel.StudyViewModel
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun DashboardScreen(navController: NavController, viewModel: StudyViewModel) {
    val sessions by viewModel.allSessions.collectAsState()
    val upcomingSessions = sessions.filter { session ->
        session.dueDate?.let { due ->
            val now = System.currentTimeMillis()
            val twoDaysFromNow = now + 2 * 24 * 60 * 60 * 1000 // 2 days in ms
            !session.isCompleted && due in now..twoDaysFromNow
        } ?: false
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0C2A))
            .padding(20.dp)
    ) {
        Text(
            text = "Welcome to StudyBuddy",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard(title = "Time Studied", value = "1h 30m")
            InfoCard(title = "Current Streak", value = "5 days")
        }

        Spacer(modifier = Modifier.height(24.dp))

        GoalProgressBar(current = 12, goal = 15)

        Spacer(modifier = Modifier.height(24.dp))


        ActionButton("Log a Session", Icons.Default.Add, onClick = { navController.navigate("log_session")} )
        ActionButton("Logged Sessions", Icons.Default.AccountBox, onClick = { navController.navigate("loggedSessions") })

        Spacer(modifier = Modifier.height(24.dp))

        if (upcomingSessions.isNotEmpty()) {
            Text(
                text = "Upcoming Tasks",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                upcomingSessions.forEach { session ->
                    Surface(
                        color = Color(0xFF1A1C3B),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = session.subject, color = Color.White)
                            session.dueDate?.let {
                                Text(
                                    text = "Due: ${formatTimestamp(it)}",
                                    color = Color.LightGray,
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
        modifier = Modifier
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1A1C3B)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = Color.LightGray, fontSize = 14.sp)
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GoalProgressBar(current: Int, goal: Int) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1A1C3B),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weekly Goal Progress", color = Color.LightGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = current / goal.toFloat(),
                color = Color.Cyan,
                trackColor = Color.DarkGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${current}h / ${goal}h",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ActionButton(label: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1A1C3B),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 8.dp)
            .clickable { onClick()}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Icon(icon, contentDescription = label, tint = Color(0xFF89CFF0))
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, color = Color.White, fontSize = 16.sp)
        }
    }
}


