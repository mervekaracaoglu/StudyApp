package com.example.studyapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
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

@Composable
fun DashboardScreen(onLogSessionClick: () -> Unit = {}) {
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

        ActionButton("Log a Session", Icons.Default.Add, onClick = onLogSessionClick)
        ActionButton("View Analytics", Icons.Default.AccountBox)
        ActionButton("Map View", Icons.Default.LocationOn)
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
