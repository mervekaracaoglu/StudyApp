package com.example.studyapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studyapp.database.StudySession
import com.example.studyapp.viewModel.StudyViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyticsScreen(viewModel: StudyViewModel) {
    val sessions = viewModel.allSessions.collectAsState().value
    val totalMinutes = sessions.sumOf { it.durationMinutes }
    val avgMinutes = if (sessions.isNotEmpty()) totalMinutes / sessions.size else 0
    val longest = sessions.maxByOrNull { it.durationMinutes }

    val tagCount = sessions.mapNotNull { it.tag }.groupingBy { it }.eachCount()
    val subjectDurations = sessions.groupBy { it.subject }.mapValues { entry ->
        entry.value.sumOf { it.durationMinutes }
    }

    val sessionsByDay = sessions.groupBy {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp))
    }.mapValues { it.value.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Total Study Time: ${totalMinutes / 60}h ${totalMinutes % 60}m", style = MaterialTheme.typography.titleMedium)
        Text("Average Session: $avgMinutes minutes", style = MaterialTheme.typography.bodyMedium)

        longest?.let {
            Text("Longest Session: ${it.subject}, ${it.durationMinutes} min", style = MaterialTheme.typography.bodyMedium)
        }

        if (tagCount.isNotEmpty()) {
            Text("Most Used Tags:", style = MaterialTheme.typography.titleMedium)
            tagCount.entries.sortedByDescending { it.value }.forEach {
                Text("${it.key}: ${it.value}x")
            }
        }

        if (subjectDurations.isNotEmpty()) {
            Text("Time Spent per Subject:", style = MaterialTheme.typography.titleMedium)
            BarChart(subjectDurations)
        }

        if (sessionsByDay.isNotEmpty()) {
            Text("Study Sessions per Day:", style = MaterialTheme.typography.titleMedium)
            LineChart(sessionsByDay)
        }
    }
}

@Composable
fun BarChart(data: Map<String, Int>) {
    val maxVal = data.values.maxOrNull()?.takeIf { it > 0 } ?: 1
    val barWidth = 40.dp

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        data.entries.forEach { (label, value) ->
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Canvas(modifier = Modifier.height(100.dp).width(barWidth)) {
                    drawRect(
                        color = Color.Cyan,
                        topLeft = Offset(0f, size.height - (value / maxVal.toFloat()) * size.height),
                        size = androidx.compose.ui.geometry.Size(size.width, (value / maxVal.toFloat()) * size.height)
                    )
                }
                Text(label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun LineChart(data: Map<String, Int>) {
    val entries = data.toSortedMap().entries.toList()
    if (entries.size < 2) {
        Text("Not enough data to render line chart")
        return
    }

    val maxY = (entries.maxOf { it.value }).coerceAtLeast(1)
    val points = entries.mapIndexed { i, entry ->
        val x = i.toFloat() / (entries.size - 1)
        val y = 1f - (entry.value / maxY.toFloat())
        Offset(x, y)
    }

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(vertical = 8.dp)
    ) {
        val w = size.width
        val h = size.height

        // Draw lines between points
        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color.Magenta,
                start = Offset(points[i].x * w, points[i].y * h),
                end = Offset(points[i + 1].x * w, points[i + 1].y * h),
                strokeWidth = 4f
            )
        }

        // Draw data points
        points.forEach { point ->
            drawCircle(
                color = Color.Red,
                radius = 6f,
                center = Offset(point.x * w, point.y * h)
            )
        }
    }

    // Optionally show x-axis labels below the chart
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        entries.forEachIndexed { index, entry ->
            Text(
                text = entry.key.takeLast(5), // show short date like "05-21"
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

