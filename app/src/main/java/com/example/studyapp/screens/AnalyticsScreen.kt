package com.example.studyapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.studyapp.R
import com.example.studyapp.viewModel.StudyViewModel

/**
 * Composable screen displaying analytics about study activity.
 * It shows:
 * - Total and average study time
 * - Longest session
 * - Most used tags
 * - Time spent per subject (visualized as a bar chart)
 *
 * @param viewModel The [StudyViewModel] providing session data.
 */
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

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            stringResource(R.string.total_study_time, totalMinutes / 60, totalMinutes % 60),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            stringResource(R.string.average_session, avgMinutes),
            style = MaterialTheme.typography.bodyMedium
        )

        longest?.let {
            Text(
                stringResource(R.string.longest_session, it.subject, it.durationMinutes),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        if (tagCount.isNotEmpty()) {
            Text(stringResource(R.string.most_used_tags), style = MaterialTheme.typography.titleMedium)
            tagCount.entries.sortedByDescending { it.value }.forEach {
                Text("${it.key}: ${it.value}x")
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))


        if (subjectDurations.isNotEmpty()) {
            Text(stringResource(R.string.time_spent_per_subject), style = MaterialTheme.typography.titleMedium)
            BarChart(subjectDurations)
        }
    }
}
/**
 * Simple horizontal bar chart that visualizes time spent per subject.
 *
 * @param data A map where keys are subject names and values are total minutes.
 */
@Composable
fun BarChart(data: Map<String, Int>) {
    val maxVal = data.values.maxOrNull()?.takeIf { it > 0 } ?: 1
    val barWidth = 40.dp

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        data.entries.forEach { (label, value) ->
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Canvas(modifier = Modifier.height(100.dp).width(barWidth)) {
                    drawRect(
                        color = Color.Blue,
                        topLeft = Offset(0f, size.height - (value / maxVal.toFloat()) * size.height),
                        size = androidx.compose.ui.geometry.Size(size.width, (value / maxVal.toFloat()) * size.height)
                    )
                }
                Text(label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

