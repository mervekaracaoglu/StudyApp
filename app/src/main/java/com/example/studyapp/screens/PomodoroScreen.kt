package com.example.studyapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PomodoroScreen(viewModel: PomodoroViewModel = viewModel()) {
    val state by viewModel.timerState.collectAsState()

    var workInput by remember { mutableStateOf("25") }
    var breakInput by remember { mutableStateOf("5") }
    var longBreakInput by remember { mutableStateOf("15") }

    Column(Modifier.padding(16.dp)) {
        Text("Set durations (minutes):")
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = workInput, onValueChange = { workInput = it }, label = { Text("Work") })
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(value = breakInput, onValueChange = { breakInput = it }, label = { Text("Break") })
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(value = longBreakInput, onValueChange = { longBreakInput = it }, label = { Text("Long Break") })
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = formatTime(state.remainingTime),
            fontSize = 48.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                val workDuration = workInput.toLongOrNull()?.times(60_000) ?: 25 * 60_000L
                viewModel.startTimer(workDuration, PomodoroMode.WORK)
            }) {
                Text("Start Work")
            }
            Button(onClick = { viewModel.stopTimer() }) {
                Text("Stop")
            }
            Button(onClick = { viewModel.resetTimer() }) {
                Text("Reset")
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val minutes = (ms / 1000) / 60
    val seconds = (ms / 1000) % 60
    return "%02d:%02d".format(minutes, seconds)
}
